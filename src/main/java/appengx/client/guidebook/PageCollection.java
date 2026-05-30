package appengx.client.guidebook;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import appengx.client.guidebook.compiler.ParsedGuidePage;
import appengx.client.guidebook.indices.PageIndex;
import appengx.client.guidebook.navigation.NavigationTree;

public interface PageCollection {
    <T extends PageIndex> T getIndex(Class<T> indexClass);

    @Nullable
    ParsedGuidePage getParsedPage(ResourceLocation id);

    @Nullable
    GuidePage getPage(ResourceLocation id);

    byte @Nullable [] loadAsset(ResourceLocation id);

    NavigationTree getNavigationTree();

    boolean pageExists(ResourceLocation pageId);
}
