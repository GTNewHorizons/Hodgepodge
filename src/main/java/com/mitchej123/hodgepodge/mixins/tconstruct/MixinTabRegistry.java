package com.mitchej123.hodgepodge.mixins.tconstruct;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tconstruct.client.tabs.TabRegistry;

@Mixin(TabRegistry.class)
public class MixinTabRegistry {

    @Redirect(
            method = "guiPostInit",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Ltconstruct/client/tabs/TabRegistry;getPotionOffset()I",
                            remap = false),
            remap = false)
    public int hodgepodge$fixPotionOffset() {
        return 0;
    }
}
