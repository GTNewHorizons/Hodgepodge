package com.mitchej123.hodgepodge.mixins.late.railcraft;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.util.PollutionHelper;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.beta.TileEngineSteamHobby;
import mods.railcraft.common.util.steam.SteamBoiler;
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
@Mixin(SteamBoiler.class)
public class MixinRailcraftBoilerPollution {

    @Shadow(remap = false)
    RailcraftTileEntity tile;

    @Shadow(remap = false)
    boolean isBurning;

    @Inject(method = "tick", at = @At(value = "HEAD"), remap = false)
    private void hodgepodge$tick(int x, CallbackInfo ci) {
        if (!this.isBurning || this.tile == null || this.tile.getWorld() == null) return;
        final World world = this.tile.getWorldObj();
        if ((world.getTotalWorldTime() % 20) == 0) {
            int pollutionAmount;
            if (this.tile instanceof TileMultiBlock)
                pollutionAmount = (((TileMultiBlock) this.tile).getComponents().size() - x)
                        * Common.config.fireboxPollutionAmount;
            else if (this.tile instanceof TileEngineSteamHobby)
                pollutionAmount = Common.config.hobbyistEnginePollutionAmount;
            else pollutionAmount = 40;

            PollutionHelper.addPollution(
                    world.getChunkFromBlockCoords(this.tile.getX(), this.tile.getZ()), pollutionAmount);
        }
    }
}
