package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cofh.lib.util.LinkedHashList;

@Mixin(World.class)
public class MixinWorld_CoFH_TE_Cache {

    @Dynamic("Remove CoFH tile entity cache")
    @Redirect(
            method = { "func_147448_a", "setTileEntity" },
            at = @At(
                    value = "INVOKE",
                    target = "Lcofh/lib/util/LinkedHashList;push(Ljava/lang/Object;)Z",
                    remap = false))
    private boolean hodgepodge$removeTEcache(LinkedHashList<?> list, Object o) {
        // do nothing
        return true;
    }

    @Dynamic("Remove CoFH tile entity cache")
    @Redirect(
            method = "addTileEntity",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lcofh/lib/util/LinkedHashList;push(Ljava/lang/Object;)Z",
                    remap = false))
    private boolean hodgepodge$removeTEcachee(LinkedHashList<?> list, Object o) {
        // do nothing
        return true;
    }
}
