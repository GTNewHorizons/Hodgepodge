package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityOcelot.class)
public abstract class MixinEntityOcelot_ChatComponentName extends EntityTameable {

    private MixinEntityOcelot_ChatComponentName(World p_i1604_1_) {
        super(p_i1604_1_);
    }

    @Override
    public IChatComponent func_145748_c_() {
        if (!this.hasCustomNameTag() && this.isTamed()) {
            return new ChatComponentTranslation("entity.Cat.name");
        } else {
            return super.func_145748_c_();
        }
    }
}
