package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
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

import java.util.ArrayList;
import java.util.List;

import static com.mitchej123.hodgepodge.Common.log;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP extends EntityPlayer implements ICrafting {

    @Unique
    private final List<ObjectImmutableList<Chunk>> hodgepodge$chunkSends = new ObjectArrayList<>();
    @Unique
    private int hodgepodge$totalChunks = 0;
    @Unique
    private static final S26PacketMapChunkBulk hodgepodge$dummyPacket = new S26PacketMapChunkBulk();

    @Shadow
    public abstract WorldServer getServerForPlayer();

    public MixinEntityPlayerMP(World p_i45324_1_, GameProfile p_i45324_2_) {
        super(p_i45324_1_, p_i45324_2_);
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;size()I", ordinal = 0))
    private int hodgepodge$numChunks(ArrayList<Chunk> chunks) {
        return this.hodgepodge$totalChunks;
    }

    @Inject(method = "onUpdate", at = @At(value = "NEW", target = "()Ljava/util/ArrayList;"))
    private void hodgepodge$setNumChunks(CallbackInfo ci) {
        this.hodgepodge$totalChunks = 0;
        this.hodgepodge$chunkSends.clear();
    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z"))
    private void hodgepodge$incNumChunks(CallbackInfo ci) {
        ++this.hodgepodge$totalChunks;
    }

    @ModifyExpressionValue(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/server/S26PacketMapChunkBulk;func_149258_c()I"))
    private int hodgepodge$maxChunks(int value) {
        return value * 5;
    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;", ordinal = 1))
    private void hodgepodge$fasterChunkSend(CallbackInfo ci, @Local(name = "arraylist") ArrayList<Chunk> chunks) {
        if (chunks.size() == S26PacketMapChunkBulk.func_149258_c()) {
            this.hodgepodge$chunkSends.add(new ObjectImmutableList<>(chunks));
            chunks.clear();
        }
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;isEmpty()Z"))
    private boolean hodgepodge$areChunksEmpty(ArrayList<Chunk> chunks) {
        return this.hodgepodge$chunkSends.isEmpty();
    }

    @Redirect(method = "onUpdate", at = @At(value = "NEW", target = "(Ljava/util/List;)Lnet/minecraft/network/play/server/S26PacketMapChunkBulk;"))
    private S26PacketMapChunkBulk hodgepodge$sendChunks(List<Chunk> extracted) {
        return hodgepodge$dummyPacket;
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetHandlerPlayServer;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private void hodgepodge$sendChunks(NetHandlerPlayServer instance, Packet enumchatvisibility) {
        for (int i = 0; i < hodgepodge$chunkSends.size(); ++i) {
            instance.sendPacket(new S26PacketMapChunkBulk(this.hodgepodge$chunkSends.get(i)));
        }
    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/ArrayList;iterator()Ljava/util/Iterator;", ordinal = 1), cancellable = true)
    private void hodgepodge$fasterChunkSend(CallbackInfo ci) {
        for (int i = 0; i < this.hodgepodge$chunkSends.size(); ++i) {
            final ObjectImmutableList<Chunk> chunks = this.hodgepodge$chunkSends.get(i);
            for (int j = 0; j < chunks.size(); ++j) {
                final Chunk chunk = chunks.get(j);
                this.getServerForPlayer().getEntityTracker().func_85172_a(((EntityPlayerMP) (Object) this), chunk);
                MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.Watch(chunk.getChunkCoordIntPair(), ((EntityPlayerMP) (Object) this)));
            }
        }

        if (this.hodgepodge$totalChunks > S26PacketMapChunkBulk.func_149258_c())
            log.info("Sent {} chunks to the client", this.hodgepodge$totalChunks);
        ci.cancel();
    }
}
