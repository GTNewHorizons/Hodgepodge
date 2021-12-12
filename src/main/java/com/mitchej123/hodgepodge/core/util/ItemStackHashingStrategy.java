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
        // Purposely allowing meta to collide (stupid wildcard), and resolving via equals
        return stack.getItem().hashCode()*31;
    }

    @Override
    public boolean equals(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null) return false;
        return stack1.getItem() == stack2.getItem() && 
            (stack1.getItemDamage() == stack2.getItemDamage() || stack2.getItemDamage() == Short.MAX_VALUE || stack1.getItemDamage() == Short.MAX_VALUE);
    }
}
