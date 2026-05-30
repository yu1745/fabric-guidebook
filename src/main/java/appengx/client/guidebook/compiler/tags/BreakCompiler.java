package appengx.client.guidebook.compiler.tags;

import java.util.Set;

import appengx.client.guidebook.compiler.PageCompiler;
import appengx.client.guidebook.document.flow.LytFlowBreak;
import appengx.client.guidebook.document.flow.LytFlowParent;
import appengx.libs.mdast.mdx.model.MdxJsxElementFields;
import appengx.libs.mdast.model.MdAstNode;

public class BreakCompiler extends FlowTagCompiler {
    @Override
    public Set<String> getTagNames() {
        return Set.of("br");
    }

    @Override
    protected void compile(PageCompiler compiler, LytFlowParent parent, MdxJsxElementFields el) {
        var br = new LytFlowBreak();
        var clear = el.getAttributeString("clear", "none");
        switch (clear) {
            case "left" -> br.setClearLeft(true);
            case "right" -> br.setClearRight(true);
            case "all" -> {
                br.setClearLeft(true);
                br.setClearRight(true);
            }
            case "none" -> {
            }
            default -> parent.append(compiler.createErrorFlowContent("Invalid 'clear' attribute", (MdAstNode) el));
        }

        parent.append(br);
    }
}
