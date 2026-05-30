package appengx.client.guidebook.scene.element;

import java.util.Set;

import appengx.client.guidebook.compiler.PageCompiler;
import appengx.client.guidebook.document.LytErrorSink;
import appengx.client.guidebook.extensions.Extension;
import appengx.client.guidebook.extensions.ExtensionPoint;
import appengx.client.guidebook.scene.GuidebookScene;
import appengx.libs.mdast.mdx.model.MdxJsxElementFields;

/**
 * Contributed by {@link SceneElementCompilerPlugin}.
 */
public interface SceneElementTagCompiler extends Extension {
    ExtensionPoint<SceneElementTagCompiler> EXTENSION_POINT = new ExtensionPoint<>(SceneElementTagCompiler.class);

    Set<String> getTagNames();

    void compile(GuidebookScene scene, PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el);
}
