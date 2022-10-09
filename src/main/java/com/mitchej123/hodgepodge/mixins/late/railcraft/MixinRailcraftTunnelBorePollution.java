package com.mitchej123.hodgepodge.mixins.late.railcraft;

import com.mitchej123.hodgepodge.Common;
import gregtech.common.GT_Pollution;
import mods.railcraft.common.carts.EntityTunnelBore;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
 * Merged from ModMixins under the MIT License
 *    Copyright bartimaeusnek & GTNewHorizons
 */
@Mixin(EntityTunnelBore.class)
public abstract class MixinRailcraftTunnelBorePollution extends EntityTunnelBore {
    @Shadow(remap = false)
    boolean active;

    public MixinRailcraftTunnelBorePollution(World world) {
        super(world);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void addPollution(CallbackInfo ci) {
        if (!worldObj.isRemote || !active) return;
        GT_Pollution.addPollution(
                worldObj.getChunkFromBlockCoords((int) posX, (int) posZ), Common.config.tunnelBorePollutionAmount);
    }
}
