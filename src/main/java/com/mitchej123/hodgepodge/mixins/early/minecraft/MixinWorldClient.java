package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.interfaces.IMixinCleanup;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldClient.class)
public class MixinWorldClient implements IMixinCleanup {
    @Shadow
    private ChunkProviderClient clientChunkProvider;

    public void cleanup() {
        // This keeps a reference to the WorldClient, which keeps a reference to the ClientChunkProvider...
        // which.....<explodes>
        Common.log.info("WorldClient::Cleanup");
        if (this.clientChunkProvider != null) {
            ((IMixinCleanup) this.clientChunkProvider).cleanup();
        }
        this.clientChunkProvider = null;
    }
}
