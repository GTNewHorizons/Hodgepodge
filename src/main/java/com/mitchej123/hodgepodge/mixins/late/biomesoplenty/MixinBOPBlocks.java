package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import com.mitchej123.hodgepodge.common.biomesoplenty.blocks.BlockBOPFence;
import com.mitchej123.hodgepodge.common.biomesoplenty.blocks.BlockBOPFenceGate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static biomesoplenty.common.core.BOPBlocks.registerBlock;
import com.mitchej123.hodgepodge.common.biomesoplenty.blocks.BOPBlocks;

@Mixin(value = biomesoplenty.common.core.BOPBlocks.class, remap = false)
public class MixinBOPBlocks {
    @Inject(method = "registerBlocks", at = @At("RETURN"))
    private static void registerBlocks(CallbackInfo ci) {
        BOPBlocks.sacredoakFence = registerBlock(new BlockBOPFence("sacredoak").setBlockName("sacredoakFence"));
        BOPBlocks.cherryFence = registerBlock(new BlockBOPFence("cherry").setBlockName("cherryFence"));
        BOPBlocks.darkFence = registerBlock(new BlockBOPFence("dark").setBlockName("darkFence"));
        BOPBlocks.firFence = registerBlock(new BlockBOPFence("fir").setBlockName("firFence"));
        BOPBlocks.etherealFence = registerBlock(new BlockBOPFence("ethereal").setBlockName("etherealFence"));
        BOPBlocks.magicFence = registerBlock(new BlockBOPFence("magic").setBlockName("magicFence"));
        BOPBlocks.mangroveFence = registerBlock(new BlockBOPFence("mangrove").setBlockName("mangroveFence"));
        BOPBlocks.palmFence = registerBlock(new BlockBOPFence("palm").setBlockName("palmFence"));
        BOPBlocks.redwoodFence = registerBlock(new BlockBOPFence("redwood").setBlockName("redwoodFence"));
        BOPBlocks.willowFence = registerBlock(new BlockBOPFence("willow").setBlockName("willowFence"));
        BOPBlocks.willowFence = registerBlock(new BlockBOPFence("bamboothatching").setBlockName("bamboothatchingFence"));
        BOPBlocks.pineFence = registerBlock(new BlockBOPFence("pine").setBlockName("pineFence"));
        BOPBlocks.hellBarkFence = registerBlock(new BlockBOPFence("hell_bark").setBlockName("hellBarkFence"));
        BOPBlocks.jacarandaFence = registerBlock(new BlockBOPFence("jacaranda").setBlockName("jacarandaFence"));
        BOPBlocks.mahoganyFence = registerBlock(new BlockBOPFence("mahogany").setBlockName("mahoganyFence"));

        BOPBlocks.sacredoakFenceGate = registerBlock(new BlockBOPFenceGate("sacredoak").setBlockName("sacredoakFenceGate"));
        BOPBlocks.cherryFenceGate = registerBlock(new BlockBOPFenceGate("cherry").setBlockName("cherryFenceGate"));
        BOPBlocks.darkFenceGate = registerBlock(new BlockBOPFenceGate("dark").setBlockName("darkFenceGate"));
        BOPBlocks.firFenceGate = registerBlock(new BlockBOPFenceGate("fir").setBlockName("firFenceGate"));
        BOPBlocks.etherealFenceGate = registerBlock(new BlockBOPFenceGate("ethereal").setBlockName("etherealFenceGate"));
        BOPBlocks.magicFenceGate = registerBlock(new BlockBOPFenceGate("magic").setBlockName("magicFenceGate"));
        BOPBlocks.mangroveFenceGate = registerBlock(new BlockBOPFenceGate("mangrove").setBlockName("mangroveFenceGate"));
        BOPBlocks.palmFenceGate = registerBlock(new BlockBOPFenceGate("palm").setBlockName("palmFenceGate"));
        BOPBlocks.redwoodFenceGate = registerBlock(new BlockBOPFenceGate("redwood").setBlockName("redwoodFenceGate"));
        BOPBlocks.willowFenceGate = registerBlock(new BlockBOPFenceGate("willow").setBlockName("willowFenceGate"));
        BOPBlocks.willowFenceGate = registerBlock(new BlockBOPFenceGate("bamboothatching").setBlockName("bamboothatchingFenceGate"));
        BOPBlocks.pineFenceGate = registerBlock(new BlockBOPFenceGate("pine").setBlockName("pineFenceGate"));
        BOPBlocks.hellBarkFenceGate = registerBlock(new BlockBOPFenceGate("hell_bark").setBlockName("hellBarkFenceGate"));
        BOPBlocks.jacarandaFenceGate = registerBlock(new BlockBOPFenceGate("jacaranda").setBlockName("jacarandaFenceGate"));
        BOPBlocks.mahoganyFenceGate = registerBlock(new BlockBOPFenceGate("mahogany").setBlockName("mahoganyFenceGate"));
    }
}
