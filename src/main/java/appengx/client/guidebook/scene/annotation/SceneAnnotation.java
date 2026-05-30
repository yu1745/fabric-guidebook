package appengx.client.guidebook.scene.annotation;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;

import appengx.client.guidebook.document.block.LytBlock;
import appengx.client.guidebook.document.interaction.ContentTooltip;
import appengx.client.guidebook.document.interaction.GuideTooltip;
import appengx.client.guidebook.document.interaction.TextTooltip;
import appengx.siteexport.ExportableResourceProvider;
import appengx.siteexport.ResourceExporter;

/**
 * An annotation to show additional information to the user about content in a
 * {@link appengx.client.guidebook.scene.GuidebookScene}.
 */
public abstract class SceneAnnotation implements ExportableResourceProvider {
    @Nullable
    private GuideTooltip tooltip;

    private boolean hovered;

    public @Nullable GuideTooltip getTooltip() {
        return tooltip;
    }

    public void setTooltip(@Nullable GuideTooltip tooltip) {
        this.tooltip = tooltip;
    }

    public void setTooltipContent(LytBlock block) {
        this.tooltip = new ContentTooltip(block);
    }

    public void setTooltipContent(Component component) {
        this.tooltip = new TextTooltip(component);
    }

    public boolean hasTooltip() {
        return tooltip != null;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    @Override
    public void exportResources(ResourceExporter exporter) {
        if (tooltip != null) {
            tooltip.exportResources(exporter);
        }
    }
}
