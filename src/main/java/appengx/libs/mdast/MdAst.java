package appengx.libs.mdast;

import appengx.libs.mdast.model.MdAstRoot;
import appengx.libs.micromark.Micromark;

public final class MdAst {
    private MdAst() {
    }

    public static MdAstRoot fromMarkdown(String markdown, MdastOptions options) {
        var evts = Micromark.parseAndPostprocess(markdown, options);
        return new MdastCompiler(options).compile(evts);
    }
}
