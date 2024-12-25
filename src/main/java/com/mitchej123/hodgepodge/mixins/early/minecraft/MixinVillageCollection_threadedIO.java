package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.mixins.interfaces.SafeWriteNBT;
import net.minecraft.village.VillageCollection;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VillageCollection.class)
public class MixinVillageCollection_threadedIO implements SafeWriteNBT {
    // Added interface
}
