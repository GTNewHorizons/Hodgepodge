package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntityItem_BouncingFix {

    @Shadow
    public World worldObj;
    @Shadow
    @Final
    public AxisAlignedBB boundingBox;

    @Inject(method = "func_145771_j", at = @At("HEAD"), cancellable = true)
    private void dontEjectBySelectionBox(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof EntityItem) {
            List list = this.worldObj.func_147461_a(this.boundingBox);
            if (list.isEmpty()) {
                cir.setReturnValue(false);
            }
        }
    }
}
