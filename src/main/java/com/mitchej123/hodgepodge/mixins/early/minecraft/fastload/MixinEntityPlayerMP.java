package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import static com.mitchej123.hodgepodge.Common.log;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.ExtEntityPlayerMP;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;
import com.mojang.authlib.GameProfile;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP extends EntityPlayer implements ICrafting, ExtEntityPlayerMP {

    @Unique
    private final List<ObjectImmutableList<Chunk>> hodgepodge$chunkSends = new ObjectArrayList<>();
    @Unique
    private final List<Chunk> hodgepodge$rollingChunks = new ObjectArrayList<>();
    @Unique
    private final List<TileEntity> hodgepodge$rollingTEs = new ObjectArrayList<>();
    @Unique
    private int hodgepodge$totalChunks = 0;
    @Unique
    private static final S26PacketMapChunkBulk hodgepodge$dummyPacket = new S26PacketMapChunkBulk();
    @Unique
    private boolean hodgepodge$isThrottled = false;
    @Unique
    private boolean hodgepodge$wasThrottled = false;
    @Unique
    private final LongOpenHashSet hodgepodge$loadedChunks = new LongOpenHashSet();
    @Unique
    private final LongOpenHashSet hodgepodge$chunksToLoad = new LongOpenHashSet();

    @Override
    public void setThrottled(boolean val) {
        this.hodgepodge$isThrottled = val;
    }

    @Override
    public boolean isThrottled() {
        return this.hodgepodge$isThrottled;
    }

    @Override
    public void setWasThrottled(boolean val) {
        this.hodgepodge$wasThrottled = val;
    }

    @Override
    public boolean wasThrottled() {
        return this.hodgepodge$wasThrottled;
    }

    @Override
    public LongOpenHashSet chunksToLoad() {
        return this.hodgepodge$chunksToLoad;
    }

    @Override
    public LongOpenHashSet loadedChunks() {
        return this.hodgepodge$loadedChunks;
    }

    @Shadow
    public abstract WorldServer getServerForPlayer();

    @Shadow
    public NetHandlerPlayServer playerNetServerHandler;

    @Shadow
    protected abstract void func_147097_b(TileEntity p_147097_1_);

    public MixinEntityPlayerMP(World p_i45324_1_, GameProfile p_i45324_2_) {
        super(p_i45324_1_, p_i45324_2_);
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 1))
    private boolean hodgepodge$skipOGChunkList(List instance) {
        return true;
    }

    @Inject(method = "onUpdate", at = @At(value = "TAIL"))
    private void hodgepodge$replaceChunkList(CallbackInfo ci) {

        if (this.hodgepodge$chunksToLoad.longStream().anyMatch(this.hodgepodge$loadedChunks::contains))
            log.warn("sending duplicate!!!");

        final int chunksPPacket = S26PacketMapChunkBulk.func_149258_c();
        final LongIterator chunkKeys = this.hodgepodge$chunksToLoad.longIterator();
        Chunk chunk;

        // For every chunk...
        while (chunkKeys.hasNext()) {

            final long key = chunkKeys.nextLong();
            final int cx = ChunkPosUtil.getPackedX(key);
            final int cz = ChunkPosUtil.getPackedZ(key);

            // Only send the chunk if it exists
            if (this.worldObj.blockExists(cx << 4, 0, cz << 4)) {
                chunk = this.worldObj.getChunkFromChunkCoords(cx, cz);

                if (chunk.func_150802_k()) {

                    ++this.hodgepodge$totalChunks;
                    this.hodgepodge$rollingChunks.add(chunk);
                    this.hodgepodge$loadedChunks.add(key);
                    this.hodgepodge$rollingTEs.addAll(
                            ((WorldServer) this.worldObj)
                                    .func_147486_a(cx << 4, 0, cz << 4, (cx << 4) + 15, 256, (cz << 16) + 15));
                    // BugFix: 16 makes it load an extra chunk, which isn't associated with a player, which makes it not
                    // unload unless a player walks near it.
                    chunkKeys.remove();
                }
            }

            // Don't overflow the packet size
            if (this.hodgepodge$rollingChunks.size() == chunksPPacket) {
                this.hodgepodge$chunkSends.add(new ObjectImmutableList<>(this.hodgepodge$rollingChunks));
                this.hodgepodge$rollingChunks.clear();
            }
        }

        // Catch a half-full packet
        if (this.hodgepodge$rollingChunks.size() < chunksPPacket)
            this.hodgepodge$chunkSends.add(new ObjectImmutableList<>(this.hodgepodge$rollingChunks));

        if (!this.hodgepodge$chunkSends.isEmpty()) {

            for (int i = 0; i < this.hodgepodge$chunkSends.size(); ++i) {
                this.playerNetServerHandler.sendPacket(new S26PacketMapChunkBulk(this.hodgepodge$chunkSends.get(i)));
            }

            for (int i = 0; i < this.hodgepodge$rollingTEs.size(); ++i) {
                this.func_147097_b(this.hodgepodge$rollingTEs.get(i));
            }

            for (int i = 0; i < this.hodgepodge$totalChunks; ++i) {
                chunk = this.hodgepodge$chunkSends.get(i / chunksPPacket).get(i % chunksPPacket);
                this.getServerForPlayer().getEntityTracker().func_85172_a((EntityPlayerMP) (Object) this, chunk);
                MinecraftForge.EVENT_BUS
                        .post(new ChunkWatchEvent.Watch(chunk.getChunkCoordIntPair(), (EntityPlayerMP) (Object) this));
            }
        }

        if (this.hodgepodge$totalChunks > S26PacketMapChunkBulk.func_149258_c())
            log.info("Sent {} chunks to the client", this.hodgepodge$totalChunks);

        this.hodgepodge$totalChunks = 0;
        this.hodgepodge$chunkSends.clear();
        this.hodgepodge$rollingChunks.clear();
        this.hodgepodge$rollingTEs.clear();
    }
}
