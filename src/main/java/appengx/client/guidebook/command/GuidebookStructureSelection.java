package appengx.client.guidebook.command;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

@Environment(EnvType.CLIENT)
public final class GuidebookStructureSelection {
    static final String TOOL_TAG = "FabricGuidebookStructureTool";

    @Nullable
    private static BlockPos first;
    @Nullable
    private static BlockPos second;
    @Nullable
    private static ResourceKey<Level> dimension;

    private GuidebookStructureSelection() {
    }

    public static void registerClient() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!isSelectionTool(player.getItemInHand(hand))) {
                return InteractionResult.PASS;
            }
            if (!world.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            setFirst(pos);
            player.displayClientMessage(Component.literal("Guidebook structure start: " + format(pos))
                    .withStyle(ChatFormatting.AQUA), true);
            return InteractionResult.SUCCESS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!isSelectionTool(player.getItemInHand(hand))) {
                return InteractionResult.PASS;
            }
            if (!world.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            setSecond(hitResult.getBlockPos());
            player.displayClientMessage(Component.literal("Guidebook structure end: " + format(hitResult.getBlockPos()))
                    .withStyle(ChatFormatting.AQUA), true);
            return InteractionResult.SUCCESS;
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            if (first == null || second == null || context.consumers() == null) {
                return;
            }

            var client = Minecraft.getInstance();
            if (client.level == null || dimension != client.level.dimension()) {
                return;
            }

            var minX = Math.min(first.getX(), second.getX());
            var minY = Math.min(first.getY(), second.getY());
            var minZ = Math.min(first.getZ(), second.getZ());
            var maxX = Math.max(first.getX(), second.getX()) + 1;
            var maxY = Math.max(first.getY(), second.getY()) + 1;
            var maxZ = Math.max(first.getZ(), second.getZ()) + 1;
            var camera = context.camera().getPosition();
            var box = new AABB(minX, minY, minZ, maxX, maxY, maxZ)
                    .move(-camera.x, -camera.y, -camera.z);

            PoseStack poseStack = context.matrixStack();
            var lines = context.consumers().getBuffer(RenderType.lines());
            LevelRenderer.renderLineBox(poseStack, lines, box, 0.2f, 0.85f, 1.0f, 1.0f);
        });
    }

    public static boolean isSelectionTool(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(TOOL_TAG);
    }

    public static void clear() {
        first = null;
        second = null;
        dimension = null;
    }

    public static void setFirst(BlockPos pos) {
        first = pos.immutable();
        updateDimension();
    }

    public static void setSecond(BlockPos pos) {
        second = pos.immutable();
        updateDimension();
    }

    @Nullable
    public static SelectedRegion getSelectedRegion(Level level) {
        if (first == null || second == null || dimension != level.dimension()) {
            return null;
        }

        var minX = Math.min(first.getX(), second.getX());
        var minY = Math.min(first.getY(), second.getY());
        var minZ = Math.min(first.getZ(), second.getZ());
        var maxX = Math.max(first.getX(), second.getX());
        var maxY = Math.max(first.getY(), second.getY());
        var maxZ = Math.max(first.getZ(), second.getZ());

        var origin = new BlockPos(minX, minY, minZ);
        var size = new BlockPos(1 + maxX - minX, 1 + maxY - minY, 1 + maxZ - minZ);
        return new SelectedRegion(origin, size);
    }

    private static void updateDimension() {
        var level = Minecraft.getInstance().level;
        dimension = level != null ? level.dimension() : null;
    }

    private static String format(BlockPos pos) {
        return pos.getX() + " " + pos.getY() + " " + pos.getZ();
    }

    public record SelectedRegion(BlockPos origin, BlockPos size) {
    }
}
