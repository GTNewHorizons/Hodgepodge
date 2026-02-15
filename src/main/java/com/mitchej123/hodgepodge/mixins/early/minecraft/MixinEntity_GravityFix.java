package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntity_GravityFix extends Entity {

    public MixinEntity_GravityFix(World worldIn) {
        super(worldIn);
    }

    @Shadow
    public float prevLimbSwingAmount;
    @Shadow
    public float limbSwingAmount;
    @Shadow
    public float limbSwing;

    @Inject(method = "moveEntityWithHeading", at = @At("HEAD"), cancellable = true)
    public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_, CallbackInfo ci) {
        if (!(((EntityLivingBase) (Object) this) instanceof EntityPlayer)
                && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            double y = -0.0784000015258789D;
            double d7 = y;
            List list = this.worldObj
                    .getCollidingBoundingBoxes((Entity) this, this.boundingBox.addCoord(0.0D, y, 0.0D));
            for (int i = 0, j = list.size(); i < j; i++) {
                y = ((AxisAlignedBB) list.get(i)).calculateYOffset(this.boundingBox, y);
            }
            this.isCollidedVertically = d7 != y;
            this.onGround = d7 != y;
            this.prevLimbSwingAmount = this.limbSwingAmount;
            double x = this.posX - this.prevPosX;
            double z = this.posZ - this.prevPosZ;
            float f = (float) Math.min(Math.sqrt(x * x + z * z) * 4.0F, 1.0F);
            this.limbSwingAmount += (f - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
            ci.cancel();
        }
    }
}
