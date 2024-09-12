package com.mitchej123.hodgepodge.mixins.late.xaerominimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import xaero.common.minimap.MinimapProcessor;
import xaero.common.minimap.MinimapRadar;
import xaero.common.minimap.render.MinimapFBORenderer;
import xaero.common.minimap.render.MinimapRenderer;
import xaero.common.settings.ModSettings;

@Mixin(value = MinimapRenderer.class, remap = false)
public abstract class MixinMinimapRenderer {

    @WrapWithCondition(
            method = "renderMinimap",
            at = @At(
                    value = "INVOKE",
                    target = "Lxaero/common/minimap/render/MinimapFBORenderer;renderMainEntityDot(Lxaero/common/minimap/MinimapProcessor;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;DDDDFLxaero/common/minimap/MinimapRadar;ZIZZZDLxaero/common/settings/ModSettings;)V"))
    private boolean hodgepodge$fixPlayerDotRenderCheck(MinimapFBORenderer instance, MinimapProcessor minimap,
            EntityPlayer p, Entity renderEntity, double ps, double pc, double playerX, double playerZ, float partial,
            MinimapRadar minimapRadar, boolean lockedNorth, int style, boolean smooth, boolean debug, boolean cave,
            double dotNameScale, ModSettings settings) {
        return !lockedNorth && settings.mainEntityAs == 1;
    }
}
