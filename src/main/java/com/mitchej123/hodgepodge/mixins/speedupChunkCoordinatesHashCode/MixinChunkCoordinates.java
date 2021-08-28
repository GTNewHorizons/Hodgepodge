package com.mitchej123.hodgepodge.mixins.speedupChunkCoordinatesHashCode;

import net.minecraft.util.ChunkCoordinates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkCoordinates.class)
public class MixinChunkCoordinates {
    @Shadow
    public int posX;

    @Shadow
    public int posY;

    @Shadow
    public int posZ;

    /**
     * @author mitchej123
     * //@reason Swap out the default (terrible) hashCode function with a better one
     */
    @Overwrite()
    public int hashCode() {
        return this.posX * 8976890 + this.posY * 981131 + this.posZ;
    }
    
}
