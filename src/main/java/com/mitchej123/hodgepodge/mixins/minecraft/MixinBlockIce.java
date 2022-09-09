package com.mitchej123.hodgepodge.mixins.minecraft;

import gregtech.api.items.GT_MetaGenerated_Tool;
import gregtech.common.tools.GT_Tool_Saw;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockIce.class)
public class MixinBlockIce {

    /**
     * Fixes the gregtech bug where breaking an ice block with a saw or chainsaw drops the block and spawns a water source
     */
    @Redirect(
            method = "harvestBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;)Z"))
    public boolean hodgepodge$fixWaterSpawnWithGTSaw(
            World instance,
            int x,
            int y,
            int z,
            Block block,
            World p_149636_1_,
            EntityPlayer p_149636_2_,
            int p_149636_3_,
            int p_149636_4_,
            int p_149636_5_,
            int p_149636_6_) {
        final ItemStack itemStack = p_149636_2_.getCurrentEquippedItem();
        if (itemStack == null
                || !(itemStack.getItem() instanceof GT_MetaGenerated_Tool)
                || !(((GT_MetaGenerated_Tool) itemStack.getItem()).getToolStats(itemStack) instanceof GT_Tool_Saw)) {
            return instance.setBlock(x, y, z, block);
        }
        return false;
    }
}
