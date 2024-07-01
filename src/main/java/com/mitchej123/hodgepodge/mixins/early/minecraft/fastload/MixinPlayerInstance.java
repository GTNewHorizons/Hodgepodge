package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.interfaces.ExtEntityPlayerMP;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;

@Mixin(PlayerManager.PlayerInstance.class)
public class MixinPlayerInstance {

    @Redirect(
            method = "addPlayer",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1))
    private boolean hodgepodge$replaceChunkSetAdd(List instance, Object o,
            @Local(argsOnly = true) EntityPlayerMP player) {
        return ((ExtEntityPlayerMP) player).chunksToLoad().add(ChunkPosUtil.toLong(o));
    }

    @Redirect(
            method = "removePlayer",
            at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z", ordinal = 3))
    private boolean hodgepodge$replaceChunkSetRemove(List instance, Object o,
            @Local(argsOnly = true) EntityPlayerMP player) {
        return ((ExtEntityPlayerMP) player).chunksToLoad().remove(ChunkPosUtil.toLong(o));
    }

    @Redirect(
            method = "sendToAllPlayersWatchingChunk",
            at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
    private boolean hodgepodge$replaceChunkSetContains(List instance, Object o, @Local EntityPlayerMP player) {
        return ((ExtEntityPlayerMP) player).chunksToLoad().remove(ChunkPosUtil.toLong(o));
    }
}
