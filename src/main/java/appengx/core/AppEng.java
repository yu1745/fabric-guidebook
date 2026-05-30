package appengx.core;

import appengx.guidebook.FabricGuidebook;
import net.minecraft.resources.ResourceLocation;

public final class AppEng {
    public static final String MOD_ID = FabricGuidebook.MOD_ID;

    private AppEng() {
    }

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(FabricGuidebook.MOD_ID, path);
    }
}
