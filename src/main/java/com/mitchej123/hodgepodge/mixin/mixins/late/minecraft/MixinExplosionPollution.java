package com.mitchej123.hodgepodge.mixin.mixins.late.minecraft;

import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.util.PollutionHelper;

/*
 * Merged from ModMixins under the MIT License Copyright bartimaeusnek & GTNewHorizons
 */
@Mixin(Explosion.class)
public class MixinExplosionPollution {

    @Shadow
    float explosionSize;

    @Shadow
    World worldObj;

    @Shadow
    double explosionX;

    @Shadow
    double explosionZ;

    @Inject(method = "doExplosionA", at = @At(value = "TAIL"))
    public void hodgepodge$addExplosionPollution(CallbackInfo ci) {
        if (!this.worldObj.isRemote) PollutionHelper.addPollution(
                this.worldObj.getChunkFromBlockCoords((int) this.explosionX, (int) this.explosionZ),
                (int) Math.ceil(explosionSize * Common.config.explosionPollutionAmount));
    }
}
