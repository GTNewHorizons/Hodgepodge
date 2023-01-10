package com.mitchej123.hodgepodge.mixins.early.minecraft.profiler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityRendererDispatcher.class)
public class TileEntityRendererDispatcherMixin {

    @Inject(
            method = "renderTileEntityAt",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/renderer/tileentity/TileEntitySpecialRenderer;renderTileEntityAt(Lnet/minecraft/tileentity/TileEntity;DDDF)V"))
    public void hodgepodge$startProfiler(
            TileEntity te, double x, double y, double z, float partialTicks, CallbackInfo ci) {
        if (Minecraft.getMinecraft().mcProfiler.profilingEnabled) {
            String name = me().getSpecialRenderer(te)
                    .getClass()
                    .getName()
                    .replace(".", "/"); // replacing due to specific logic inside profiler based on dots
            Minecraft.getMinecraft().mcProfiler.startSection(name);
        }
    }

    @Inject(
            method = "renderTileEntityAt",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/renderer/tileentity/TileEntitySpecialRenderer;renderTileEntityAt(Lnet/minecraft/tileentity/TileEntity;DDDF)V",
                            shift = At.Shift.AFTER))
    public void hodgepodge$endProfiler(
            TileEntity te, double x, double y, double z, float partialTicks, CallbackInfo ci) {
        if (Minecraft.getMinecraft().mcProfiler.profilingEnabled) {
            Minecraft.getMinecraft().mcProfiler.endSection();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private TileEntityRendererDispatcher me() {
        return ((TileEntityRendererDispatcher) ((Object) this));
    }
}
