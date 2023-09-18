package com.mitchej123.hodgepodge.mixin.mixins.late.extrautilities;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.Hodgepodge;
import com.rwtema.extrautils.block.BlockSpike;
import com.rwtema.extrautils.block.BlockSpikeDiamond;
import com.rwtema.extrautils.block.BlockSpikeGold;
import com.rwtema.extrautils.block.BlockSpikeWood;

@Mixin(value = { BlockSpike.class, BlockSpikeDiamond.class, BlockSpikeGold.class, BlockSpikeWood.class })
public abstract class MixinBlockSpike {

    @Inject(
            method = "onEntityCollidedWithBlock(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V",
            at = @At("HEAD"))
    public void hodgepodge$onEntityCollidedWithBlockStart(World world, int x, int y, int z, Entity target,
            CallbackInfo ci) {
        if (!world.isRemote) {
            Hodgepodge.EVENT_HANDLER.setAidTriggerDisabled(true);
        }
    }

    @Inject(
            method = "onEntityCollidedWithBlock(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V",
            at = @At("RETURN"))
    public void hodgepodge$onEntityCollidedWithBlockEnd(World world, int x, int y, int z, Entity target,
            CallbackInfo ci) {
        if (!world.isRemote) {
            Hodgepodge.EVENT_HANDLER.setAidTriggerDisabled(false);
        }
    }
}
