package appengx.guidebook.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

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
        openHandler.open(guideId, Guide.normalizePagePath(page));
    }

    public static void openForItem(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(GUIDE_ID_NBT)) {
            return;
        }
        var id = ResourceLocation.tryParse(stack.getTag().getString(GUIDE_ID_NBT));
        if (id != null) {
            open(id, get(id).map(Guide::landingPage).orElse("index.md"));
        }
    }

    public static void setOpenHandler(OpenHandler handler) {
        openHandler = handler;
    }

    @FunctionalInterface
    public interface OpenHandler {
        OpenHandler NOOP = (guideId, page) -> {
        };

        void open(ResourceLocation guideId, String page);
    }
}
