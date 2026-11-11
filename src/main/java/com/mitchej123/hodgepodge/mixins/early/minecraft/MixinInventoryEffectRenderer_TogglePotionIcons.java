package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.InventoryEffectRenderer;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Mixin(InventoryEffectRenderer.class)
public class MixinInventoryEffectRenderer_TogglePotionIcons {

    @WrapMethod(method = "func_147044_g")
    private void hodgepodge$togglePotionEffectIcons(Operation<Void> original) {}
}
