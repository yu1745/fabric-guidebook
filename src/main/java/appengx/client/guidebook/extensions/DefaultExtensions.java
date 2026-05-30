package appengx.client.guidebook.extensions;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import appengx.client.guidebook.compiler.TagCompiler;
import appengx.client.guidebook.compiler.tags.ATagCompiler;
import appengx.client.guidebook.compiler.tags.BoxFlowDirection;
import appengx.client.guidebook.compiler.tags.BoxTagCompiler;
import appengx.client.guidebook.compiler.tags.BreakCompiler;
import appengx.client.guidebook.compiler.tags.CategoryIndexCompiler;
import appengx.client.guidebook.compiler.tags.DivTagCompiler;
import appengx.client.guidebook.compiler.tags.FloatingImageCompiler;
import appengx.client.guidebook.compiler.tags.ItemGridCompiler;
import appengx.client.guidebook.compiler.tags.ItemLinkCompiler;
import appengx.client.guidebook.compiler.tags.RecipeCompiler;
import appengx.client.guidebook.compiler.tags.SubPagesCompiler;
import appengx.client.guidebook.scene.BlockImageTagCompiler;
import appengx.client.guidebook.scene.ItemImageTagCompiler;
import appengx.client.guidebook.scene.SceneTagCompiler;
import appengx.client.guidebook.scene.annotation.BlockAnnotationElementCompiler;
import appengx.client.guidebook.scene.annotation.BoxAnnotationElementCompiler;
import appengx.client.guidebook.scene.annotation.DiamondAnnotationElementCompiler;
import appengx.client.guidebook.scene.annotation.LineAnnotationElementCompiler;
import appengx.client.guidebook.scene.element.ImportStructureElementCompiler;
import appengx.client.guidebook.scene.element.IsometricCameraElementCompiler;
import appengx.client.guidebook.scene.element.SceneBlockElementCompiler;
import appengx.client.guidebook.scene.element.SceneElementTagCompiler;

public final class DefaultExtensions {
    private static final List<Registration<?>> EXTENSIONS = List.of(
            new Registration<>(TagCompiler.EXTENSION_POINT, DefaultExtensions::tagCompilers),
            new Registration<>(SceneElementTagCompiler.EXTENSION_POINT, DefaultExtensions::sceneElementTagCompilers));

    private DefaultExtensions() {
    }

    public static void addAll(ExtensionCollection.Builder builder, Set<ExtensionPoint<?>> disabledExtensionPoints) {
        for (var registration : EXTENSIONS) {
            add(builder, disabledExtensionPoints, registration);
        }
    }

    private static <T extends Extension> void add(ExtensionCollection.Builder builder,
            Set<ExtensionPoint<?>> disabledExtensionPoints, Registration<T> registration) {
        if (disabledExtensionPoints.contains(registration.extensionPoint)) {
            return;
        }

        for (var extension : registration.factory.get()) {
            builder.add(registration.extensionPoint, extension);
        }
    }

    private static List<TagCompiler> tagCompilers() {
        return List.of(
                new DivTagCompiler(),
                new ATagCompiler(),
                new ItemLinkCompiler(),
                new FloatingImageCompiler(),
                new BreakCompiler(),
                new ItemGridCompiler(),
                new CategoryIndexCompiler(),
                new BlockImageTagCompiler(),
                new ItemImageTagCompiler(),
                new RecipeCompiler(),
                new BoxTagCompiler(BoxFlowDirection.ROW),
                new BoxTagCompiler(BoxFlowDirection.COLUMN),
                new SceneTagCompiler(),
                new SubPagesCompiler());
    }

    private static List<SceneElementTagCompiler> sceneElementTagCompilers() {
        return List.of(
                new SceneBlockElementCompiler(),
                new ImportStructureElementCompiler(),
                new IsometricCameraElementCompiler(),
                new BlockAnnotationElementCompiler(),
                new BoxAnnotationElementCompiler(),
                new LineAnnotationElementCompiler(),
                new DiamondAnnotationElementCompiler());
    }

    private record Registration<T extends Extension> (ExtensionPoint<T> extensionPoint,
            Supplier<Collection<T>> factory) {
    }
}
