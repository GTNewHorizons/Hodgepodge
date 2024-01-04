package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnderPearl;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemEnderPearl.class)
public abstract class MixinItemEnderPearl extends Item {

    @Redirect(
            method = "onItemRightClick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;capabilities:Lnet/minecraft/entity/player/PlayerCapabilities;",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0))
    public PlayerCapabilities hodgepodge$getCapabilities(EntityPlayer player) {
        PlayerCapabilities caps = player.capabilities;
        caps.isCreativeMode = false;
        return caps;
    }
}
