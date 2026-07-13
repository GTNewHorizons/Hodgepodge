package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving_ChatComponentName extends EntityLivingBase {

    private MixinEntityLiving_ChatComponentName(World p_i1594_1_) {
        super(p_i1594_1_);
    }

    @Shadow
    public abstract boolean hasCustomNameTag();

    @Shadow
    public abstract String getCustomNameTag();

    @Override
    public IChatComponent func_145748_c_() {
        if (this.hasCustomNameTag()) {
            return new ChatComponentText(this.getCustomNameTag());
        } else {
            return super.func_145748_c_();
        }
    }
}
