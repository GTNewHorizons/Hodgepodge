package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.flatid;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.mixins.interfaces.HasID;

@Mixin({ Block.class, Item.class })
public class MixinEmbedIDs implements HasID {

    @Unique
    private int hodgepodge$id = -1;

    @Override
    public int hodgepodge$getID() {
        return hodgepodge$id;
    }

    @Override
    public void hodgepodge$setID(int id) {
        hodgepodge$id = id;
    }
}
