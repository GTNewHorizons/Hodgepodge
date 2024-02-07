package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Comparator;
import java.util.PriorityQueue;

import net.minecraft.client.renderer.Tessellator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Tessellator.class)
public class MixinTessellator {

    @Redirect(
            method = "getVertexState",
            at = @At(value = "NEW", target = "(ILjava/util/Comparator;)Ljava/util/PriorityQueue;"))
    public PriorityQueue<Integer> hodgepodge$preserveQuadOrder(int bufferIndex, Comparator<Integer> comparator, float x,
            float y, float z) {
        return new PriorityQueue<>(bufferIndex, comparator.thenComparingInt(o -> o));
    }
}
