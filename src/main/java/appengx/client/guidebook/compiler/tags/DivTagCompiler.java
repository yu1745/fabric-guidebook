package appengx.client.guidebook.compiler.tags;

import java.util.Set;

import appengx.client.guidebook.compiler.PageCompiler;
import appengx.client.guidebook.compiler.TagCompiler;
import appengx.client.guidebook.document.block.LytBlockContainer;
import appengx.libs.mdast.mdx.model.MdxJsxFlowElement;

public class DivTagCompiler implements TagCompiler {
    @Override
    public Set<String> getTagNames() {
        return Set.of("div");
    }

    @Override
    public void compileBlockContext(PageCompiler compiler, LytBlockContainer parent, MdxJsxFlowElement el) {
        compiler.compileBlockContext(el, parent);
    }
}
