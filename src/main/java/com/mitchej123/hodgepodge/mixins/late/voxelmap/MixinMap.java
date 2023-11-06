package com.mitchej123.hodgepodge.mixins.late.voxelmap;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.thevoxelbox.voxelmap.j;

@Mixin(j.class)
public class MixinMap {

    @Redirect(
            // int com.thevoxelbox.voxelmap.util.GameVariableAccessShim.yCoord()
            at = @At(value = "INVOKE", target = "Lcom/thevoxelbox/voxelmap/util/v;new()I", remap = false),
            method = "if(Lnet/minecraft/client/Minecraft;)V", // void drawMinimap(Minecraft)
            remap = false)
    private int hodgepodge$getYCoord() {
        return (int) (Minecraft.getMinecraft().thePlayer.posY - 1);
    }

}
