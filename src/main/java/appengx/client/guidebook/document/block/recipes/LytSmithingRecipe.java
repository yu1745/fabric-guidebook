package appengx.client.guidebook.document.block.recipes;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.block.Blocks;

import appengx.client.guidebook.document.DefaultStyles;
import appengx.client.guidebook.document.LytRect;
import appengx.client.guidebook.document.block.LytSlot;
import appengx.client.guidebook.document.block.LytSlotGrid;
import appengx.client.guidebook.layout.LayoutContext;
import appengx.client.guidebook.render.RenderContext;
import appengx.core.AppEng;
import appengx.util.Platform;

public class LytSmithingRecipe extends LytRecipeBox {
    private static final ResourceLocation ARROW_LIGHT = AppEng.makeId("ae2guide/gui/recipe_arrow_light.png");

    private final LytSlotGrid inputGrid;

    private final LytSlot resultSlot;

    public LytSmithingRecipe(SmithingRecipe recipe) {
        super(recipe);
        setPadding(5);
        paddingTop = 15;

        append(inputGrid = LytSlotGrid.row(getIngredients(recipe), true));
        append(resultSlot = new LytSlot(recipe.getResultItem(Platform.getClientRegistryAccess())));
    }

    private static List<Ingredient> getIngredients(SmithingRecipe recipe) {
        return recipe.getIngredients();
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
                y,
                availableWidth);
        return LytRect.union(inputBounds, resultBounds);
    }

    @Override
    public void render(RenderContext context) {
        context.renderPanel(getBounds());

        context.renderItem(
                Blocks.SMITHING_TABLE.asItem().getDefaultInstance(),
                bounds.x() + paddingLeft,
                bounds.y() + 4,
                8,
                8);
        context.renderText(
                Items.SMITHING_TABLE.getDescription().getString(),
                DefaultStyles.CRAFTING_RECIPE_TYPE.mergeWith(DefaultStyles.BASE_STYLE),
                bounds.x() + paddingLeft + 10,
                bounds.y() + 4);

        context.fillTexturedRect(
                new LytRect(bounds.right() - 25 - 24, bounds.y() + 10 + (bounds.height() - 27) / 2, 24, 17),
                ARROW_LIGHT);

        super.render(context);
    }
}
