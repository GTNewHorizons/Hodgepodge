
package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.monster;

import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityMagmaCube.class)
public abstract class MixinEntityMagmaCube extends EntitySlime {

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        Item item = this.getDropItem();
        if (item != null && this.getSlimeSize() > 1) {
            int count = this.rand.nextInt(4) - 2;
            if (lootingLevel > 0) {
                count += this.rand.nextInt(lootingLevel + 1);
            }
            if (count > 0) {
                this.dropItem(item, count);
            }
        }
    }

    private MixinEntityMagmaCube(World worldIn) {
        super(worldIn);
    }
}
