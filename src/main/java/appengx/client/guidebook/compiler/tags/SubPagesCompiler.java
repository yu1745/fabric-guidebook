package appengx.client.guidebook.compiler.tags;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;

import appengx.client.guidebook.PageAnchor;
import appengx.client.guidebook.compiler.PageCompiler;
import appengx.client.guidebook.document.block.LytBlock;
import appengx.client.guidebook.document.block.LytBlockContainer;
import appengx.client.guidebook.document.block.LytList;
import appengx.client.guidebook.document.block.LytListItem;
import appengx.client.guidebook.document.block.LytParagraph;
import appengx.client.guidebook.document.flow.LytFlowLink;
import appengx.client.guidebook.navigation.NavigationNode;
import appengx.libs.mdast.mdx.model.MdxJsxElementFields;

public class SubPagesCompiler extends BlockTagCompiler {
    private static final Comparator<NavigationNode> ALPHABETICAL_COMPARATOR = Comparator
            .comparing(NavigationNode::title);

    @Override
    public Set<String> getTagNames() {
        return Set.of("SubPages");
    }

    @Override
    protected void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields el) {
        var pageIdStr = el.getAttributeString("id", null);
        var alphabetical = MdxAttrs.getBoolean(compiler, parent, el, "alphabetical", false);

        var navigationTree = compiler.getPageCollection().getNavigationTree();

        // Find the page in the tree, if it's explicitly set to empty, show the root nav
        List<NavigationNode> subNodes;
        if ("".equals(pageIdStr)) {
            subNodes = navigationTree.getRootNodes();
        } else {
            ResourceLocation pageId;
            try {
                pageId = pageIdStr == null ? compiler.getPageId() : compiler.resolveId(pageIdStr);
            } catch (Exception e) {
                parent.appendError(compiler, "Invalid id", el);
                return;
            }

            var node = navigationTree.getNodeById(pageId);
            if (node == null) {
                parent.appendError(compiler, "Couldn't find page " + pageId + " in the navigation tree", el);
                return; // Not found in navigation tree
            }

            subNodes = node.children();
        }

        if (alphabetical) {
            subNodes = new ArrayList<>(subNodes);
            subNodes.sort(ALPHABETICAL_COMPARATOR);
        }

        var list = new LytList(false, 0);
        for (var childNode : subNodes) {
            if (!childNode.hasPage()) {
                continue;
            }

            var listItem = new LytListItem();
            var listItemPar = new LytParagraph();

            var link = new LytFlowLink();
            link.setClickCallback(guideScreen -> guideScreen.navigateTo(PageAnchor.page(childNode.pageId())));
            link.appendText(childNode.title());
            listItemPar.append(link);

            LytBlock listItemBlock = listItemPar;

            listItem.append(listItemBlock);
            list.append(listItem);
        }
        parent.append(list);
    }
}
