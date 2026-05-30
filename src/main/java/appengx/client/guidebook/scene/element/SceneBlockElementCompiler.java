package appengx.client.guidebook.scene.element;

import java.util.Set;

import appengx.client.guidebook.compiler.PageCompiler;
import appengx.client.guidebook.compiler.tags.MdxAttrs;
import appengx.client.guidebook.document.LytErrorSink;
import appengx.client.guidebook.scene.GuidebookScene;
import appengx.libs.mdast.mdx.model.MdxJsxElementFields;

public class SceneBlockElementCompiler implements SceneElementTagCompiler {
    @Override
    public Set<String> getTagNames() {
        return Set.of("Block");
    }

    @Override
    public void compile(GuidebookScene scene,
            PageCompiler compiler,
            LytErrorSink errorSink,
            MdxJsxElementFields el) {
        var pair = MdxAttrs.getRequiredBlockAndId(compiler, errorSink, el, "id");
        if (pair == null) {
            return;
        }
        var state = pair.getRight().defaultBlockState();
        state = MdxAttrs.applyBlockStateProperties(compiler, errorSink, el, state);

        var pos = MdxAttrs.getPos(compiler, errorSink, el);
        scene.getLevel().setBlockAndUpdate(pos, state);
    }
}
