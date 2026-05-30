package appengx.guidebook.client;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import appengx.guidebook.FabricGuidebook;
import appengx.guidebook.api.Guide;
import appengx.guidebook.api.Guidebooks;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;

public final class GuideResourceReloader implements SimpleSynchronousResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return FabricGuidebook.id("guides");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        var guides = new ArrayList<Guide>();
        var definitions = manager.listResources("guidebook_guides", id -> id.getPath().endsWith(".json"));
        definitions.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                .forEach(entry -> readGuide(manager, entry.getKey()).ifPresent(guides::add));
        Guidebooks.replaceAll(guides);
    }

    private static java.util.Optional<Guide> readGuide(ResourceManager manager, ResourceLocation definitionId) {
        try (var reader = new InputStreamReader(manager.getResource(definitionId).orElseThrow().open(),
                StandardCharsets.UTF_8)) {
            var json = JsonParser.parseReader(reader).getAsJsonObject();
            var guidePath = definitionId.getPath();
            var fileName = guidePath.substring(guidePath.lastIndexOf('/') + 1, guidePath.length() - ".json".length());
            var guideId = new ResourceLocation(definitionId.getNamespace(), fileName);
            var guide = Guide.builder(guideId)
                    .title(readString(json, "title", guideId.toString()))
                    .folder(readString(json, "folder", "guidebook/" + fileName))
                    .landingPage(readString(json, "landing_page", "index.md"))
                    .build();

            return java.util.Optional.of(guide);
        } catch (Exception e) {
            FabricGuidebookLogger.error("Failed to load guide definition {}", definitionId, e);
            return java.util.Optional.empty();
        }
    }

    private static String readString(JsonObject json, String key, String fallback) {
        return json.has(key) ? json.get(key).getAsString() : fallback;
    }
}
