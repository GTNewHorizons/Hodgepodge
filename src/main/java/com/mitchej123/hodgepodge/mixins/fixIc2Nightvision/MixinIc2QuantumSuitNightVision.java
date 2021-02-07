package com.mitchej123.hodgepodge.mixins.fixIc2Nightvision;

import ic2.core.item.armor.ItemArmorQuantumSuit;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemArmorQuantumSuit.class)
public class MixinIc2QuantumSuitNightVision {
    @Redirect(
        method= "onArmorTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V", 
        at = @At(
            value="INVOKE", 
            target="Lnet/minecraft/world/World;getBlockLightValue(III)I"
        ),
        remap = false
    )
    public int getBlockLightValue(World world, int p_72957_1_, int p_72957_2_, int p_72957_3_) {
        // Ic2 nightvision will blind anyone if `getBlockLightValue` returns > 8; so always return 1
        return 1;
    }
}
