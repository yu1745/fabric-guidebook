package appengx.client.guidebook.document.block;

import net.minecraft.client.renderer.MultiBufferSource;

import appengx.client.guidebook.color.SymbolicColor;
import appengx.client.guidebook.document.LytRect;
import appengx.client.guidebook.layout.LayoutContext;
import appengx.client.guidebook.render.RenderContext;

public class LytThematicBreak extends LytBlock {
    @Override
    public LytRect computeLayout(LayoutContext context, int x, int y, int availableWidth) {
        return new LytRect(x, y, availableWidth, 6);
    }

    @Override
    protected void onLayoutMoved(int deltaX, int deltaY) {
    }

    @Override
    public void renderBatch(RenderContext context, MultiBufferSource buffers) {
    }

    @Override
    public void render(RenderContext context) {
        var line = bounds.withHeight(2).centerVerticallyIn(bounds);

        context.fillRect(line, SymbolicColor.THEMATIC_BREAK);
    }
}
