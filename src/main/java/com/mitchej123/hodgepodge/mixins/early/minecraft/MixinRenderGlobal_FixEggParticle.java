package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.init.Items;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal_FixEggParticle {

    @Shadow
    Minecraft mc;

    @Shadow
    WorldClient theWorld;

    @Inject(method = "doSpawnParticle", at = @At("RETURN"), cancellable = true)
    private void injectEggParticle(String name, double x, double y, double z, double dx, double dy, double dz,
            CallbackInfoReturnable<EntityFX> cir) {
        if (cir.getReturnValue() == null && "eggpoof".equals(name)) {
            EntityFX entityfx = new EntityBreakingFX(theWorld, x, y, z, Items.egg);
            mc.effectRenderer.addEffect(entityfx);
            cir.setReturnValue(entityfx);
        }
    }

}
