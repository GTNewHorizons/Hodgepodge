package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP extends EntityPlayer {

    @Shadow
    @Final
    public List<ChunkCoordIntPair> loadedChunks;

    @Shadow
    public NetHandlerPlayServer playerNetServerHandler;

    @Shadow
    protected abstract void func_147097_b(TileEntity te);

    // Cache these lists so they don't have to be reallocated every onUpdate
    @Unique
    private final ObjectArrayList<ObjectImmutableList<Chunk>> hodgepodge$chunkSends = new ObjectArrayList<>();
    @Unique
    private final ObjectArrayList<Chunk> hodgepodge$rollingChunks = new ObjectArrayList<>();
    @Unique
    private final ObjectArrayList<TileEntity> hodgepodge$rollingTEs = new ObjectArrayList<>();

    @Shadow
    public abstract WorldServer getServerForPlayer();

    @Shadow @Final private static Logger logger;

    public MixinEntityPlayerMP(World world, GameProfile profile) {
        super(world, profile);
    }

    /**
     * This injects just before where vanilla handles chunk sending and returns before then. This will mess up any
     * mixins that target the tail of this function, but other solutions are similarly invasive.
     */
    @Inject(method = "onUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayerMP;loadedChunks:Ljava/util/List;", shift = At.Shift.BEFORE, ordinal = 0), cancellable = true)
    private void hodgepodge$replaceChunkSending(CallbackInfo ci) {
        if (loadedChunks.isEmpty()) { ci.cancel(); return; }

        int numChunks = 0;
        final Iterator<ChunkCoordIntPair> allChunks = loadedChunks.iterator();
        final int chunksPerPacket = S26PacketMapChunkBulk.func_149258_c();

        // Gather chunks to send, unload chunks outside of range
        while (allChunks.hasNext() && numChunks < SpeedupsConfig.maxSendSpeed) {
            final ChunkCoordIntPair ccip = allChunks.next();
            if (ccip == null) {
                allChunks.remove(); continue;
            }

            if (!worldObj.blockExists(ccip.chunkXPos << 4, 0, ccip.chunkZPos << 4)) {
                continue;
            }

            final Chunk chunk = worldObj.getChunkFromChunkCoords(ccip.chunkXPos, ccip.chunkZPos);
            if (!chunk.func_150802_k()) { // chunk.isNotEmpty()
                continue;
            }

            // If there's enough to fill a packet, overflow it
            if (hodgepodge$rollingChunks.size() == chunksPerPacket) {
                hodgepodge$chunkSends.add(new ObjectImmutableList<>(hodgepodge$rollingChunks));
                hodgepodge$rollingChunks.clear();
            }
            hodgepodge$rollingChunks.add(chunk);
            hodgepodge$rollingTEs.addAll(((WorldServer) worldObj).func_147486_a(ccip.chunkXPos * 16, 0, ccip.chunkZPos * 16, ccip.chunkXPos * 16 + 15, 256, ccip.chunkZPos * 16 + 15));
            allChunks.remove();
            numChunks++;
        }

        // Since it only gets cleared when it's full, rollingChunks always holds the last ones
        hodgepodge$chunkSends.add(new ObjectImmutableList<>(hodgepodge$rollingChunks));

        if (numChunks > 0) {
            for (int i = 0; i < hodgepodge$chunkSends.size(); ++i) {
                playerNetServerHandler.sendPacket(new S26PacketMapChunkBulk(hodgepodge$chunkSends.get(i)));
            }

            for (TileEntity tileentity : hodgepodge$rollingTEs) {
                func_147097_b(tileentity);
            }

            for (int i = 0; i < numChunks; ++i) {
                final int div = i / chunksPerPacket;
                final int rem = i % chunksPerPacket;
                final Chunk chunk = hodgepodge$chunkSends.get(div).get(rem);
                getServerForPlayer().getEntityTracker().func_85172_a((EntityPlayerMP) (Object) this, chunk);
                MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.Watch(chunk.getChunkCoordIntPair(), (EntityPlayerMP) (Object) this));
            }
        }
        hodgepodge$chunkSends.clear();
        hodgepodge$rollingChunks.clear();
        hodgepodge$rollingTEs.clear();
        ci.cancel();
    }
}
