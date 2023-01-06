package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import com.mitchej123.hodgepodge.Hodgepodge;
import com.rwtema.extrautils.block.BlockSpikeGold;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockSpikeGold.class)
public class MixinBlockSpikeGold {
    @Inject(
            method = "onEntityCollidedWithBlock(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V",
            at = @At("HEAD"))
    public void onEntityCollidedWithBlockStart(World world, int x, int y, int z, Entity target, CallbackInfo ci) {
        if (!world.isRemote) {
            Hodgepodge.EVENT_HANDLER.setAidTriggerDisabled(true);
        }
    }

    @Inject(
            method = "onEntityCollidedWithBlock(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V",
            at = @At("RETURN"))
    public void onEntityCollidedWithBlockEnd(World world, int x, int y, int z, Entity target, CallbackInfo ci) {
        if (!world.isRemote) {
            Hodgepodge.EVENT_HANDLER.setAidTriggerDisabled(false);
        }
    }
}
