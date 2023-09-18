package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft;

import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.gtnewhorizon.gtnhlib.GTNHLib;

@Mixin(BlockBed.class)
public class MixinBlockBed {

    @Redirect(
            method = "onBlockActivated",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;addChatComponentMessage(Lnet/minecraft/util/IChatComponent;)V"))
    public void hodgepodge$sendMessageAboveHotbar(EntityPlayer player, IChatComponent chatComponent) {
        GTNHLib.proxy.sendMessageAboveHotbar(
                (EntityPlayerMP) player,
                chatComponent.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE)),
                60,
                true,
                true);
    }
}
