package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public abstract class MixinWorldServerUpdateEntities extends World {

    @Shadow
    private int updateEntityTick;

    /**
     * @author kubasz
     * @reason Vanilla skipping the update when no players are in the dimension causes memory leaks
     */
    @Inject(at = @At(ordinal = 0, value = "RETURN"), method = "updateEntities")
    private void hodgepodge$updateEntities(CallbackInfo ci) {
        // Make sure to run cleanup code every 10s
        if (this.updateEntityTick % 200 == 0) {
            super.updateEntities();
        }
    }

    private MixinWorldServerUpdateEntities(ISaveHandler p_i45368_1_, String p_i45368_2_, WorldProvider p_i45368_3_,
            WorldSettings p_i45368_4_, Profiler p_i45368_5_) {
        // Needed because we're extending from World
        super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
    }
}
