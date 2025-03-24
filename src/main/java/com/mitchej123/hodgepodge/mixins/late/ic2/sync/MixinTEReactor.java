package com.mitchej123.hodgepodge.mixins.late.ic2.sync;

import static java.lang.Math.floorMod;

import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TileEntityNuclearReactorElectric.class, remap = false)
public abstract class MixinTEReactor {
    @Shadow public abstract World getWorld();

    @Redirect(method = "updateEntityServer", at = @At(value = "FIELD", target = "Lic2/core/block/reactor/tileentity/TileEntityNuclearReactorElectric;updateTicker:I", ordinal = 0))
    private int hodgepodge$sync(TileEntityNuclearReactorElectric instance) {
        // Ensure the update ticker is locked to the world time, using a modulo to never be negative.
        // Technically, the IC2 one would roll over every ~3.5 years, but that means it's possible that
        // nobody ever tested what would happen if it did roll over.
        return (int) floorMod(getWorld().getTotalWorldTime(), (long) Integer.MAX_VALUE);
    }
}
