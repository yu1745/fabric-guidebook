package appengx.guidebook.client;

import appengx.client.guidebook.command.GuidebookStructureCommands;
import appengx.client.guidebook.command.GuidebookStructureSelection;
import appengx.guidebook.api.Guidebooks;
import appengx.guidebook.FabricGuidebook;
import appengx.guidebook.api.Guide;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public final class FabricGuidebookClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Guidebooks.register(Guide.builder(FabricGuidebook.id("main"))
                .title("Fabric Guidebook")
                .folder("guidebook/main")
                .landingPage("index.md")
                .build());
        Guidebooks.setOpenHandler(ClientGuidebooks::open);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new GuideResourceReloader());
        OpenGuideHotkey.init();
        GuidebookDevCommands.register();
        GuidebookStructureSelection.registerClient();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> GuidebookStructureCommands.register(
                server.getCommands().getDispatcher()));
    }
}
