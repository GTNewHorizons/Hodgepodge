package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.interfaces.ExtEntityPlayerMP;
import com.mitchej123.hodgepodge.mixins.interfaces.FastWorldServer;
import com.mitchej123.hodgepodge.server.FastCPS;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import static com.mitchej123.hodgepodge.Common.log;

import java.util.List;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {

    @Shadow @Final private WorldServer theWorldServer;
    @Shadow private int playerViewRadius;
    @Shadow protected abstract boolean overlaps(int x1, int z1, int x2, int z2, int radius);
    @Shadow protected abstract PlayerManager.PlayerInstance getOrCreateChunkWatcher(int cx, int cz, boolean shouldCreate);
    @Unique final private ChunkPosUtil.FastComparator cmp = new ChunkPosUtil.FastComparator();

    @Unique
    private final ChunkPosUtil.FastComparator hodgepodge$comparator = new ChunkPosUtil.FastComparator();

    /**
     * @author ah-OOG-ah
     * @reason Original method kinda sucked ngl
     */
    @Overwrite
    public void filterChunkLoadQueue(EntityPlayerMP player) {

        ((ExtEntityPlayerMP) player).chunksToLoad().removeIf(l -> cmp.setPos(player).withinRadius(l, this.playerViewRadius));
        ((ExtEntityPlayerMP) player).loadedChunks().removeIf(l -> cmp.setPos(player).withinRadius(l, this.playerViewRadius));
    }

    /**
     * @author ah-OOG-ah
     * @reason Original method was convoluted and impossible to fix
     */
    @Overwrite
    public void updatePlayerPertinentChunks(EntityPlayerMP player) {

        final ExtEntityPlayerMP eemp = (ExtEntityPlayerMP) player;
        eemp.setWasThrottled(eemp.isThrottled());
        eemp.setThrottled(false);

        final double deltax = player.posX - player.managedPosX;
        final double deltaz = player.posZ - player.managedPosZ;
        final double distMovedSquared = deltax * deltax + deltaz * deltaz;

        // If the player moved less than half a chunk (8^2 = 64), or if the player wasn't throttled last tick, no need
        // to update
        if (!eemp.wasThrottled() && distMovedSquared < 64)
            return;

        final int pcx = (int) player.posX >> 4;
        final int pcz = (int) player.posZ >> 4;
        final int prev_cx = (int) player.managedPosX >> 4;
        final int prev_cz = (int) player.managedPosZ >> 4;
        final int dpcx = pcx - prev_cx;
        final int dpcz = pcz - prev_cz;

        // If the player hasn't moved to a new chunk (and wasn't throttled), no need to update
        if (!eemp.wasThrottled() && dpcx == 0 && dpcz == 0)
            return;

        int x = 0;
        int z = 0;
        int sideLen = this.playerViewRadius * 2 + 1;
        final LongArrayList chunksToLoad = new LongArrayList(sideLen * sideLen);

        // Add all chunk coords in a spiral around the player
        for (int i = 0; i < sideLen * sideLen; ++i) {

            final int cx = x + pcx;
            final int cz = z + pcz;
            final long key = ChunkCoordIntPair.chunkXZ2Int(cx, cz);
            if (!eemp.loadedChunks().contains(key))
                chunksToLoad.add(key);

            // At the same time, we can check the previous chunk grid, and remove the ones which are out of range
            if (!this.overlaps(cx - dpcx, cz - dpcz, pcx, pcz, this.playerViewRadius)) {
                final PlayerManager.PlayerInstance playerinstance = this.getOrCreateChunkWatcher(cx - dpcx, cz - dpcz, false);

                if (playerinstance != null)
                    playerinstance.removePlayer(player);
            }

            if (Math.abs(x) <= Math.abs(z) && (x != z || x >= 0))
                x += z >= 0 ? 1 : -1;
            else
                z += x >= 0 ? -1 : 1;
        }

        // Purge the old load queue
        this.filterChunkLoadQueue(player);

        player.managedPosX = player.posX;
        player.managedPosZ = player.posZ;

        // Generate nearest chunks first
        // This also refills the load queue
        chunksToLoad.sort(hodgepodge$comparator.setPos(pcx, pcz));
        chunksToLoad.forEach(l -> {
            if (this.hodgepodge$allowChunkGen(l, pcx, pcz, eemp)) {
                this.getOrCreateChunkWatcher(
                        ChunkPosUtil.getPackedX(l),
                        ChunkPosUtil.getPackedZ(l),
                        true).addPlayer(player);

                if (!eemp.chunksToLoad().contains(l))
                    eemp.chunksToLoad().add(l);
            }
        });
    }

    /**
     * Checks if a chunk may be loaded without violating the chunkgen throttle.
     */
    @Unique
    private boolean hodgepodge$allowChunkGen(long c, int pcx, int pcz, ExtEntityPlayerMP player) {

        final int cx = ChunkPosUtil.getPackedX(c);
        final int cz = ChunkPosUtil.getPackedZ(c);

        if (cx == pcx && cz == pcz)
            return true; // Always load the player's chunk

        if (((FastCPS) this.theWorldServer.theChunkProviderServer).doesChunkExist(cx, cz, c))
            return true; // Always load chunks from disk

        if (((FastWorldServer) this.theWorldServer).isThrottlingGen()) {

            // Don't generate new chunks while throttling
            player.setThrottled(true);
            return false;
        }

        // Generate, but count it against the budget this tick
        ((FastWorldServer) this.theWorldServer).spendGenBudget(1);
        return true;
    }

    @Redirect(method = "isPlayerWatchingChunk", at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
    private boolean hodgepodge$replaceLoadedChunks(List instance, Object o, @Local(argsOnly = true) EntityPlayerMP player) {
        return ((ExtEntityPlayerMP) player).chunksToLoad().contains(ChunkPosUtil.toLong(o));
    }
}
