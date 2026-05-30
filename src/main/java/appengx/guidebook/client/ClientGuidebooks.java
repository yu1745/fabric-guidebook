package appengx.guidebook.client;

import appengx.guidebook.api.Guidebooks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import appengx.client.guidebook.PageAnchor;
import appengx.client.guidebook.screen.GlobalInMemoryHistory;
import appengx.client.guidebook.screen.GuideScreen;

public final class ClientGuidebooks {
    private ClientGuidebooks() {
    }

    public static void open(ResourceLocation guideId, String page) {
        var client = Minecraft.getInstance();
        client.execute(() -> Guidebooks.get(guideId).ifPresentOrElse(
                guide -> client.setScreen(GuideScreen.openNew(
                        guide.ae2Guide(),
                        PageAnchor.page(new ResourceLocation(guideId.getNamespace(), page)),
                        GlobalInMemoryHistory.INSTANCE)),
                () -> {
                    if (client.player != null) {
                        client.player.displayClientMessage(
                                Component.translatable("message.fabric_guidebook.unknown_guide", guideId),
                                false);
                    }
                }));
    }
}
