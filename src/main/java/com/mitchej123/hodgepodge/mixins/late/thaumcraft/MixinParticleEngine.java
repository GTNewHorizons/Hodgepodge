package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import cpw.mods.fml.common.gameevent.TickEvent;
import thaumcraft.client.fx.ParticleEngine;

@Mixin(value = ParticleEngine.class, remap = false)
public class MixinParticleEngine {

    @Shadow
    private HashMap<Integer, ArrayList<EntityFX>>[] particles;
    @Unique
    private boolean hp$hasParticles;

    @WrapMethod(method = "onRenderWorldLast")
    private void hp$onRenderWorldLast(RenderWorldLastEvent event, Operation<Void> original) {
        if (hp$hasParticles) {
            original.call(event);
        }
    }

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V"))
    private void hp$removeUselessGlPush() {}

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V"))
    private void hp$removeUselessGlPop() {}

    @Inject(method = "addEffect", at = @At("TAIL"))
    private void hp$addEffect(CallbackInfo ci) {
        hp$hasParticles = true;
    }

    /**
     * @author Sisyphus
     * @reason Optimize
     */
    @Overwrite
    public void updateParticles(TickEvent.ClientTickEvent event) {
        if (!hp$hasParticles || event.phase != TickEvent.Phase.END) return;

        World world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        int dim = world.provider.dimensionId;
        hp$hasParticles = false;
        for (int layer = 0; layer < 4; layer++) {
            final ArrayList<EntityFX> parts = this.particles[layer].get(dim);
            if (parts != null) {
                for (int i = 0; i < parts.size(); i++) {
                    final EntityFX entityfx = parts.get(i);

                    try {
                        if (entityfx != null) {
                            entityfx.onUpdate();
                            hp$hasParticles = true;
                        }
                    } catch (Throwable var12) {
                        CrashReport crashreport = CrashReport.makeCrashReport(var12, "Ticking Particle");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being ticked");
                        crashreportcategory.addCrashSectionCallable("Particle", () -> entityfx.toString());
                        crashreportcategory.addCrashSectionCallable("Particle Type", () -> "ENTITY_PARTICLE_TEXTURE");
                        throw new ReportedException(crashreport);
                    }

                    if (entityfx == null || entityfx.isDead) {
                        parts.remove(i--);
                    }
                }
            }
        }
    }
}
