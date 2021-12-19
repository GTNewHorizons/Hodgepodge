package com.mitchej123.hodgepodge.mixins.minecraft;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public class MixinWorldLightValue {
	@Redirect(
			method = "getBlockLightValue_do",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBlockLightValue(IIII)I")
	)
	public int getBlockLightValueNullSafe(Chunk chunk, int par1, int par2, int par3, int par4) {
		return chunk != null ? chunk.getBlockLightValue(par1, par2, par3, par4) : 0;
	}
}
