package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.mixins.hooks.BlockLookupHooks;
import com.mitchej123.hodgepodge.mixins.interfaces.BlockExt_FastLookup;

@Mixin(value = Block.class, priority = 1100)
public class MixinBlock_FastLookup implements BlockExt_FastLookup {

    @Unique
    private int hodgepodge$blockId = -1;

    @Override
    public int hodgepodge$getBlockId() {
        return hodgepodge$blockId;
    }

    @Override
    public void hodgepodge$setBlockId(int id) {
        hodgepodge$blockId = id;
    }

    /**
     * @author hodgepodge
     * @reason Direct array lookup instead of registry dispatch
     */
    @Overwrite
    public static Block getBlockById(int id) {
        return BlockLookupHooks.getBlockById(id);
    }

    /**
     * @author hodgepodge
     * @reason Direct field read instead of registry dispatch
     */
    @Overwrite
    public static int getIdFromBlock(Block block) {
        return BlockLookupHooks.getIdFromBlock(block);
    }
}
