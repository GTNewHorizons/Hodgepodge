package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.client.HodgepodgeClient;
import com.mitchej123.hodgepodge.client.HodgepodgeClient.RenderDebugMode;
import com.mitchej123.hodgepodge.util.ManagedEnum;
import com.mitchej123.hodgepodge.util.RenderDebugHelper;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {

    @Unique
    private final Set<TileEntity> knownIssues = Collections.newSetFromMap(new WeakHashMap<>());

    @Inject(
            method = "renderEntities",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE_STRING",
                            target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
                            args = "ldc=blockentities")),
            at = @At(
                    value = "INVOKE",
                    ordinal = 0,
                    target = "Lnet/minecraft/client/renderer/RenderHelper;enableStandardItemLighting()V",
                    shift = At.Shift.AFTER,
                    by = 1))
    public void hodgepodge$prepareTESR(EntityLivingBase p_147589_1_, ICamera p_147589_2_, float p_147589_3_,
            CallbackInfo ci) {
        ManagedEnum<RenderDebugMode> renderDebugMode = HodgepodgeClient.renderDebugMode;
        if (renderDebugMode.is(RenderDebugMode.OFF)) return;
        RenderDebugHelper.recordGLStates();
    }

    @Redirect(
            method = "renderEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;renderTileEntity(Lnet/minecraft/tileentity/TileEntity;F)V"))
    public void hodgepodge$postTESR(TileEntityRendererDispatcher instance, TileEntity j, float k) {
        ManagedEnum<RenderDebugMode> renderDebugMode = HodgepodgeClient.renderDebugMode;
        if (!renderDebugMode.is(RenderDebugMode.OFF))
            // this should be enough
            GL11.glPushAttrib(
                    GL11.GL_ENABLE_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LIGHTING_BIT);

        instance.renderTileEntity(j, k);

        if (!renderDebugMode.is(RenderDebugMode.OFF)) {

            if (!knownIssues.contains(j) && !RenderDebugHelper.checkGLStates()) {
                knownIssues.add(j);
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(
                                "TileEntity (" + j.getClass().getName()
                                        + " at "
                                        + j.xCoord
                                        + ", "
                                        + j.yCoord
                                        + ", "
                                        + j.zCoord
                                        + ") is messing up render states!"));
                RenderDebugHelper.log.error(
                        "TileEntity {} at ({}, {}, {}) alter render state after TESR call: {}",
                        j.getClass(),
                        j.xCoord,
                        j.yCoord,
                        j.zCoord,
                        RenderDebugHelper.compose());
            }

            GL11.glPopAttrib();
        }
    }
}
