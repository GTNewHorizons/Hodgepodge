package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityFireball.class)
public abstract class MixinEntityFireball extends Entity {

    @Shadow
    public double accelerationX;

    @Shadow
    public double accelerationY;

    @Shadow
    public double accelerationZ;

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    public void hodgepodge$writeFireballAcceleration(NBTTagCompound tagCompound, CallbackInfo ci) {
        tagCompound.setTag(
                "acceleration", this.newDoubleNBTList(this.accelerationX, this.accelerationY, this.accelerationZ));
    }

    @Inject(method = "readEntityFromNBT", at = @At(value = "TAIL"))
    public void hodgepodge$readFireballAcceleration(NBTTagCompound tagCompund, CallbackInfo ci) {
        if (tagCompund.hasKey("acceleration", 9)) {
            NBTTagList nbttaglist = tagCompund.getTagList("acceleration", 6);
            this.accelerationX = nbttaglist.func_150309_d(0);
            this.accelerationY = nbttaglist.func_150309_d(1);
            this.accelerationZ = nbttaglist.func_150309_d(2);
        } else {
            this.setDead();
        }
    }

    /*Forced to have constructor*/
    private MixinEntityFireball(World worldIn) {
        super(worldIn);
    }
}
