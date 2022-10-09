package com.mitchej123.hodgepodge.mixins.late.galacticraftcore;

import com.mitchej123.hodgepodge.Common;
import gregtech.common.GT_Pollution;
import micdoodle8.mods.galacticraft.api.entity.IRocketType;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
 * Merged from ModMixins under the MIT License
 *    Copyright bartimaeusnek & GTNewHorizons
 */
@Mixin(EntityTieredRocket.class)
public abstract class MixinGalacticraftRocketPollution extends EntityAutoRocket implements IRocketType {

    public MixinGalacticraftRocketPollution(World world) {
        super(world);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void addRocketPollution(CallbackInfo ci) {
        if (this.worldObj.isRemote
                || !(launchPhase == EnumLaunchPhase.LAUNCHED.ordinal()
                        || launchPhase == EnumLaunchPhase.IGNITED.ordinal())) return;
        int pollutionAmount = Common.config.rocketPollutionAmount;

        // Note: Linear instead of growing by powers of 2
        if (launchPhase == EnumLaunchPhase.LAUNCHED.ordinal())
            pollutionAmount = (pollutionAmount * this.getRocketTier());
        else if (launchPhase == EnumLaunchPhase.IGNITED.ordinal())
            pollutionAmount = (pollutionAmount * (this.getRocketTier())) / 100;
        else pollutionAmount = 0;

        GT_Pollution.addPollution(worldObj.getChunkFromBlockCoords((int) posX, (int) posZ), pollutionAmount);
    }
}
