package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(Minecraft.class)
public class MixinMinecraft_FastBlockPlacing {

    @Shadow
    private int rightClickDelayTimer;
    @Shadow
    public MovingObjectPosition objectMouseOver;

    private MovingObjectPosition lastPosition;
    private ForgeDirection lastSide;

    @Inject(
            method = "runTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;func_147115_a(Z)V",
                    shift = At.Shift.AFTER))
    private void hodgepodge$func_147121_ag(CallbackInfo ci) {
        if (!TweaksConfig.fastBlockPlacing) return;
        if (objectMouseOver == null) return;
        if (objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

        // TODO check for keybind toggle active
        if (!isPosEqual(objectMouseOver, lastPosition)
                && (lastPosition == null || !isPosEqual(objectMouseOver, getNewPosition(lastPosition, lastSide)))) {
            rightClickDelayTimer = 0;
        } else if (isPosEqual(objectMouseOver, lastPosition) && lastSide != null
                && lastSide == ForgeDirection.getOrientation(objectMouseOver.sideHit)) {
                    rightClickDelayTimer = 4;
                }

        lastPosition = objectMouseOver;
        lastSide = ForgeDirection.getOrientation(objectMouseOver.sideHit);
    }

    private boolean isPosEqual(MovingObjectPosition p1, MovingObjectPosition p2) {
        if (p1 == null || p2 == null) {
            return false;
        }
        return p1.blockX == p2.blockX && p1.blockY == p2.blockY && p1.blockZ == p2.blockZ;
    }

    private MovingObjectPosition getNewPosition(MovingObjectPosition pos, ForgeDirection direction) {
        return new MovingObjectPosition(
                pos.blockX + direction.offsetX,
                pos.blockY + direction.offsetY,
                pos.blockZ + direction.offsetZ,
                direction.ordinal(),
                pos.hitVec);
    }
}
