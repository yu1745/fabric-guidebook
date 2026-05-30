package appengx.guidebook.api;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

public final class Guide {
    private final ResourceLocation id;
    private final String folder;
    private final String title;
    private final String landingPage;
    private final appengx.client.guidebook.Guide ae2Guide;

    private Guide(Builder builder) {
        this.id = builder.id;
        this.folder = builder.folder;
        this.title = builder.title;
        this.landingPage = builder.landingPage;
        this.ae2Guide = appengx.client.guidebook.Guide.builder(id.getNamespace(), folder).build();
    }

    public static Builder builder(ResourceLocation id) {
        return new Builder(id);
    }

    public ResourceLocation id() {
        return id;
    }

    public String folder() {
        return folder;
    }

    public String title() {
        return title;
    }

    public String landingPage() {
        return landingPage;
    }

    @Nullable
    public appengx.client.guidebook.Guide ae2Guide() {
        return ae2Guide;
    }

    public static String normalizePagePath(String path) {
        var normalized = path.replace('\\', '/');
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    public static final class Builder {
        private final ResourceLocation id;
        private String folder = "guidebook/main";
        private String title;
        private String landingPage = "index.md";

        private Builder(ResourceLocation id) {
            this.id = Objects.requireNonNull(id, "id");
            this.title = id.toString();
        }

        public Builder folder(String folder) {
            this.folder = Guide.normalizePagePath(Objects.requireNonNull(folder, "folder"));
            return this;
        }

        public Builder title(String title) {
            this.title = Objects.requireNonNull(title, "title");
            return this;
        }

        public Builder landingPage(String landingPage) {
            this.landingPage = Guide.normalizePagePath(Objects.requireNonNull(landingPage, "landingPage"));
            return this;
        }

        public Guide build() {
            return new Guide(this);
        }
    }
}
