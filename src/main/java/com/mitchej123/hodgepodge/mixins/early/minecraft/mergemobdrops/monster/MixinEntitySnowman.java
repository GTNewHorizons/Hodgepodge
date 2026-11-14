package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Items;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntitySnowman.class)
public abstract class MixinEntitySnowman extends Entity {

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        int snowballCount = this.rand.nextInt(16);
        if (snowballCount > 0) {
            this.dropItem(Items.snowball, snowballCount);
        }
    }

    private MixinEntitySnowman(World worldIn) {
        super(worldIn);
    }
}
