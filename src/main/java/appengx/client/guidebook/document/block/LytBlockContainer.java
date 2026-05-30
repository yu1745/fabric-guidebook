package appengx.client.guidebook.document.block;

import appengx.client.guidebook.compiler.PageCompiler;
import appengx.client.guidebook.document.LytErrorSink;
import appengx.libs.unist.UnistNode;

public interface LytBlockContainer extends LytErrorSink {
    void append(LytBlock node);

    @Override
    default void appendError(PageCompiler compiler, String text, UnistNode node) {
        append(compiler.createErrorBlock(text, node));
    }
}
