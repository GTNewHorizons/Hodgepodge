package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.FixesConfig;

@Mixin(Chunk.class)
public class MixinChunk {

    @Shadow
    public World worldObj;

    @Shadow
    @Final
    public int xPosition;

    @Shadow
    @Final
    public int zPosition;

    @Inject(method = "func_150812_a", at = @At("HEAD"), cancellable = true)
    void hodgepodge$validateTileInBounds(int inChunkX, int inChunkY, int inChunkZ, TileEntity tile, CallbackInfo ci) {
        if (inChunkX < 0 || inChunkX >= 16 || inChunkY < 0 || inChunkY >= 256 || inChunkZ < 0 || inChunkZ >= 16) {
            int dimension = Integer.MIN_VALUE;
            try {
                dimension = this.worldObj.provider.dimensionId;
            } catch (Throwable t) {
                // no-op
            }
            String nbt;
            try {
                final NBTTagCompound tag = new NBTTagCompound();
                tile.writeToNBT(tag);
                nbt = tag.toString();
            } catch (Exception e) {
                nbt = "<error happened when serializing to string: " + e.getMessage() + ">";
            }
            final String message = String.format(
                    "Tile entity of type %s (%s) reports coordinates of (%d, %d, %d) that lie outside of the bounds of chunk (x=%d, z=%d) in dimension %d - ending up with illegal in-chunk coordinates of (%d, %d, %d). Serialized NBT of the offending TE: %s",
                    tile.getClass().getName(),
                    tile.getClass().getProtectionDomain().getCodeSource().getLocation(),
                    tile.xCoord,
                    tile.yCoord,
                    tile.zCoord,
                    this.xPosition,
                    this.zPosition,
                    dimension,
                    inChunkX,
                    inChunkY,
                    inChunkZ,
                    nbt);
            final IllegalArgumentException e = new IllegalArgumentException(message);
            Common.log.fatal("{}", message, e);
            if (FixesConfig.earlyChunkTileCoordinateCheckDestructive) {
                ci.cancel();
            } else {
                throw e;
            }
        }
    }
}
