package appengx.client.guidebook.scene;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import appengx.client.guidebook.extensions.Extension;
import appengx.client.guidebook.extensions.ExtensionPoint;
import appengx.client.guidebook.scene.annotation.SceneAnnotation;
import appengx.client.guidebook.scene.level.GuidebookLevel;

/**
 * Provides a way to generate a {@link appengx.client.guidebook.scene.annotation.SceneAnnotation} on the fly if no
 * explicit annotation could be found under the mouse.
 */
public interface ImplicitAnnotationStrategy extends Extension {
    ExtensionPoint<ImplicitAnnotationStrategy> EXTENSION_POINT = new ExtensionPoint<>(ImplicitAnnotationStrategy.class);

    @Nullable
    SceneAnnotation getAnnotation(GuidebookLevel level, BlockState blockState, BlockHitResult blockHitResult);
}
