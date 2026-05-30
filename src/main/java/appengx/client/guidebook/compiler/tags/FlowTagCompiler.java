package appengx.client.guidebook.compiler.tags;

import appengx.client.guidebook.compiler.PageCompiler;
import appengx.client.guidebook.compiler.TagCompiler;
import appengx.client.guidebook.document.block.LytBlockContainer;
import appengx.client.guidebook.document.block.LytParagraph;
import appengx.client.guidebook.document.flow.LytFlowParent;
import appengx.libs.mdast.mdx.model.MdxJsxElementFields;
import appengx.libs.mdast.mdx.model.MdxJsxFlowElement;
import appengx.libs.mdast.mdx.model.MdxJsxTextElement;

/**
 * Compiler base-class for tag compilers that compile flow content but allow the flow content to be used in block
 * context by wrapping it in a paragraph.
 */
public abstract class FlowTagCompiler implements TagCompiler {
    protected abstract void compile(PageCompiler compiler, LytFlowParent parent, MdxJsxElementFields el);

    @Override
    public void compileFlowContext(PageCompiler compiler, LytFlowParent parent, MdxJsxTextElement el) {
        compile(compiler, parent, el);
    }

    @Override
    public final void compileBlockContext(PageCompiler compiler, LytBlockContainer parent, MdxJsxFlowElement el) {
        var paragraph = new LytParagraph();
        compile(compiler, paragraph, el);
        parent.append(paragraph);
    }
}
