package com.mitchej123.hodgepodge.mixins.late.travellersgear;

import java.util.Collection;

import net.minecraft.potion.PotionEffect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import travellersgear.client.ClientProxy;

@Mixin(ClientProxy.class)
public class MixinClientProxy {

    @Redirect(
            method = "guiPostInit",
            at = @At(value = "INVOKE", target = "Ljava/util/Collection;isEmpty()Z", remap = false),
            remap = false)
    public boolean hodgepodge$fixPotionOffset(Collection<PotionEffect> instance) {
        return true;
    }
}
