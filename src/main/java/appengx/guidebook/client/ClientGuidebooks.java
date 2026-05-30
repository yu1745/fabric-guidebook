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

    public static void open(ResourceLocation guideId, PageAnchor page) {
        var client = Minecraft.getInstance();
        client.tell(() -> Guidebooks.get(guideId).ifPresentOrElse(
                guide -> {
                    guide.ae2Guide().reloadDevelopmentSourcesNow();
                    client.setScreen(GuideScreen.openNew(
                            guide.ae2Guide(),
                            page,
                            GlobalInMemoryHistory.INSTANCE));
                },
                () -> {
                    if (client.player != null) {
                        client.player.displayClientMessage(
                                Component.translatable("message.fabric_guidebook.unknown_guide", guideId),
                                false);
                    }
                }));
    }
}
