package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

/**
 * A teleported player stays registered in its old chunk until its next entity tick, which only runs once all chunks
 * within 32 blocks of the new position are loaded. With throttled chunk generation that can take several ticks. If the
 * old chunk is unloaded in the meantime, vanilla unloads the player together with the chunk's other entities: it's
 * removed from the world's entity list, never ticks again and so never gets sent any chunks. Players are never part of
 * a chunk's saved entities, so they are taken out of the chunk instead (it could otherwise re-add them from Forge's
 * dormant chunk cache); their next tick moves them into their new chunk.
 */
@Mixin(Chunk.class)
public class MixinChunk_KeepPlayersOnUnload {

    @WrapOperation(
            method = "onChunkUnload",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;unloadEntities(Ljava/util/List;)V"))
    private void hodgepodge$keepPlayers(World world, List<Entity> entities, Operation<Void> original) {
        entities.removeIf(entity -> entity instanceof EntityPlayer);
        original.call(world, entities);
    }
}
