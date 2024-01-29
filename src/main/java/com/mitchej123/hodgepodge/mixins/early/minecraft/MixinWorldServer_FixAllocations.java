package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.hax.LongChunkCoordIntPairSet;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer_FixAllocations {

    @Redirect(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"))
    private Iterator<?> fixAllocations(Set instance) {
        return ((LongChunkCoordIntPairSet) instance).unsafeIterator();
    }

}
