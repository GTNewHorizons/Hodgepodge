package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PathNavigate.class)
public abstract class MixinPathNavigate {

    @Shadow
    public abstract float getPathSearchRange();

    @Shadow
    private EntityLiving theEntity;

    @Inject(
            method = "getPathToXYZ",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getEntityPathToXYZ(Lnet/minecraft/entity/Entity;IIIFZZZZ)Lnet/minecraft/pathfinding/PathEntity;"))
    private void checkExist(double x, double y, double z, CallbackInfoReturnable<PathEntity> cir) {
        final int x1 = MathHelper.floor_double(this.theEntity.posX);
        final int y1 = MathHelper.floor_double(this.theEntity.posY);
        final int z1 = MathHelper.floor_double(this.theEntity.posZ);
        final int range = (int) (this.getPathSearchRange() + 8.0F);
        if (!this.theEntity.worldObj
                .checkChunksExist(x1 - range, y1 - range, z1 - range, x1 + range, y1 + range, z1 + range)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(
            method = "getPathToEntityLiving",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getPathEntityToEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;FZZZZ)Lnet/minecraft/pathfinding/PathEntity;"))
    private void checkExist(Entity other, CallbackInfoReturnable<PathEntity> cir) {
        final int x1 = MathHelper.floor_double(this.theEntity.posX);
        final int y1 = MathHelper.floor_double(this.theEntity.posY + 1.0D);
        final int z1 = MathHelper.floor_double(this.theEntity.posZ);
        final int range = (int) (this.getPathSearchRange() + 16.0F);
        if (!this.theEntity.worldObj
                .checkChunksExist(x1 - range, y1 - range, z1 - range, x1 + range, y1 + range, z1 + range)) {
            cir.setReturnValue(null);
        }
    }
}
