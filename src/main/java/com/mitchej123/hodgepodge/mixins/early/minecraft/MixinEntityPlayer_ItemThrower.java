package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer_ItemThrower {

    @ModifyArg(
            method = "dropPlayerItemWithRandomChoice",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/common/ForgeHooks;onPlayerTossEvent(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/entity/item/EntityItem;",
                    remap = false),
            index = 2)
    private boolean hodgepodge$passThrower(boolean original, @Local(argsOnly = true) boolean addThrowerTag) {
        return addThrowerTag;
    }
}
