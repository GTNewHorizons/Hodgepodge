package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.gtnewhorizon.gtnhlib.GTNHLib;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(BlockBed.class)
public class MixinBlockBed_AlwaysSetsSpawn {

    @WrapOperation(
            method = "onBlockActivated",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;sleepInBedAt(III)Lnet/minecraft/entity/player/EntityPlayer$EnumStatus;"))
    public EntityPlayer.EnumStatus hodgepodge$setSpawn(EntityPlayer instance, int x, int y, int z,
            Operation<EntityPlayer.EnumStatus> original, @Local(argsOnly = true) World worldIn) {
        // do fake player activators dream of fake sheep?
        if (!(instance instanceof EntityPlayerMP entityPlayerMP)) return original.call(instance, x, y, z);

        ChunkCoordinates bedPos = entityPlayerMP.getBedLocation(worldIn.provider.dimensionId);

        ChunkCoordinates newBedPos = new ChunkCoordinates(x, y, z);

        if (worldIn.isDaytime() && !newBedPos.equals(bedPos)) {
            entityPlayerMP.setSpawnChunk(newBedPos, false);

            // render over hotbar since the caller doesn't handle OTHER_PROBLEM
            GTNHLib.proxy.sendMessageAboveHotbar(
                    entityPlayerMP,
                    new ChatComponentText(StatCollector.translateToLocal("hodgepodge.bed_respawn.msg"))
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE)),
                    60,
                    true,
                    true);
            return EntityPlayer.EnumStatus.OTHER_PROBLEM;
        }

        return original.call(instance, x, y, z);
    }
}
