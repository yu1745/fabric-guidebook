package appengx.guidebook;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import appengx.guidebook.api.Guidebooks;
import appengx.guidebook.item.GuidebookItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class GuideStarterBooks {
    private static final String GUIDE_DEFINITIONS_FOLDER = "guidebook_guides";
    private static final Map<ResourceLocation, StarterGuide> STARTER_GUIDES = new LinkedHashMap<>();

    private GuideStarterBooks() {
    }

    public static void register() {
        discoverGuides();
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> giveMissingGuides(handler.player));
    }

    private static void discoverGuides() {
        STARTER_GUIDES.clear();

        for (var mod : FabricLoader.getInstance().getAllMods()) {
            for (var root : mod.getRootPaths()) {
                discoverGuides(root);
            }
        }
    }

    private static void discoverGuides(Path root) {
        var assetsRoot = root.resolve("assets");
        if (!Files.isDirectory(assetsRoot)) {
            return;
        }

        try (var namespaces = Files.list(assetsRoot)) {
            namespaces
                    .filter(Files::isDirectory)
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .forEach(GuideStarterBooks::discoverNamespaceGuides);
        } catch (IOException ignored) {
        }
    }

    private static void discoverNamespaceGuides(Path namespaceRoot) {
        var namespace = namespaceRoot.getFileName().toString();
        var guideRoot = namespaceRoot.resolve(GUIDE_DEFINITIONS_FOLDER);
        if (!Files.isDirectory(guideRoot)) {
            return;
        }

        try (var guides = Files.list(guideRoot)) {
            guides
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .forEach(path -> readGuide(namespace, path));
        } catch (IOException ignored) {
        }
    }

    private static void readGuide(String namespace, Path path) {
        var fileName = path.getFileName().toString();
        var guideName = fileName.substring(0, fileName.length() - ".json".length());
        var guideId = ResourceLocation.tryParse(namespace + ":" + guideName);
        if (guideId == null) {
            return;
        }

        try (var reader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {
            var json = JsonParser.parseReader(reader).getAsJsonObject();
            if (!readBoolean(json, "starter_item", true)) {
                return;
            }
            var title = readString(json, "title", defaultModName(namespace, guideId.toString()));
            var titleKey = readString(json, "title_key", "");
            STARTER_GUIDES.putIfAbsent(guideId, new StarterGuide(guideId, title, titleKey));
        } catch (Exception ignored) {
        }
    }

    private static void giveMissingGuides(ServerPlayer player) {
        for (var guide : STARTER_GUIDES.values()) {
            var tag = givenTag(guide.id());
            if (player.getTags().contains(tag)) {
                continue;
            }

            player.addTag(tag);
            var stack = createGuideStack(guide);
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
    }

    private static ItemStack createGuideStack(StarterGuide guide) {
        var stack = new ItemStack(GuidebookItems.GUIDE);
        var tag = new CompoundTag();
        tag.putString(Guidebooks.GUIDE_ID_NBT, guide.id().toString());
        stack.setTag(tag);
        stack.setHoverName(guide.titleComponent());
        return stack;
    }

    private static String givenTag(ResourceLocation guideId) {
        return "fabric_guidebook.given." + guideId.getNamespace() + "." + guideId.getPath().replace('/', '.');
    }

    private static String readString(JsonObject json, String key, String fallback) {
        return json.has(key) ? json.get(key).getAsString() : fallback;
    }

    private static boolean readBoolean(JsonObject json, String key, boolean fallback) {
        return json.has(key) ? json.get(key).getAsBoolean() : fallback;
    }

    private static String defaultModName(String namespace, String fallback) {
        return FabricLoader.getInstance()
                .getModContainer(namespace)
                .map(container -> container.getMetadata())
                .map(ModMetadata::getName)
                .orElse(fallback);
    }

    private record StarterGuide(ResourceLocation id, String title, String titleKey) {
        Component titleComponent() {
            if (!titleKey.isBlank()) {
                return Component.translatable(titleKey);
            }
            var modName = Component.translatableWithFallback("modmenu.nameTranslation." + id.getNamespace(), title);
            return Component.translatableWithFallback("item.fabric_guidebook.starter_guide", "%s Guide", modName);
        }
    }
}
