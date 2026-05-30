package appengx.client.guidebook;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.resources.ResourceLocation;

import appengx.client.guidebook.compiler.PageCompiler;
import appengx.client.guidebook.compiler.ParsedGuidePage;

class GuideSourceWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuideSourceWatcher.class);

    /**
     * The {@link ResourceLocation} namespace to use for files in the watched folder.
     */
    private final String namespace;
    /**
     * The ID of the resource pack to use as the source pack.
     */
    private final String sourcePackId;

    private final Path sourceFolder;

    // Recursive directory watcher for the guidebook sources.
    @Nullable
    private WatchService sourceWatcher;
    private final Map<WatchKey, Path> watchedDirectories = new HashMap<>();

    // Queued changes that come in from a separate thread
    private final Map<ResourceLocation, ParsedGuidePage> changedPages = new HashMap<>();
    private final Set<ResourceLocation> deletedPages = new HashSet<>();
    private boolean assetsChanged;

    private final ExecutorService watchExecutor;

    public GuideSourceWatcher(String namespace, Path sourceFolder) {
        this.namespace = namespace;
        // The namespace does not necessarily *need* to be a mod id, but if it is, the source pack needs to
        // follow the specific mod-id format. Otherwise we assume it's a resource pack where namespace == pack id,
        // which is also not 100% correct.
        this.sourcePackId = FabricLoaderImpl.INSTANCE.isModLoaded(namespace) ? "mod:" + namespace : namespace;
        this.sourceFolder = sourceFolder;
        if (!Files.isDirectory(sourceFolder)) {
            throw new RuntimeException("Cannot find the specified folder for the AE2 guidebook sources: "
                    + sourceFolder);
        }
        LOGGER.info("Watching guidebook sources in {}", sourceFolder);

        watchExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("AE2GuidebookWatcher%d")
                .build());

        // Watch the folder recursively in a separate thread, queue up any changes and apply them
        // in the client tick.
        WatchService watcher;
        try {
            watcher = sourceFolder.getFileSystem().newWatchService();
            sourceWatcher = watcher;
            registerDirectoryTree(sourceFolder);
        } catch (IOException e) {
            LOGGER.error("Failed to watch for changes in the guidebook sources at {}", sourceFolder, e);
            watcher = null;
            sourceWatcher = null;
        }

        // Actually process changes in the client tick to prevent race conditions and other crashes
        if (sourceWatcher != null) {
            watchExecutor.submit(this::watchLoop);
        }
    }

    public List<ParsedGuidePage> loadAll() {
        var stopwatch = Stopwatch.createStarted();

        // Find all potential pages
        var pagesToLoad = new HashMap<ResourceLocation, Path>();
        try {
            Files.walkFileTree(sourceFolder, new FileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    var pageId = getPageId(file);
                    if (pageId != null) {
                        pagesToLoad.put(pageId, file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    LOGGER.error("Failed to list page {}", file, exc);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    if (exc != null) {
                        LOGGER.error("Failed to list all pages in {}", dir, exc);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to list all pages in {}", sourceFolder, e);
        }

        LOGGER.info("Loading {} guidebook pages", pagesToLoad.size());
        var loadedPages = pagesToLoad.entrySet()
                .stream()
                .map(entry -> {
                    var path = entry.getValue();
                    try {
                        var pageContent = Files.readString(path, StandardCharsets.UTF_8);
                        return PageCompiler.parse(sourcePackId, entry.getKey(), pageContent);

                    } catch (Exception e) {
                        LOGGER.error("Failed to reload guidebook page {}", path, e);
                        try {
                            var pageContent = Files.readString(path, StandardCharsets.UTF_8);
                            return PageCompiler.parseError(sourcePackId, entry.getKey(), pageContent, e);
                        } catch (Exception errorPageFailure) {
                            LOGGER.error("Failed to create error page for {}", path, errorPageFailure);
                            return null;
                        }
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        LOGGER.info("Loaded {} pages from {} in {}", loadedPages.size(), sourceFolder, stopwatch);

        return loadedPages;
    }

    public synchronized List<GuidePageChange> takeChanges() {

        if (deletedPages.isEmpty() && changedPages.isEmpty()) {
            return List.of();
        }

        var changes = new ArrayList<GuidePageChange>();

        for (var deletedPage : deletedPages) {
            changes.add(new GuidePageChange(deletedPage, null, null));
        }
        deletedPages.clear();

        for (var changedPage : changedPages.values()) {
            changes.add(new GuidePageChange(changedPage.getId(), null, changedPage));
        }
        changedPages.clear();

        return changes;
    }

    public synchronized boolean takeAssetsChanged() {
        var result = assetsChanged;
        assetsChanged = false;
        return result;
    }

    public synchronized void close() {
        changedPages.clear();
        deletedPages.clear();
        watchedDirectories.clear();
        watchExecutor.shutdownNow();

        if (sourceWatcher != null) {
            try {
                sourceWatcher.close();
            } catch (IOException e) {
                LOGGER.error("Failed to close fileystem watcher for {}", sourceFolder);
            }
        }
    }

    private void watchLoop() {
        while (!Thread.currentThread().isInterrupted() && sourceWatcher != null) {
            WatchKey key;
            try {
                key = sourceWatcher.take();
            } catch (ClosedWatchServiceException e) {
                return;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            Path directory;
            synchronized (this) {
                directory = watchedDirectories.get(key);
            }
            if (directory == null) {
                key.reset();
                continue;
            }

            for (var event : key.pollEvents()) {
                var kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    synchronized (this) {
                        assetsChanged = true;
                    }
                    continue;
                }

                var changedPath = directory.resolve((Path) event.context());
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    if (Files.isDirectory(changedPath)) {
                        try {
                            registerDirectoryTree(changedPath);
                        } catch (IOException e) {
                            LOGGER.error("Failed to watch new guidebook source folder {}", changedPath, e);
                        }
                        synchronized (this) {
                            assetsChanged = true;
                        }
                    } else {
                        pageChanged(changedPath);
                    }
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    pageChanged(changedPath);
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    pageDeleted(changedPath);
                }
            }

            var valid = key.reset();
            if (!valid) {
                synchronized (this) {
                    watchedDirectories.remove(key);
                }
            }
        }
    }

    private void registerDirectoryTree(Path root) throws IOException {
        Files.walkFileTree(root, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                LOGGER.error("Failed to list guidebook source folder {}", file, exc);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                if (exc != null) {
                    LOGGER.error("Failed to list guidebook source folder {}", dir, exc);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private synchronized void registerDirectory(Path directory) throws IOException {
        if (sourceWatcher == null) {
            return;
        }
        var key = directory.register(sourceWatcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
        watchedDirectories.put(key, directory);
    }

    // Only call while holding the lock!
    private synchronized void pageChanged(Path path) {
        var pageId = getPageId(path);
        if (pageId == null) {
            assetsChanged = true;
            return; // Probably not a page
        }

        // If it was previously deleted in the same change-set, undelete it
        deletedPages.remove(pageId);

        try {
            var pageContent = Files.readString(path, StandardCharsets.UTF_8);
            var page = PageCompiler.parse(sourcePackId, pageId, pageContent);
            changedPages.put(pageId, page);
        } catch (Exception e) {
            LOGGER.error("Failed to reload guidebook page {}", path, e);
            try {
                var pageContent = Files.readString(path, StandardCharsets.UTF_8);
                changedPages.put(pageId, PageCompiler.parseError(sourcePackId, pageId, pageContent, e));
            } catch (Exception errorPageFailure) {
                LOGGER.error("Failed to create error page for {}", path, errorPageFailure);
            }
        }
    }

    // Only call while holding the lock!
    private synchronized void pageDeleted(Path path) {
        var pageId = getPageId(path);
        if (pageId == null) {
            assetsChanged = true;
            return; // Probably not a page
        }

        // If it was previously changed in the same change-set, remove the change
        changedPages.remove(pageId);
        deletedPages.add(pageId);
    }

    @Nullable
    private ResourceLocation getPageId(Path path) {
        var relativePath = sourceFolder.relativize(path);
        var relativePathStr = relativePath.toString().replace('\\', '/');
        if (!relativePathStr.endsWith(".md")) {
            return null;
        }
        if (!ResourceLocation.isValidResourceLocation(relativePathStr)) {
            return null;
        }
        return new ResourceLocation(namespace, relativePathStr);
    }
}
