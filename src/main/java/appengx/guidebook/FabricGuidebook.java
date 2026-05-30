package appengx.guidebook;

import appengx.guidebook.item.GuidebookItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;

public final class FabricGuidebook implements ModInitializer {
    public static final String MOD_ID = "fabric_guidebook";

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        GuidebookItems.register();
    }
}
