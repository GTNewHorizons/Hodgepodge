package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.FontRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FontRenderer.class)
public interface FontRendererAccessor {

    @Invoker("getFormatFromString")
    static String callGetFormatFromString(String s) {
        throw new IllegalStateException("Mixin stub invoked");
    }

}
