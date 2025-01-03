package com.mitchej123.hodgepodge.mixins.early.cofhcore;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import cofh.core.util.FMLEventHandler;

@Mixin(value = FMLEventHandler.class, remap = false)
public abstract class MixinFMLEventHandler {

    @WrapWithCondition(
            method = "handleIdMappingEvent",
            at = @At(value = "INVOKE", target = "Lcofh/core/util/oredict/OreDictionaryArbiter;initialize()V"))
    private boolean hodgepodge$fixNPE() {
        return !Minecraft.getMinecraft().isSingleplayer();
    }
}
