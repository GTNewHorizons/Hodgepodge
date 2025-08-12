package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.rwtema.extrautils.item.ItemErosionShovel;

@Mixin(ItemErosionShovel.class)
public class MixinItemErosionShovelUnbreakable extends Item {

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void hodgepodge$makeUnbreakableErosionShovel(CallbackInfo ci) {
        this.setMaxDamage(0);
    }
}
