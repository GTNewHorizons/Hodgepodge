package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import thaumcraft.common.tiles.TileArcaneBore;

@Mixin(value = TileArcaneBore.class, remap = true)
public abstract class MixinBoreVisFrequencyReduction {

    @Shadow
    public abstract boolean gettingPower();

    @Shadow
    private float speedyTime;

    @Unique
    private int gTNHLib$cooldown = 0;
    @Unique
    private float gTNHLib$cachedTime = 0;

    // Add a cooldown to the if statement.
    @ModifyExpressionValue(
            method = "updateEntity",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/World;isRemote:Z",
                    ordinal = 0,
                    opcode = Opcodes.GETFIELD))
    public boolean powerAndSleep(boolean isRemote) {
        // The returned value is inverted.

        // Client thread, return
        if (isRemote) return true;

        // Cooldown expired, do checks
        if (gTNHLib$cooldown-- <= 0) {
            if (this.gettingPower()) {
                // Do a cvis check, sleep 10s
                gTNHLib$cooldown = 10 * 20;
                // double cooldown if it never had cvis
                // imprecision means it will never be 0 again. I think.
                gTNHLib$cooldown *= this.speedyTime == 0 ? 2 : 1;
                return false;
            } else {
                // Block isn't powered, so do cvis thing but choke to 30s instead
                gTNHLib$cooldown = 30 * 20;
                // double cooldown if it never had cvis
                gTNHLib$cooldown *= this.speedyTime == 0 ? 2 : 1;
                return false;
            }
        }

        // if it has any cached speedyTime, but less than the max, skip cooldown
        // Speedy time can be exhausted in 1 second by a constantly-digging bore.
        // if the speedyTime doesn't change since last lookup, increase cooldown to 1/2s
        if (this.speedyTime > 1 && this.speedyTime < 20) {
            if (this.speedyTime == gTNHLib$cachedTime) {
                gTNHLib$cooldown = Math.min(gTNHLib$cooldown, 10);
            } else gTNHLib$cooldown = 0;
            gTNHLib$cachedTime = this.speedyTime;
        }

        // Cooldown's not up, don't do cvis thing.
        return true;
    }

}
