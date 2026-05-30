package appengx.guidebook.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import appengx.client.guidebook.PageAnchor;
import appengx.client.guidebook.indices.ItemIndex;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public final class Guidebooks {
    public static final String GUIDE_ID_NBT = "GuideId";

    private static final Map<ResourceLocation, Guide> GUIDES = new LinkedHashMap<>();
    private static OpenHandler openHandler = OpenHandler.NOOP;

    private Guidebooks() {
    }

    public static void register(Guide guide) {
        GUIDES.put(guide.id(), guide);
    }

    public static void replaceAll(Collection<Guide> guides) {
        GUIDES.clear();
        for (var guide : guides) {
            register(guide);
        }
    }

    public static Optional<Guide> get(ResourceLocation id) {
        return Optional.ofNullable(GUIDES.get(id));
    }

    public static Collection<Guide> all() {
        return GUIDES.values();
    }

    public static void open(ResourceLocation guideId, String page) {
        open(guideId, PageAnchor.page(new ResourceLocation(guideId.getNamespace(), Guide.normalizePagePath(page))));
    }

    public static void open(ResourceLocation guideId, PageAnchor anchor) {
        openHandler.open(guideId, anchor);
    }

    public static void openForItem(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(GUIDE_ID_NBT)) {
            openIndexedItem(stack);
            return;
        }
        var id = ResourceLocation.tryParse(stack.getTag().getString(GUIDE_ID_NBT));
        if (id != null) {
            open(id, get(id).map(Guide::landingPage).orElse("index.md"));
        }
    }

    private static void openIndexedItem(ItemStack stack) {
        var itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        for (var guide : GUIDES.values()) {
            var anchor = guide.ae2Guide().getIndex(ItemIndex.class).get(itemId);
            if (anchor != null) {
                open(guide.id(), anchor);
                return;
            }
        }
    }

    public static void setOpenHandler(OpenHandler handler) {
        openHandler = handler;
    }

    @FunctionalInterface
    public interface OpenHandler {
        OpenHandler NOOP = (guideId, page) -> {
        };

        void open(ResourceLocation guideId, PageAnchor page);
    }
}
