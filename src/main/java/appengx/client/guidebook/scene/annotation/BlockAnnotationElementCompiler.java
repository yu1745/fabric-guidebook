package appengx.client.guidebook.scene.annotation;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

import appengx.client.guidebook.color.ConstantColor;
import appengx.client.guidebook.compiler.PageCompiler;
import appengx.client.guidebook.compiler.tags.MdxAttrs;
import appengx.client.guidebook.document.LytErrorSink;
import appengx.libs.mdast.mdx.model.MdxJsxElementFields;

public class BlockAnnotationElementCompiler extends AnnotationTagCompiler {

    public static final String TAG_NAME = "BlockAnnotation";

    @Override
    public Set<String> getTagNames() {
        return Set.of(TAG_NAME);
    }

    @Override
    protected @Nullable SceneAnnotation createAnnotation(PageCompiler compiler, LytErrorSink errorSink,
            MdxJsxElementFields el) {
        var pos = MdxAttrs.getPos(compiler, errorSink, el);
        var color = MdxAttrs.getColor(compiler, errorSink, el, "color", ConstantColor.WHITE);

        return InWorldBoxAnnotation.forBlock(pos, color);
    }
}
