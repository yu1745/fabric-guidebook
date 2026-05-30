package appengx.client.guidebook.layout;

import appengx.client.guidebook.style.ResolvedTextStyle;

public interface FontMetrics {
    float getAdvance(int codePoint, ResolvedTextStyle style);

    int getLineHeight(ResolvedTextStyle style);
}
