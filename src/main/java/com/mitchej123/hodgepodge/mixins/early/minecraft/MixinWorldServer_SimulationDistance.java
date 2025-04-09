package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.Compat;
import com.mitchej123.hodgepodge.CoreTweaksCompat;
import com.mitchej123.hodgepodge.ISimulationDistanceWorld;
import com.mitchej123.hodgepodge.SimulationDistanceHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.*;
import net.minecraftforge.common.ForgeChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer_SimulationDistance extends World implements ISimulationDistanceWorld {

    @Shadow
    private TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet;

    @Shadow
    private Set<NextTickListEntry> pendingTickListEntriesHashSet;

    @Shadow
    private List<NextTickListEntry> pendingTickListEntriesThisTick;

    @Unique
    private final Set<ChunkCoordIntPair> hodgepodge$noTickChunks = new HashSet<>();

    public MixinWorldServer_SimulationDistance() {
        super(null, null, (WorldProvider) null, null, null);
    }

    @Override
    public void hodgepodge$preventChunkSimulation(ChunkCoordIntPair chunk, boolean prevent) {
        if (prevent) {
            hodgepodge$noTickChunks.add(chunk);
        } else {
            hodgepodge$noTickChunks.remove(chunk);
        }
    }

    /*
    Fake HashSet size so the original MC code doesn't process any ticks
     */
    @Redirect(method = "tickUpdates", at = @At(value = "INVOKE", target = "Ljava/util/TreeSet;size()I"))
    private int hodgepodge$fakeTreeSetSize(TreeSet<NextTickListEntry> instance) {
        return 0;
    }

    /*
   Fake TreeSet size to match HashSet size to prevent IllegalStateException
    */
    @Redirect(method = "tickUpdates", at = @At(value = "INVOKE", target = "Ljava/util/Set;size()I"))
    private int hodgepodge$fakeHashSetSize(Set<NextTickListEntry> instance) {
        return 0;
    }

    @Unique
    private boolean hodgepodge$closeToPlayer(int x, int z) {
        int simulationDistance = SimulationDistanceHelper.getSimulationDistance();
        for (EntityPlayer player : playerEntities) {
            int playerX = (int) player.posX >> 4;
            int playerZ = (int) player.posZ >> 4;
            if (Math.abs(playerX - x) <= simulationDistance && Math.abs(playerZ - z) <= simulationDistance) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean hodgepodge$shouldProcessTick(NextTickListEntry entry) {
        ChunkCoordIntPair pos = new ChunkCoordIntPair(entry.xCoord >> 4, entry.zCoord >> 4);
        if (hodgepodge$closeToPlayer(pos.chunkXPos, pos.chunkZPos)) {
            return true;
        }

        if (hodgepodge$noTickChunks.contains(pos)) {
            return false;
        }
        return ForgeChunkManager.getPersistentChunksFor(this).containsKey(pos);
    }

    @Inject(method = "tickUpdates", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void hodgepodge$cleanupTicks(boolean p_72955_1_, CallbackInfoReturnable<Boolean> cir) {
        if (pendingTickListEntriesTreeSet.size() != pendingTickListEntriesHashSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        }

        Iterator<NextTickListEntry> iterator = pendingTickListEntriesTreeSet.iterator();
        while(iterator.hasNext()) {
            NextTickListEntry entry = iterator.next();
            if (!p_72955_1_ && entry.scheduledTime > worldInfo.getWorldTotalTime()) {
                break;
            }

            if (!chunkExists(entry.xCoord >> 4, entry.zCoord >> 4)) {
                iterator.remove();
                pendingTickListEntriesHashSet.remove(entry);
                if (Compat.isCoreTweaksPresent()) {
                    CoreTweaksCompat.removeTickEntry(this, entry);
                }
            } else if (hodgepodge$shouldProcessTick(entry)) {
                iterator.remove();
                pendingTickListEntriesHashSet.remove(entry);
                if (Compat.isCoreTweaksPresent()) {
                    CoreTweaksCompat.removeTickEntry(this, entry);
                }
                pendingTickListEntriesThisTick.add(entry);
                if (pendingTickListEntriesThisTick.size() >= 1000) {
                    break;
                }
            }
        }
    }
}
