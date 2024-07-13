package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

import org.joml.Vector3i;
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

    @Unique
    private final Vector3i currentPosition = new Vector3i(0, 0, 0);
    @Unique
    private final Vector3i comparePosition = new Vector3i(0, 0, 0);
    @Unique
    private final Vector3i lastPosition = new Vector3i(0, 0, 0);
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

        currentPosition.set(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
        if (rightClickDelayTimer > 0 && !currentPosition.equals(lastPosition)
                && (!currentPosition.equals(
                        comparePosition.set(lastPosition).add(lastSide.offsetX, lastSide.offsetY, lastSide.offsetZ)))) {
            rightClickDelayTimer = 0;
        } else if (rightClickDelayTimer == 0 && currentPosition.equals(lastPosition)
                && lastSide.equals(ForgeDirection.getOrientation(objectMouseOver.sideHit))) {
                    rightClickDelayTimer = 4;
                }

        lastPosition.set(currentPosition);
        lastSide = ForgeDirection.getOrientation(objectMouseOver.sideHit);
    }
}
