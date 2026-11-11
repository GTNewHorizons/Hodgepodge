package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityWitch.class)
public abstract class MixinEntityWitch extends Entity {

    @Shadow
    private static Item[] witchDrops;

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        int drops = this.rand.nextInt(3) + 1;
        for (int i = 0; i < drops; i++) {
            int count = this.rand.nextInt(3);
            Item item = witchDrops[this.rand.nextInt(witchDrops.length)];
            if (lootingLevel > 0) {
                count += this.rand.nextInt(lootingLevel + 1);
            }
            if (count > 0) {
                this.dropItem(item, count);
            }
        }
    }

    private MixinEntityWitch(World worldIn) {
        super(worldIn);
    }
}
