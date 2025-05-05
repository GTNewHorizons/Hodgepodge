package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityEgg.class)
public abstract class MixinEntityEgg extends EntityThrowable {

    public MixinEntityEgg(World p_i1776_1_) {
        super(p_i1776_1_);
    }

    @ModifyArgs(
            method = "onImpact",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnParticle(Ljava/lang/String;DDDDDD)V"))
    private void replaceSpawnParticleArgs(Args args) {
        args.set(0, "iconcrack_" + Item.getIdFromItem(Items.egg));
        args.set(4, ((double) rand.nextFloat() - 0.5D) * 0.08D);
        args.set(5, ((double) rand.nextFloat() - 0.5D) * 0.08D);
        args.set(6, ((double) rand.nextFloat() - 0.5D) * 0.08D);
    }
}
