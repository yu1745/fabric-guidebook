package appengx.guidebook.item;

import appengx.guidebook.api.Guidebooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public final class GuideBookItem extends Item {
    public GuideBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        var stack = user.getItemInHand(hand);
        if (world.isClientSide()) {
            if (stack.hasTag() && stack.getTag().contains(Guidebooks.GUIDE_ID_NBT)) {
                Guidebooks.openForItem(stack);
            } else {
                user.displayClientMessage(Component.translatable("item.fabric_guidebook.guide.missing_guide"), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }
}
