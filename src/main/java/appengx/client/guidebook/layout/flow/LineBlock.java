package appengx.client.guidebook.layout.flow;

import net.minecraft.client.renderer.MultiBufferSource;

import appengx.client.guidebook.document.block.LytBlock;
import appengx.client.guidebook.render.RenderContext;

/**
 * Standalone block in-line with other content.
 */
public class LineBlock extends LineElement {

    private final LytBlock block;

    public LineBlock(LytBlock block) {
        this.block = block;
    }

    public LytBlock getBlock() {
        return block;
    }

    @Override
    public void renderBatch(RenderContext context, MultiBufferSource buffers) {
        block.renderBatch(context, buffers);
    }

    @Override
    public void render(RenderContext context) {
        block.render(context);
    }
}
