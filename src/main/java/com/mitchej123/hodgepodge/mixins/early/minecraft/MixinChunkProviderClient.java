package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.interfaces.IMixinCleanup;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkProviderClient.class)
public class MixinChunkProviderClient implements IMixinCleanup {

    @Shadow
    private List<Chunk> chunkListing;

    @Shadow
    private LongHashMap chunkMapping;

    @Shadow
    private World worldObj;

    @Override
    public void cleanup() {
        Common.log.info("ChunkProviderClient::Cleanup");
        // Make sure we don't have any dangling objects that have a reference to the worldObj
        this.chunkListing = new ArrayList<>();
        this.chunkMapping = new LongHashMap();
        this.worldObj = null;
    }
}
