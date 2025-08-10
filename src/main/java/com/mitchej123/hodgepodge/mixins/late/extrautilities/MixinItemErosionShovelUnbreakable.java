package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import com.rwtema.extrautils.item.ItemErosionShovel;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemErosionShovel.class)
public class MixinItemErosionShovelUnbreakable extends Item 
{
    @Override
    public boolean isDamageable() {
        return false;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void hodgepodge$makeUnbreakableErosionShovel(CallbackInfo ci) {
        ((Item) (Object) this).setMaxDamage(0);
    }
}
