package appengx.guidebook.client;

import appengx.guidebook.api.Guidebooks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public final class FabricGuidebookClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Guidebooks.setOpenHandler(ClientGuidebooks::open);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new GuideResourceReloader());
    }
}
