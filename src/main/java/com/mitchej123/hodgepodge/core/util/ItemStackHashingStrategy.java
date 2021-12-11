package com.mitchej123.hodgepodge.core.util;

import gnu.trove.strategy.HashingStrategy;
import net.minecraft.item.ItemStack;

/*
 * Strategy to make ItemStacks Hashable
 *  - Taken from https://github.com/hilburn/AdvancedSystemsManager/blob/master/src/main/java/advancedsystemsmanager/flow/execution/buffers/maps/ItemStackHashingStrategy.java
 *    under the the DBaJ (Don't Be a Jerk) non-commercial care-free license. 
 *  (c) hilburn
 */
public class ItemStackHashingStrategy implements HashingStrategy<ItemStack> {
    public static final ItemStackHashingStrategy INSTANCE = new ItemStackHashingStrategy();
    
    @Override
    public int computeHashCode(ItemStack stack) {
        return stack.getItem().hashCode() ^ (stack.getItemDamage() * 31) ^ (stack.hasTagCompound() ? stack.stackTagCompound.hashCode() : 0);
    }

    @Override
    public boolean equals(ItemStack stack1, ItemStack stack2) {
        return stack1 != null && stack1.isItemEqual(stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }
}
