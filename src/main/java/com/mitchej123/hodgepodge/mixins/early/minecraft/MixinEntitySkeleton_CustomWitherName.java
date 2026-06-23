package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntitySkeleton.class)
public abstract class MixinEntitySkeleton_CustomWitherName extends EntityMob {

    private MixinEntitySkeleton_CustomWitherName(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    @Shadow
    public abstract int getSkeletonType();

    @Override
    public String getCommandSenderName() {
        if (this.getSkeletonType() == 1) {
            return StatCollector.translateToLocal("entity.Skeleton.wither.name");
        } else {
            return super.getCommandSenderName();
        }
    }

    @Override
    public IChatComponent func_145748_c_() {
        if (this.getSkeletonType() == 1) {
            return new ChatComponentTranslation("entity.Skeleton.wither.name");
        } else {
            return super.func_145748_c_();
        }
    }
}
