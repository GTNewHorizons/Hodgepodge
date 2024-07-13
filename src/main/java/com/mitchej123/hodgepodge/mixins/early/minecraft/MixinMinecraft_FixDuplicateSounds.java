package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.server.integrated.IntegratedServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft_FixDuplicateSounds {

    @Shadow
    public abstract boolean isSingleplayer();

    @Shadow
    private IntegratedServer theIntegratedServer;

    @WrapWithCondition(
            method = "displayGuiScreen",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundHandler;resumeSounds()V"))
    private boolean hodgepodge$fixDuplicateSounds(SoundHandler instance, @Local(name = "old") GuiScreen old) {
        return old instanceof GuiIngameMenu && isSingleplayer() && !this.theIntegratedServer.getPublic();
    }
}
