package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(GuiContainerCreative.class)
public class MixinGuiContainerCreative_TabTitle {

    @ModifyExpressionValue(
            method = "drawGuiContainerForegroundLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/creativetab/CreativeTabs;getTranslatedTabLabel()Ljava/lang/String;"))
    private String hodgepodge$colorTitle(String original) {
        String colorKey = "itemGroup.creative.gui.title";
        if (StatCollector.canTranslate(colorKey)) {
            String color = StatCollector.translateToLocal(colorKey);
            if (!color.isEmpty()) {
                return color + original;
            }
        }
        return original;
    }
}
