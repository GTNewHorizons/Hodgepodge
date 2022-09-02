package com.mitchej123.hodgepodge.mixins.minecraft;

import com.mitchej123.hodgepodge.core.HodgePodgeClient;
import com.mitchej123.hodgepodge.core.util.RenderDebugHelper;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {
    private final Set<TileEntity> knownIssues = Collections.newSetFromMap(new WeakHashMap<>());

    @Inject(
            method = "renderEntities",
            at =
                    @At(
                            value = "FIELD",
                            ordinal = 0,
                            target = "Lnet/minecraft/client/renderer/RenderGlobal;tileEntities:Ljava/util/List;"))
    public void prepareTESR(EntityLivingBase p_147589_1_, ICamera p_147589_2_, float p_147589_3_, CallbackInfo ci) {
        RenderDebugHelper.recordGLStates();
    }

    @Redirect(
            method = "renderEntities",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;renderTileEntity(Lnet/minecraft/tileentity/TileEntity;F)V"))
    public void postTESR(TileEntityRendererDispatcher instance, TileEntity j, float k) {
        int renderDebugMode = HodgePodgeClient.renderDebugMode;
        if (renderDebugMode != 0)
            // this should be enough
            GL11.glPushAttrib(
                    GL11.GL_ENABLE_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LIGHTING_BIT);

        instance.renderTileEntity(j, k);

        if (renderDebugMode == 0) {
            knownIssues.clear();
            return;
        }

        if (!knownIssues.contains(j) && !RenderDebugHelper.checkGLStates()) {
            knownIssues.add(j);
            Minecraft.getMinecraft()
                    .thePlayer
                    .addChatMessage(
                            new ChatComponentText("TileEntity (" + j.getClass().getName() + " at " + j.xCoord + ", "
                                    + j.yCoord + ", " + j.zCoord + ") is messing up render states!"));
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
