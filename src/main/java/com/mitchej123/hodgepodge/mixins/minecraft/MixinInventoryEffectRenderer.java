package com.mitchej123.hodgepodge.mixins.minecraft;

import net.minecraft.client.renderer.InventoryEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(InventoryEffectRenderer.class)
public class MixinInventoryEffectRenderer {

    @ModifyConstant(method = "initGui", constant = @Constant(intValue = 160, ordinal = 0))
    public int Hodgepodge$fixPotionOffset1(int i) {
        return 0;
    }

    @ModifyConstant(method = "initGui", constant = @Constant(intValue = 200, ordinal = 0))
    public int Hodgepodge$fixPotionOffset2(int i) {
        return 0;
    }
}
