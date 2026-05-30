package appengx.client.guidebook.scene.annotation;

import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import appengx.client.guidebook.color.ConstantColor;
import appengx.client.guidebook.compiler.PageCompiler;
import appengx.client.guidebook.compiler.tags.MdxAttrs;
import appengx.client.guidebook.document.LytErrorSink;
import appengx.libs.mdast.mdx.model.MdxJsxElementFields;

public class DiamondAnnotationElementCompiler extends AnnotationTagCompiler {
    @Override
    public Set<String> getTagNames() {
        return Set.of("DiamondAnnotation");
    }

    @Override
    protected @Nullable SceneAnnotation createAnnotation(PageCompiler compiler, LytErrorSink errorSink,
            MdxJsxElementFields el) {
        var pos = MdxAttrs.getVector3(compiler, errorSink, el, "pos", new Vector3f());
        var color = MdxAttrs.getColor(compiler, errorSink, el, "color", ConstantColor.WHITE);

        return new DiamondAnnotation(pos, color);
    }
}
