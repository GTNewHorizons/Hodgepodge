package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.item.ItemPiston;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(ItemPiston.class)
public class MixinItemPiston {

    @ModifyReturnValue(method = "getMetadata", at = @At("RETURN"))
    private int hodgepodge$getMetadata(int original) {
        // why is the original 7?
        return 0;
    }
}
