package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cpw.mods.fml.common.Loader;

@Mixin(thaumcraft.common.lib.utils.Utils.class)
public class MixinUtils {

    @Inject(method = "isEETransmutionItem", at = @At("HEAD"), remap = false, cancellable = true)
    private static void hodgepodge$checkEEModExist(Item item, CallbackInfoReturnable<Boolean> ci) {
        if (!Loader.isModLoaded("ee")) {
            ci.cancel();
        }
    }
}
