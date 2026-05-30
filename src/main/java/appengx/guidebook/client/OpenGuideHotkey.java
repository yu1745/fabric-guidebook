package appengx.guidebook.client;

import java.util.List;
import java.util.Objects;

import com.google.common.base.Strings;
import com.mojang.blaze3d.platform.InputConstants;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import appengx.client.guidebook.GuidebookText;
import appengx.client.guidebook.PageAnchor;
import appengx.client.guidebook.screen.GuideScreen;
import appengx.guidebook.FabricGuidebook;
import appengx.guidebook.api.Guidebooks;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class OpenGuideHotkey {
    private static final KeyMapping OPEN_GUIDE_MAPPING = new KeyMapping(
            "key.fabric_guidebook.open_guide", GLFW.GLFW_KEY_G, "key.fabric_guidebook.category");
    private static final int TICKS_TO_OPEN = 10;
    private static final ResourceLocation TOOLTIP_PHASE = FabricGuidebook.id("open_guide_for_item");

    private static boolean newTick = true;
    private static ResourceLocation previousItemId;
    @Nullable
    private static Guidebooks.IndexedPage indexedPage;
    private static int ticksKeyHeld;
    private static boolean holding;

    private OpenGuideHotkey() {
    }

    public static void init() {
        KeyBindingHelper.registerKeyBinding(OPEN_GUIDE_MAPPING);
        ItemTooltipCallback.EVENT.register(TOOLTIP_PHASE, OpenGuideHotkey::handleTooltip);
        ItemTooltipCallback.EVENT.addPhaseOrdering(Event.DEFAULT_PHASE, TOOLTIP_PHASE);
        ClientTickEvents.START_CLIENT_TICK.register(client -> newTick = true);
    }

    private static void handleTooltip(ItemStack itemStack, TooltipFlag tooltipFlag, List<Component> lines) {
        if (OPEN_GUIDE_MAPPING.isUnbound()) {
            holding = false;
            ticksKeyHeld = 0;
            return;
        }

        if (newTick) {
            newTick = false;
            update(itemStack);
        }

        if (indexedPage == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        PageAnchor page = indexedPage.anchor();
        if (minecraft.screen instanceof GuideScreen guideScreen
                && guideScreen.getCurrentPageId().equals(page.pageId())) {
            return;
        }

        float progress = ticksKeyHeld;
        if (holding) {
            progress += minecraft.getFrameTime();
        } else {
            progress -= minecraft.getFrameTime();
        }
        progress /= (float) TICKS_TO_OPEN;

        var component = makeProgressBar(Mth.clamp(progress, 0, 1));
        if (lines.isEmpty()) {
            lines.add(component);
        } else {
            lines.add(1, component);
        }
    }

    private static Component makeProgressBar(float progress) {
        var minecraft = Minecraft.getInstance();
        var holdText = Component.empty()
                .append(GuidebookText.HoldToShow
                        .text(OPEN_GUIDE_MAPPING.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GRAY)))
                .withStyle(ChatFormatting.DARK_GRAY);

        var charWidth = minecraft.font.width("|");
        var tipWidth = minecraft.font.width(holdText);
        var total = Math.max(1, tipWidth / charWidth);
        var current = (int) (progress * total);

        if (progress > 0) {
            var result = Component.literal(Strings.repeat("|", current)).withStyle(ChatFormatting.GRAY);
            if (progress < 1) {
                result = result.append(
                        Component.literal(Strings.repeat("|", total - current)).withStyle(ChatFormatting.DARK_GRAY));
            }
            return result;
        }

        return holdText;
    }

    private static void update(ItemStack itemStack) {
        var itemId = itemStack.getItemHolder()
                .unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);

        if (!Objects.equals(itemId, previousItemId)) {
            previousItemId = itemId;
            indexedPage = null;
            ticksKeyHeld = 0;

            if (itemId == null) {
                return;
            }

            indexedPage = Guidebooks.findIndexedPage(itemStack).orElse(null);
        }

        holding = isKeyHeld();
        if (holding) {
            if (ticksKeyHeld < TICKS_TO_OPEN && ++ticksKeyHeld == TICKS_TO_OPEN) {
                if (indexedPage != null) {
                    if (Minecraft.getInstance().screen instanceof GuideScreen guideScreen) {
                        guideScreen.navigateTo(indexedPage.anchor());
                    } else {
                        Guidebooks.open(indexedPage.guideId(), indexedPage.anchor());
                    }
                    ticksKeyHeld = 0;
                    holding = false;
                }
            } else if (ticksKeyHeld > TICKS_TO_OPEN) {
                ticksKeyHeld = TICKS_TO_OPEN;
            }
        } else {
            ticksKeyHeld = Math.max(0, ticksKeyHeld - 2);
        }
    }

    private static boolean isKeyHeld() {
        var boundKey = KeyBindingHelper.getBoundKeyOf(OPEN_GUIDE_MAPPING);
        int keyCode = boundKey.getValue();
        var window = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(window, keyCode);
    }
}
