package appengx.client.guidebook.compiler.tags;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import appengx.client.guidebook.compiler.PageCompiler;
import appengx.client.guidebook.document.block.LytBlock;
import appengx.client.guidebook.document.block.LytBlockContainer;
import appengx.client.guidebook.document.block.recipes.LytCraftingRecipe;
import appengx.client.guidebook.document.block.recipes.LytGenericRecipe;
import appengx.client.guidebook.document.block.recipes.LytSmeltingRecipe;
import appengx.client.guidebook.document.block.recipes.LytSmithingRecipe;
import appengx.libs.mdast.mdx.model.MdxJsxElementFields;
import appengx.libs.mdast.model.MdAstNode;
import appengx.util.Platform;

/**
 * Shows a recipe-book-like representation of the recipe needed to craft a given item.
 */
public class RecipeCompiler extends BlockTagCompiler {
    private final List<RecipeTypeMapping<?, ?>> mappings = List.of(
            new RecipeTypeMapping<>(RecipeType.CRAFTING, LytCraftingRecipe::new),
            new RecipeTypeMapping<>(RecipeType.SMELTING, LytSmeltingRecipe::new),
            new RecipeTypeMapping<>(RecipeType.SMITHING, LytSmithingRecipe::new));

    @Override
    public Set<String> getTagNames() {
        return Set.of("Recipe", "RecipeFor");
    }

    @Override
    protected void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields el) {
        var recipeManager = Platform.getClientRecipeManager();
        if (recipeManager == null) {
            parent.appendError(compiler, "Cannot show recipe while not in-game", el);
            return;
        }

        if ("RecipeFor".equals(el.name())) {
            var itemAndId = MdxAttrs.getRequiredItemAndId(compiler, parent, el, "id");
            if (itemAndId == null) {
                return;
            }

            var id = itemAndId.getLeft();
            var item = itemAndId.getRight();

            for (var mapping : mappings) {
                var block = mapping.tryCreate(recipeManager, item);
                if (block != null) {
                    block.setSourceNode((MdAstNode) el);
                    parent.append(block);
                    return;
                }
            }

            var fallbackBlock = tryCreateFallback(recipeManager, item);
            if (fallbackBlock != null) {
                fallbackBlock.setSourceNode((MdAstNode) el);
                parent.append(fallbackBlock);
                return;
            }

            parent.appendError(compiler, "Couldn't find recipe for " + id, el);
        } else {
            var recipeId = MdxAttrs.getRequiredId(compiler, parent, el, "id");
            if (recipeId == null) {
                return;
            }

            var recipe = recipeManager.byKey(recipeId).orElse(null);
            if (recipe == null) {
                parent.appendError(compiler, "Couldn't find recipe " + recipeId, el);
                return;
            }

            for (var mapping : mappings) {
                var block = mapping.tryCreate(recipe);
                if (block != null) {
                    block.setSourceNode((MdAstNode) el);
                    parent.append(block);
                    return;
                }
            }

            var block = new LytGenericRecipe(recipe);
            block.setSourceNode((MdAstNode) el);
            parent.append(block);
        }
    }

    @Nullable
    private LytBlock tryCreateFallback(RecipeManager recipeManager, Item resultItem) {
        for (var recipe : recipeManager.getRecipes()) {
            try {
                if (recipe.getResultItem(Platform.getClientRegistryAccess()).getItem() == resultItem) {
                    return new LytGenericRecipe(recipe);
                }
            } catch (Exception ignored) {
                // Some recipes may not expose a normal result stack.
            }
        }
        return null;
    }

    private record RecipeTypeMapping<T extends Recipe<C>, C extends Container>(
            RecipeType<T> recipeType,
            Function<T, LytBlock> factory) {
        @Nullable
        LytBlock tryCreate(RecipeManager recipeManager, Item resultItem) {
            for (var recipe : recipeManager.getAllRecipesFor(recipeType)) {
                try {
                    if (recipe.getResultItem(Platform.getClientRegistryAccess()).getItem() == resultItem) {
                        return factory.apply(recipe);
                    }
                } catch (Exception ignored) {
                    // Some recipes require registry access or may be disabled by datapacks.
                }
            }

            return null;
        }

        @SuppressWarnings("unchecked")
        @Nullable
        LytBlock tryCreate(Recipe<?> recipe) {
            if (recipeType == recipe.getType()) {
                return factory.apply((T) recipe);
            }

            return null;
        }
    }
}
