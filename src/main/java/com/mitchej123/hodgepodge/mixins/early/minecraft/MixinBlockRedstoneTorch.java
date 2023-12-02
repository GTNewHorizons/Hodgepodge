package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.BlockRedstoneTorch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin(value = BlockRedstoneTorch.class, remap = false)
public class MixinBlockRedstoneTorch {
    @Shadow private static Map field_150112_b = new WeakHashMap<>();
}
