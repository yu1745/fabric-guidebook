package appengx.siteexport;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public interface ResourceExporter {
    default void referenceItem(ItemStack stack) {
    }

    default void referenceRecipe(Recipe<?> recipe) {
    }
}
