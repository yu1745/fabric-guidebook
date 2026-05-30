package appengx.libs.micromark.extensions;

import appengx.libs.micromark.Token;
import appengx.libs.micromark.html.HtmlContext;
import appengx.libs.micromark.html.HtmlExtension;

public final class YamlFrontmatterHtml {

    public static final HtmlExtension INSTANCE = HtmlExtension.builder()
            .enter("yaml", YamlFrontmatterHtml::enter)
            .exit("yaml", YamlFrontmatterHtml::exit)
            .build();

    private static void enter(HtmlContext context, Token token) {
        context.buffer();
    }

    private static void exit(HtmlContext context, Token token) {
        context.resume();
        context.setSlurpOneLineEnding(true);
    }

}
