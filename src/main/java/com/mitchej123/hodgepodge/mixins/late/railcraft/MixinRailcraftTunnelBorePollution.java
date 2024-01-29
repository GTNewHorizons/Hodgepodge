package com.mitchej123.hodgepodge.mixins.late.railcraft;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.PollutionConfig;
import com.mitchej123.hodgepodge.util.PollutionHelper;

import mods.railcraft.common.carts.EntityTunnelBore;

/*
 * Merged from ModMixins under the MIT License Copyright bartimaeusnek & GTNewHorizons
 */
@Mixin(EntityTunnelBore.class)
public abstract class MixinRailcraftTunnelBorePollution extends EntityMinecart {

    @Shadow(remap = false)
    boolean active;

    private MixinRailcraftTunnelBorePollution(World world) {
        super(world);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void hodgepodge$addPollution(CallbackInfo ci) {
        if (!worldObj.isRemote || !active) return;
        PollutionHelper.addPollution(
                worldObj.getChunkFromBlockCoords((int) posX, (int) posZ),
                PollutionConfig.tunnelBorePollutionAmount);
    }
}
