package appengx.client.guidebook.layout;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import appengx.client.guidebook.style.ResolvedTextStyle;

public class MinecraftFontMetrics implements FontMetrics {
    private final Font font;

    public MinecraftFontMetrics() {
        this(Minecraft.getInstance().font);
    }

    public MinecraftFontMetrics(Font font) {
        this.font = font;
    }

    public float getAdvance(int codePoint, ResolvedTextStyle style) {
        return font.width(new String(Character.toChars(codePoint))) * style.fontScale();
    }

    public int getLineHeight(ResolvedTextStyle style) {
        return (int) Math.ceil(font.lineHeight * style.fontScale());
    }
}
