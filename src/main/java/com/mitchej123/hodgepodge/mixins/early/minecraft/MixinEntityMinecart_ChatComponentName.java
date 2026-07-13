package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityMinecart.class)
public abstract class MixinEntityMinecart_ChatComponentName extends Entity {

    private MixinEntityMinecart_ChatComponentName(World worldIn) {
        super(worldIn);
    }

    @Shadow
    private String entityName;

    @Override
    public IChatComponent func_145748_c_() {
        if (this.entityName != null) {
            return new ChatComponentText(this.entityName);
        } else {
            return super.func_145748_c_();
        }
    }
}
