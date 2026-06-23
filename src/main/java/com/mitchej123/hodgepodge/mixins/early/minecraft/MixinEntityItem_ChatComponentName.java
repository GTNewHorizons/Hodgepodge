package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem_ChatComponentName extends Entity {

    private MixinEntityItem_ChatComponentName(World worldIn) {
        super(worldIn);
    }

    @Shadow
    public abstract ItemStack getEntityItem();

    @Override
    public IChatComponent func_145748_c_() {
        return new ChatComponentTranslation("item." + this.getEntityItem().getUnlocalizedName());
    }
}
