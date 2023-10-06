package com.mitchej123.hodgepodge.mixins.late.betterhud;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.Common;

import tk.nukeduck.hud.element.entityinfo.ExtraGuiElementMobInfo;

@Mixin(value = ExtraGuiElementMobInfo.class, remap = false)
public abstract class MixinHealthRender {

    /**
     * Prevent the game from freezing by drawing more hearts than it can chew
     */
    @Inject(
            method = "renderInfo(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/Minecraft;F)V",
            remap = false,
            at = @At(
                    shift = At.Shift.BEFORE,
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V"),
            cancellable = true)
    private void onlyRenderReasonableHP(EntityLivingBase entity, Minecraft mc, float partialTicks, CallbackInfo ci) {
        if (entity.getMaxHealth() > Common.config.betterHUDHPRenderLimit) {
            GL11.glPopMatrix();
            ci.cancel();
        }
    }

    /**
     * Only render the info background when it's actually needed
     */
    @ModifyArg(
            method = "renderInfo(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/Minecraft;F)V",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Ltk/nukeduck/hud/util/RenderUtil;renderQuad(Lnet/minecraft/client/renderer/Tessellator;IIIIFFFF)V",
                    remap = false),
            index = 4)
    private int limitInfoBoxSize(int input) {
        return input > Common.config.betterHUDHPRenderLimit ? 20 : input;
    }
}
