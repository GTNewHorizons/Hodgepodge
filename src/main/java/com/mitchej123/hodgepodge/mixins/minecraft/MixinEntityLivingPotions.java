package com.mitchej123.hodgepodge.mixins.minecraft;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("unused")
@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingPotions extends Entity {

    @Shadow
    @Final
    private HashMap activePotionsMap;

    @Shadow
    private boolean potionsNeedUpdate;

    public MixinEntityLivingPotions(World p_i1594_1_) {
        super(p_i1594_1_);
    }

    /**
     * @author laetansky
     * @reason Fix @see java.util.ConcurrentModificationException being thrown
     * when modifying active potions inside those forge event handlers which could be fired
     * while iterating over active potion effect.
     * Fix is back ported from newer versions.
     */
    @Overwrite
    protected void updatePotionEffects() {
        Iterator iterator = this.activePotionsMap.keySet().iterator();

        try {
            while (iterator.hasNext()) {
                Integer integer = (Integer) iterator.next();
                PotionEffect potioneffect = (PotionEffect) this.activePotionsMap.get(integer);

                if (!potioneffect.onUpdate(((EntityLivingBase) (Object) this))) {
                    if (!this.worldObj.isRemote) {
                        iterator.remove();
                        this.onFinishedPotionEffect(potioneffect);
                    }
                } else if (potioneffect.getDuration() % 600 == 0) {
                    this.onChangedPotionEffect(potioneffect, false);
                }
            }
        } catch (ConcurrentModificationException ignored) {
        }

        int i;

        if (this.potionsNeedUpdate) {
            if (!this.worldObj.isRemote) {
                if (this.activePotionsMap.isEmpty()) {
                    this.dataWatcher.updateObject(8, Byte.valueOf((byte) 0));
                    this.dataWatcher.updateObject(7, Integer.valueOf(0));
                    this.setInvisible(false);
                } else {
                    i = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
                    this.dataWatcher.updateObject(8, Byte.valueOf((byte)
                            (PotionHelper.func_82817_b(this.activePotionsMap.values()) ? 1 : 0)));
                    this.dataWatcher.updateObject(7, Integer.valueOf(i));
                    this.setInvisible(this.isPotionActive(Potion.invisibility.id));
                }
            }

            this.potionsNeedUpdate = false;
        }

        i = this.dataWatcher.getWatchableObjectInt(7);
        boolean flag1 = this.dataWatcher.getWatchableObjectByte(8) > 0;

        if (i > 0) {
            boolean flag = false;

            if (!this.isInvisible()) {
                flag = this.rand.nextBoolean();
            } else {
                flag = this.rand.nextInt(15) == 0;
            }

            if (flag1) {
                flag &= this.rand.nextInt(5) == 0;
            }

            if (flag && i > 0) {
                double d0 = (double) (i >> 16 & 255) / 255.0D;
                double d1 = (double) (i >> 8 & 255) / 255.0D;
                double d2 = (double) (i >> 0 & 255) / 255.0D;
                this.worldObj.spawnParticle(
                        flag1 ? "mobSpellAmbient" : "mobSpell",
                        this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        this.posY + this.rand.nextDouble() * (double) this.height - (double) this.yOffset,
                        this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        d0,
                        d1,
                        d2);
            }
        }
    }

    @Shadow
    private void onFinishedPotionEffect(PotionEffect p_70688_1_) {}

    @Shadow
    private void onChangedPotionEffect(PotionEffect p_70695_1_, boolean p_70695_2_) {}

    @Shadow
    public abstract boolean isPotionActive(int p_82165_1_);
}
