package appengx.client.guidebook.document.block.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;

import appengx.client.guidebook.document.DefaultStyles;
import appengx.client.guidebook.document.LytRect;
import appengx.client.guidebook.document.block.LytSlot;
import appengx.client.guidebook.document.block.LytSlotGrid;
import appengx.client.guidebook.layout.LayoutContext;
import appengx.client.guidebook.render.RenderContext;
import appengx.core.AppEng;
import appengx.util.Platform;

public class LytGenericRecipe extends LytRecipeBox {
    private static final ResourceLocation ARROW_LIGHT = AppEng.makeId("ae2guide/gui/recipe_arrow_light.png");

    private final Recipe<?> recipe;
    private final LytSlotGrid inputGrid;
    private final LytSlot resultSlot;

    public LytGenericRecipe(Recipe<?> recipe) {
        super(recipe);
        this.recipe = recipe;
        setPadding(5);
        paddingTop = 15;

        append(inputGrid = createInputGrid(recipe.getIngredients()));
        append(resultSlot = new LytSlot(recipe.getResultItem(Platform.getClientRegistryAccess())));
    }

    private static LytSlotGrid createInputGrid(List<Ingredient> ingredients) {
        var nonEmptyIngredients = new ArrayList<Ingredient>();
        for (var ingredient : ingredients) {
            if (!ingredient.isEmpty()) {
                nonEmptyIngredients.add(ingredient);
            }
        }

        if (nonEmptyIngredients.isEmpty()) {
            var grid = new LytSlotGrid(1, 1);
            grid.setRenderEmptySlots(false);
            return grid;
        }

        var width = Math.min(3, nonEmptyIngredients.size());
        var height = (nonEmptyIngredients.size() + width - 1) / width;
        var grid = new LytSlotGrid(width, height);
        for (int i = 0; i < nonEmptyIngredients.size(); i++) {
            grid.setIngredient(i % width, i / width, nonEmptyIngredients.get(i));
        }
        return grid;
    }

    @Override
    protected LytRect computeBoxLayout(LayoutContext context, int x, int y, int availableWidth) {
        var inputBounds = inputGrid.layout(
                context,
                x,
                y,
                availableWidth);

        var resultBounds = resultSlot.layout(
                context,
                inputBounds.right() + 28,
                Math.max(y, inputBounds.y() + (inputBounds.height() - 18) / 2),
                availableWidth);
        return LytRect.union(inputBounds, resultBounds);
    }

    @Override
    public void render(RenderContext context) {
        context.renderPanel(getBounds());

        context.renderItem(
                Blocks.CRAFTING_TABLE.asItem().getDefaultInstance(),
                bounds.x() + paddingLeft,
                bounds.y() + 4,
                8,
                8);
        context.renderText(
                getRecipeTypeName(),
                DefaultStyles.CRAFTING_RECIPE_TYPE.mergeWith(DefaultStyles.BASE_STYLE),
                bounds.x() + paddingLeft + 10,
                bounds.y() + 4);

        context.fillTexturedRect(
                new LytRect(bounds.right() - 25 - 24, bounds.y() + 10 + (bounds.height() - 27) / 2, 24, 17),
                ARROW_LIGHT);

        super.render(context);
    }

    private String getRecipeTypeName() {
        var id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
        if (id == null) {
            return "Recipe";
        }
        return id.toString();
    }
}
