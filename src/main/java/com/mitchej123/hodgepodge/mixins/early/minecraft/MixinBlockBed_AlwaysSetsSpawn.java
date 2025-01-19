package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBed.class)
public class MixinBlockBed_AlwaysSetsSpawn {

    @Inject(
            method = "onBlockActivated",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;sleepInBedAt(III)Lnet/minecraft/entity/player/EntityPlayer$EnumStatus;",
                    shift = At.Shift.AFTER))
    public void hodgepodge$setSpawn(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
            float subY, float subZ, CallbackInfoReturnable<Boolean> cir) {
        // do fake player activators dream of fake sheep?
        if (!(player instanceof EntityPlayerMP entityPlayerMP)) return;

        entityPlayerMP.setSpawnChunk(new ChunkCoordinates(x, y, z), false);
        // not render over hotbar due to other mixin
        entityPlayerMP.addChatComponentMessage(
                new ChatComponentText(StatCollector.translateToLocal("hodgepodge.bed_respawn.msg")));
    }
}
