package appengx.siteexport;

import net.minecraft.world.item.ItemStack;

public interface ResourceExporter {
    default void referenceItem(ItemStack stack) {
    }
}
