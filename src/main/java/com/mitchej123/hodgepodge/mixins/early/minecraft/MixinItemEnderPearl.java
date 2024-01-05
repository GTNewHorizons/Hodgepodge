package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnderPearl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemEnderPearl.class)
public abstract class MixinItemEnderPearl extends Item {

    @Redirect(
            method = "onItemRightClick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerCapabilities;isCreativeMode:Z"))
    public boolean hodgepodge$removeCreativeCheck(PlayerCapabilities instance) {
        return false;
    }
}
