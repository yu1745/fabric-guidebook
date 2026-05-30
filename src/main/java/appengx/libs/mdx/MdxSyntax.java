package appengx.libs.mdx;

import java.util.Collections;
import java.util.List;

import appengx.libs.micromark.Extension;
import appengx.libs.micromark.symbol.Codes;

public class MdxSyntax {

    public static final Extension INSTANCE = new Extension();

    static {
        INSTANCE.flow.put(Codes.lessThan, List.of(JsxFlow.INSTANCE));
        INSTANCE.text.put(Codes.lessThan, List.of(JsxText.INSTANCE));

        // See https://github.com/micromark/micromark-extension-mdx-md/blob/main/index.js
        Collections.addAll(
                INSTANCE.nullDisable,
                "autolink", "codeIndented", "htmlFlow", "htmlText");
    }

}
