package com.mitchej123.hodgepodge.mixins.early.minecraft;

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

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_FixPotionException extends Entity {

    @Shadow
    @Final
    private HashMap<Integer, PotionEffect> activePotionsMap;

    @Shadow
    private boolean potionsNeedUpdate;

    private MixinEntityLivingBase_FixPotionException(World p_i1594_1_) {
        super(p_i1594_1_);
    }

    /**
     * @author laetansky
     * @reason Fix {@link ConcurrentModificationException} being thrown when modifying active potions inside those forge
     *         event handlers which could be fired while iterating over active potion effect. Fix is back ported from
     *         newer versions.
     */
    @Overwrite
    protected void updatePotionEffects() {
        Iterator<Integer> iterator = this.activePotionsMap.keySet().iterator();

        try {
            while (iterator.hasNext()) {
                PotionEffect potioneffect = this.activePotionsMap.get(iterator.next());

                if (!potioneffect.onUpdate(((EntityLivingBase) (Object) this))) {
                    if (!this.worldObj.isRemote) {
                        iterator.remove();
                        this.onFinishedPotionEffect(potioneffect);
                    }
                } else if (potioneffect.getDuration() % 600 == 0) {
                    this.onChangedPotionEffect(potioneffect, false);
                }
            }
        } catch (ConcurrentModificationException ignored) {}

        int i;

        if (this.potionsNeedUpdate) {
            if (!this.worldObj.isRemote) {
                if (this.activePotionsMap.isEmpty()) {
                    this.dataWatcher.updateObject(8, (byte) 0);
                    this.dataWatcher.updateObject(7, 0);
                    this.setInvisible(false);
                } else {
                    i = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
                    this.dataWatcher.updateObject(
                            8,
                            (byte) (PotionHelper.func_82817_b(this.activePotionsMap.values()) ? 1 : 0));
                    this.dataWatcher.updateObject(7, i);
                    this.setInvisible(this.isPotionActive(Potion.invisibility.id));
                }
            }

            this.potionsNeedUpdate = false;
        }

        i = this.dataWatcher.getWatchableObjectInt(7);

        if (i > 0) {
            boolean ambient = this.dataWatcher.getWatchableObjectByte(8) > 0;
            boolean spawnParticle;

            if (this.isInvisible()) {
                spawnParticle = this.rand.nextInt(15) == 0;
            } else {
                spawnParticle = this.rand.nextBoolean();
            }

            if (ambient) {
                spawnParticle &= this.rand.nextInt(5) == 0;
            }

            if (spawnParticle) {
                double r = (double) (i >> 16 & 255) / 255.0D;
                double g = (double) (i >> 8 & 255) / 255.0D;
                double b = (double) (i & 255) / 255.0D;
                this.worldObj.spawnParticle(
                        ambient ? "mobSpellAmbient" : "mobSpell",
                        this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        this.posY + this.rand.nextDouble() * (double) this.height - (double) this.yOffset,
                        this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        r,
                        g,
                        b);
            }
        }
    }

    @Shadow
    protected abstract void onFinishedPotionEffect(PotionEffect p_70688_1_);

    @Shadow
    protected abstract void onChangedPotionEffect(PotionEffect p_70695_1_, boolean p_70695_2_);

    @Shadow
    public abstract boolean isPotionActive(int p_82165_1_);
}
