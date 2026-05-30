package appengx.api.stacks;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;

public record AEFluidKey(Fluid fluid, @Nullable CompoundTag tag) {
    public static AEFluidKey of(Fluid fluid, @Nullable CompoundTag tag) {
        return new AEFluidKey(fluid, tag);
    }

    public FluidVariant toVariant() {
        return FluidVariant.of(fluid, tag);
    }
}
