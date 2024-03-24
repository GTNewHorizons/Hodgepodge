package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.interfaces.ExtEntityPlayerMP;
import com.mitchej123.hodgepodge.mixins.interfaces.FastWorldServer;
import com.mitchej123.hodgepodge.server.FastCPS;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Shadow
    @Final
    private WorldServer theWorldServer;

    private static final ChunkCoordIntPair hodgepodge$dummyCoord = new ChunkCoordIntPair(
            Integer.MAX_VALUE,
            Integer.MAX_VALUE);

    /**
     * @author ah-OOG-ah
     * @reason throttles new chunk generation in filterChunkLoadQueue
     */
    @WrapOperation(
            method = "filterChunkLoadQueue",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/management/PlayerManager;getOrCreateChunkWatcher(IIZ)Lnet/minecraft/server/management/PlayerManager$PlayerInstance;"),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/server/management/PlayerManager;getOrCreateChunkWatcher(IIZ)Lnet/minecraft/server/management/PlayerManager$PlayerInstance;",
                            ordinal = 1),
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/server/management/PlayerManager;getOrCreateChunkWatcher(IIZ)Lnet/minecraft/server/management/PlayerManager$PlayerInstance;",
                            ordinal = 2)))
    private PlayerManager.PlayerInstance hodgepodge$throttleChunkGen(PlayerManager instance, int cx, int cz,
            boolean instantiate, Operation<PlayerManager.PlayerInstance> original,
            @Local(argsOnly = true) EntityPlayerMP player) {

        final long key = ChunkCoordIntPair.chunkXZ2Int(cx, cz);

        if (((FastCPS) this.theWorldServer.theChunkProviderServer).doesChunkExist(cx, cz, key))
            return original.call(instance, cx, cz, instantiate); // Always load generated chunks
        if (((FastWorldServer) this.theWorldServer).isThrottlingGen()) {

            ((ExtEntityPlayerMP) player).setThrottled(true);
            return null; // Don't generate new chunks while throttling
        }

        // Generate, but count it against the budget this tick
        ((FastWorldServer) this.theWorldServer).spendGenBudget(1);
        return original.call(instance, cx, cz, instantiate);
    }

    /**
     * @author ah-OOG-ah
     * @reason handles nulls passed from
     *         {@link #hodgepodge$throttleChunkGen(PlayerManager, int, int, boolean, Operation, EntityPlayerMP)}
     */
    @WrapOperation(
            method = "filterChunkLoadQueue",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/management/PlayerManager$PlayerInstance;chunkLocation:Lnet/minecraft/world/ChunkCoordIntPair;"),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/server/management/PlayerManager$PlayerInstance;chunkLocation:Lnet/minecraft/world/ChunkCoordIntPair;",
                            ordinal = 1),
                    to = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/server/management/PlayerManager$PlayerInstance;chunkLocation:Lnet/minecraft/world/ChunkCoordIntPair;",
                            ordinal = 2)))
    private ChunkCoordIntPair hodgepodge$throttleChunkGen(PlayerManager.PlayerInstance instance,
            Operation<ChunkCoordIntPair> original) {
        if (instance != null) return original.call(instance);
        return hodgepodge$dummyCoord;
    }

    @WrapOperation(
            method = "filterChunkLoadQueue",
            at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;contains(Ljava/lang/Object;)Z"))
    private boolean hodgepodge$forceChunkSend(ArrayList<ChunkCoordIntPair> priorChunks, Object o,
            Operation<Boolean> original, @Local(argsOnly = true) EntityPlayerMP player,
            @Local(name = "j") int viewRadius, @Local(name = "k") int pcx, @Local(name = "l") int pcz) {

        if (o instanceof ChunkCoordIntPair c) {

            if (!((ExtEntityPlayerMP) player).isThrottled()) {
                return original.call(priorChunks, o);
            }

            // Returns true for all chunks in a square with sides of (2*viewRadius+1)^2, centered around the player
            int dist = Math.max(Math.abs(pcx - c.chunkXPos), Math.abs(pcz - c.chunkZPos));
            return dist <= viewRadius;
        }

        return false;
    }

    @Inject(method = "updatePlayerPertinentChunks", at = @At(value = "HEAD"))
    private void hodgepodge$resetChunkThrottle(CallbackInfo ci, @Local(argsOnly = true) EntityPlayerMP player) {

        if (player instanceof ExtEntityPlayerMP eemp) {

            eemp.setWasThrottled(eemp.isThrottled());
            eemp.setThrottled(false);
        }
    }

    /**
     * @author ah-OOG-ah
     * @reason force chunks sent to the player to update if the player's chunkgen was throttled last tick
     */
    @ModifyConstant(method = "updatePlayerPertinentChunks", constant = @Constant(doubleValue = 64.0))
    private double hodgepodge$forceChunkUpdate(double original, @Local(argsOnly = true) EntityPlayerMP player) {
        return ((ExtEntityPlayerMP) player).wasThrottled() ? Double.MIN_VALUE : original;
    }

    /**
     * @author ah-OOG-ah
     * @reason throttles new chunk generation in updatePlayerPertinentChunks
     */
    @WrapWithCondition(
            method = "updatePlayerPertinentChunks",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean hodgepodge$throttleChunkGen(List<ChunkCoordIntPair> instance, @Coerce Object coord,
            @Local(argsOnly = true) EntityPlayerMP player, @Local(name = "i") int pcx, @Local(name = "j") int pcz) {

        final ChunkCoordIntPair c = (ChunkCoordIntPair) coord;
        final int cx = c.chunkXPos;
        final int cz = c.chunkZPos;
        if (cx == pcx && cz == pcz) return true; // Always load the player's chunk
        if (((FastCPS) this.theWorldServer.theChunkProviderServer).doesChunkExist(c)) return true; // Always load
                                                                                                   // generated chunks
        if (((FastWorldServer) this.theWorldServer).isThrottlingGen()) {

            ((ExtEntityPlayerMP) player).setThrottled(true);
            return false; // Don't generate new chunks while throttling
        }

        // Generate, but count it against the budget this tick
        ((FastWorldServer) this.theWorldServer).spendGenBudget(1);
        return true;
    }
}
