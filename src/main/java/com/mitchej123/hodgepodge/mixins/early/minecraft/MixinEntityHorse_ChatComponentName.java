package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityHorse.class)
public abstract class MixinEntityHorse_ChatComponentName extends EntityAnimal {

    private MixinEntityHorse_ChatComponentName(World p_i1681_1_) {
        super(p_i1681_1_);
    }

    @Shadow
    public abstract int getHorseType();

    @Override
    public IChatComponent func_145748_c_() {
        if (!this.hasCustomNameTag()) {
            return new ChatComponentTranslation(switch (this.getHorseType()) {
                case 1 -> "entity.donkey.name";
                case 2 -> "entity.mule.name";
                case 3 -> "entity.zombiehorse.name";
                case 4 -> "entity.skeletonhorse.name";
                default -> "entity.horse.name";
            });
        } else {
            return super.func_145748_c_();
        }
    }
}
