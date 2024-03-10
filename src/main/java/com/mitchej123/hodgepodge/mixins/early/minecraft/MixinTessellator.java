package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Comparator;

import net.minecraft.client.renderer.Tessellator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Tessellator.class)
public class MixinTessellator {

    @ModifyArg(
            at = @At(
                    remap = false,
                    target = "Ljava/util/PriorityQueue;<init>(ILjava/util/Comparator;)V",
                    value = "INVOKE"),
            method = "getVertexState")
    private Comparator<Integer> hodgepodge$preserveQuadOrder(Comparator<Integer> comparator) {
        return comparator.thenComparingInt(o -> o);
    }
}
