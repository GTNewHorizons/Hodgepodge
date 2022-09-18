package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.Common;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * To be applied after {@link MixinEntityLivingBase_FixPotionException}
 */
@Mixin(value = EntityLivingBase.class, priority = 1001)
public class MixinEntityLivingBase_HidePotionParticles {

    @Inject(
            method = "updatePotionEffects",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/World;spawnParticle(Ljava/lang/String;DDDDDD)V",
                            shift = At.Shift.BEFORE),
            cancellable = true)
    public void hodgepodge$HideParticles(CallbackInfo ci) {
        if (Common.config.hidePotionParticlesFromSelf && (Object) this == Minecraft.getMinecraft().thePlayer) {
            ci.cancel();
        }
    }
}
