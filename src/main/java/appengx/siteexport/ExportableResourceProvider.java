package appengx.siteexport;

import appengx.client.guidebook.document.block.LytNode;

public interface ExportableResourceProvider {
    default void exportResources(ResourceExporter exporter) {
    }

    static void visit(LytNode node, ResourceExporter exporter) {
        if (node instanceof ExportableResourceProvider provider) {
            provider.exportResources(exporter);
        }
        for (var child : node.getChildren()) {
            visit(child, exporter);
        }
    }
}
