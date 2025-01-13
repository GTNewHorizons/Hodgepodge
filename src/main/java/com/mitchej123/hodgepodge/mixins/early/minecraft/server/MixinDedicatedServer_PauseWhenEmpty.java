package com.mitchej123.hodgepodge.mixins.early.minecraft.server;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PropertyManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.mixins.interfaces.IPauseWhenEmpty;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer_PauseWhenEmpty implements IPauseWhenEmpty {

    @Shadow
    private PropertyManager settings;

    @Unique
    private int hodgepodge$pauseWhenEmptySeconds = 0;

    @Override
    public int getPauseWhenEmptySeconds() {
        return hodgepodge$pauseWhenEmptySeconds;
    }

    @Override
    public void setPauseWhenEmptySeconds(int value) {
        value = Math.max(value, 0);
        hodgepodge$pauseWhenEmptySeconds = value;

        settings.setProperty("pause-when-empty-seconds", hodgepodge$pauseWhenEmptySeconds);
        settings.saveProperties();
    }

    @Inject(
            method = "startServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/FMLCommonHandler;onServerStarted()V",
                    remap = false,
                    shift = At.Shift.AFTER))
    public void hodgepodge$setupServer(CallbackInfoReturnable<Boolean> cir) {
        hodgepodge$pauseWhenEmptySeconds = settings.getIntProperty("pause-when-empty-seconds", 0);
    }
}
