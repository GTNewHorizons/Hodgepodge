package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
    @Shadow
    public EntityClientPlayerMP thePlayer;
    @Shadow
    public GameSettings gameSettings;

    @Unique
    private Vec3 lastPosition;
    @Unique
    private ForgeDirection lastSide;

    @Inject(
            method = "runTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/FMLCommonHandler;onPreClientTick()V",
                    remap = false,
                    shift = At.Shift.AFTER))
    private void hodgepodge$func_147121_ag(CallbackInfo ci) {
        if (!TweaksConfig.fastBlockPlacing) return;
        if (thePlayer == null || thePlayer.isUsingItem()) return;
        if (objectMouseOver == null) return;
        if (objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

        ItemStack itemstack = this.thePlayer.inventory.getCurrentItem();

        if (itemstack == null) return;
        if (!(itemstack.getItem() instanceof ItemBlock)) return;

        Vec3 pos = createVec3(objectMouseOver);
        if (rightClickDelayTimer > 0 && !isPosEqual(pos, lastPosition)
                && (lastPosition == null || !isPosEqual(pos, getNewPosition(lastPosition, lastSide)))) {
            rightClickDelayTimer = 0;
        } else if (rightClickDelayTimer == 0 && isPosEqual(pos, lastPosition)
                && lastSide.equals(ForgeDirection.getOrientation(objectMouseOver.sideHit))) {
                    rightClickDelayTimer = 4;
                }

        lastPosition = pos;
        lastSide = ForgeDirection.getOrientation(objectMouseOver.sideHit);
    }

    @Unique
    private Vec3 createVec3(MovingObjectPosition pos) {
        return Vec3.createVectorHelper(pos.blockX, pos.blockY, pos.blockZ);
    }

    @Unique
    private boolean isPosEqual(Vec3 p1, Vec3 p2) {
        if (p1 == null || p2 == null) {
            return false;
        }
        return p1.xCoord == p2.xCoord && p1.yCoord == p2.yCoord && p1.zCoord == p2.zCoord;
    }

    @Unique
    private Vec3 getNewPosition(Vec3 pos, ForgeDirection direction) {
        return Vec3.createVectorHelper(
                pos.xCoord + direction.offsetX,
                pos.yCoord + direction.offsetY,
                pos.zCoord + direction.offsetZ);
    }
}
