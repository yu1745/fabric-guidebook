package appengx.client.guidebook.document;

import appengx.client.guidebook.compiler.PageCompiler;
import appengx.libs.unist.UnistNode;

public interface LytErrorSink {
    void appendError(PageCompiler compiler, String text, UnistNode node);
}
