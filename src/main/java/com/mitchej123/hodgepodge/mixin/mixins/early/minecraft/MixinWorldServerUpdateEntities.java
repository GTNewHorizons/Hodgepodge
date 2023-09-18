package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft;

import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldServer.class)
public abstract class MixinWorldServerUpdateEntities extends World {

    @Shadow
    private int updateEntityTick;

    /**
     * @author kubasz
     * @reason Vanilla skipping the update when no players are in the dimension causes memory leaks
     */
    @Overwrite
    public void updateEntities() {
        if (this.playerEntities.isEmpty() && getPersistentChunks().isEmpty()) {
            if (this.updateEntityTick++ >= 1200) {
                // Make sure to run cleanup code every 10s
                if (this.updateEntityTick % 200 == 0) {
                    super.updateEntities();
                }
                return;
            }
        } else {
            this.updateEntityTick = 0;
        }

        super.updateEntities();
    }

    private MixinWorldServerUpdateEntities(ISaveHandler p_i45368_1_, String p_i45368_2_, WorldProvider p_i45368_3_,
            WorldSettings p_i45368_4_, Profiler p_i45368_5_) {
        // Needed because we're extending from World
        super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
    }
}
