package appengx.libs.mdx;

import appengx.libs.micromark.Assert;
import appengx.libs.micromark.CharUtil;
import appengx.libs.micromark.Construct;
import appengx.libs.micromark.State;
import appengx.libs.micromark.TokenizeContext;
import appengx.libs.micromark.Tokenizer;
import appengx.libs.micromark.Types;
import appengx.libs.micromark.factory.FactorySpace;
import appengx.libs.micromark.symbol.Codes;

final class JsxFlow {

    public static final Construct INSTANCE = new Construct();

    static {
        INSTANCE.tokenize = JsxFlow::tokenize;
        INSTANCE.concrete = true;
    }

    private static State tokenize(TokenizeContext context, Tokenizer.Effects effects, State ok, State nok) {
        class StateMachine {
            State start(int code) {
                Assert.check(code == Codes.lessThan, "expected `<`");
                return FactoryTag.create(
                        context,
                        effects,
                        FactorySpace.create(effects, this::after, Types.whitespace),
                        nok,
                        false,
                        "mdxJsxFlowTag",
                        "mdxJsxFlowTagMarker",
                        "mdxJsxFlowTagClosingMarker",
                        "mdxJsxFlowTagSelfClosingMarker",
                        "mdxJsxFlowTagName",
                        "mdxJsxFlowTagNamePrimary",
                        "mdxJsxFlowTagNameMemberMarker",
                        "mdxJsxFlowTagNameMember",
                        "mdxJsxFlowTagNamePrefixMarker",
                        "mdxJsxFlowTagNameLocal",
                        "mdxJsxFlowTagExpressionAttribute",
                        "mdxJsxFlowTagExpressionAttributeMarker",
                        "mdxJsxFlowTagExpressionAttributeValue",
                        "mdxJsxFlowTagAttribute",
                        "mdxJsxFlowTagAttributeName",
                        "mdxJsxFlowTagAttributeNamePrimary",
                        "mdxJsxFlowTagAttributeNamePrefixMarker",
                        "mdxJsxFlowTagAttributeNameLocal",
                        "mdxJsxFlowTagAttributeInitializerMarker",
                        "mdxJsxFlowTagAttributeValueLiteral",
                        "mdxJsxFlowTagAttributeValueLiteralMarker",
                        "mdxJsxFlowTagAttributeValueLiteralValue",
                        "mdxJsxFlowTagAttributeValueExpression",
                        "mdxJsxFlowTagAttributeValueExpressionMarker",
                        "mdxJsxFlowTagAttributeValueExpressionValue").step(code);
            }

            State after(int code) {
                // Another tag.
                return code == Codes.lessThan
                        ? start(code)
                        : code == Codes.eof || CharUtil.markdownLineEnding(code)
                                ? ok.step(code)
                                : nok.step(code);
            }
        }

        return new StateMachine()::start;
    }

}
