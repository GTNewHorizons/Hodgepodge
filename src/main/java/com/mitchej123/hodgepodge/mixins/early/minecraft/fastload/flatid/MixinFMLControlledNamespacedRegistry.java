package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.flatid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.BlockExt_ID;

import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;

@Mixin(FMLControlledNamespacedRegistry.class)
public class MixinFMLControlledNamespacedRegistry {

    @Inject(method = "addObjectRaw", at = @At(value = "RETURN"), remap = false)
    private <I> void hodgepodge$captureID(int id, String name, I thing, CallbackInfo ci) {
        if (!(thing instanceof BlockExt_ID block)) return;

        block.setID(id);
    }
}
