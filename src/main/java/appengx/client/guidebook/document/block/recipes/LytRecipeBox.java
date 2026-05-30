package appengx.client.guidebook.document.block.recipes;

import net.minecraft.world.item.crafting.Recipe;

import appengx.client.guidebook.document.block.LytBox;
import appengx.siteexport.ExportableResourceProvider;
import appengx.siteexport.ResourceExporter;

public abstract class LytRecipeBox extends LytBox implements ExportableResourceProvider {
    private final Recipe<?> recipe;

    public LytRecipeBox(Recipe<?> recipe) {
        this.recipe = recipe;
    }

    @Override
    public void exportResources(ResourceExporter exporter) {
        exporter.referenceRecipe(this.recipe);
    }
}
