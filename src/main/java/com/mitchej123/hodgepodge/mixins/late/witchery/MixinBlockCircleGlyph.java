package com.mitchej123.hodgepodge.mixins.late.witchery;

import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.emoniph.witchery.blocks.BlockCircleGlyph;

@Mixin(BlockCircleGlyph.class)
public class MixinBlockCircleGlyph {

    @Shadow(remap = false)
    private IIcon[] icons;

    @ModifyConstant(method = "getIcon(II)Lnet/minecraft/util/IIcon;", constant = @Constant(intValue = 12))
    private int hodgepodge$fixMetadataClamp(int oldValue) {
        return icons.length - 1;
    }

}
