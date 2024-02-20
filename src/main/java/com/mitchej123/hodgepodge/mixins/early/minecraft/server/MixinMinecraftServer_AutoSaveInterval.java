package com.mitchej123.hodgepodge.mixins.early.minecraft.server;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer_AutoSaveInterval {

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 900))
    private int hodgepodge$modifyAutoSaveInterval(int constant) {
        return TweaksConfig.autoSaveInterval;
    }

}
