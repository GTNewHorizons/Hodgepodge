package com.mitchej123.hodgepodge.mixins.early.minecraft.textures.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.RenderBlockFluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.textures.AnimationsRenderUtils;

@Mixin(value = RenderBlockFluid.class, remap = false)
public abstract class MixinRenderBlockFluid {

    @Unique
    private IBlockAccess currentBlockAccess;

    @Inject(method = "renderWorldBlock", at = @At(value = "HEAD"))
    private void hodgepodge$saveCurrentBlockAccess(IBlockAccess world, int x, int y, int z, Block block, int modelId,
            RenderBlocks renderer, CallbackInfoReturnable<Boolean> cir) {
        currentBlockAccess = world;
    }

    /**
     * @author laetansky
     * @reason mark texture for update
     */
    @Inject(at = @At(ordinal = 0, value = "RETURN"), method = "getIcon")
    private void hodgepodge$markBlockTextureForUpdate(IIcon icon, CallbackInfoReturnable<IIcon> cir) {
        AnimationsRenderUtils.markBlockTextureForUpdate(icon, currentBlockAccess);
    }
}
