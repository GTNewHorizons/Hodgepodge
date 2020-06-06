package com.github.bartimaeusnek.modmixins.mixins.railcraft.boiler

import gregtech.common.GT_Pollution
import mods.railcraft.common.blocks.RailcraftTileEntity
import mods.railcraft.common.blocks.machine.TileMultiBlock
import mods.railcraft.common.blocks.machine.beta.TileEngineSteamHobby
import mods.railcraft.common.util.steam.SteamBoiler
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(value = [SteamBoiler::class], remap = false)
class RailcraftBuilderPollution {

    @Shadow
    private var tile: RailcraftTileEntity? = null

    @Shadow
    private var isBurning: Boolean = false

    @Inject(method = ["tick"], at = [At(value = "HEAD")])
    fun tick(x: Int, c: CallbackInfo) {
        if (this.isBurning)
            tile?.also {
                val pollution =
                        when (it) {
                            is TileMultiBlock -> (tile as TileMultiBlock).components.size * 10
                            is TileEngineSteamHobby -> 5
                            else -> 8
                        }
                GT_Pollution.addPollution(this.tile!!.world.getChunkFromBlockCoords(this.tile!!.x, this.tile!!.z), pollution)
            }
    }
}