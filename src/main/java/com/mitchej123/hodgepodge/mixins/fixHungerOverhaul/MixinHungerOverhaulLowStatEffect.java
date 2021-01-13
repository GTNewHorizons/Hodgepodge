package com.mitchej123.hodgepodge.mixins.fixHungerOverhaul;

import com.mitchej123.hodgepodge.Hodgepodge;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import iguanaman.hungeroverhaul.config.IguanaConfig;
import iguanaman.hungeroverhaul.util.IguanaEventHook;
import iguanaman.hungeroverhaul.util.RandomHelper;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = IguanaEventHook.class, remap = false)
public class MixinHungerOverhaulLowStatEffect {

    /**
     * @author mitchej123
     * @reason Fix bad interaction with SoL Carrot Eddition
     */
    @Overwrite()
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        // Slow growth and egg rates
        if (event.entityLiving instanceof EntityAnimal) {
            float rndBreed = RandomHelper.nextFloat(Hodgepodge.RNG, IguanaConfig.breedingTimeoutMultiplier);
            float rndChild = RandomHelper.nextFloat(Hodgepodge.RNG, IguanaConfig.childDurationMultiplier);
            EntityAgeable ageable = (EntityAgeable) event.entityLiving;
            int growingAge = ageable.getGrowingAge();

            if (growingAge > 0 && rndBreed >= 1)      ageable.setGrowingAge(++growingAge);
            else if (growingAge < 0 && rndChild >= 1) ageable.setGrowingAge(--growingAge);

            if (IguanaConfig.eggTimeoutMultiplier > 1 && event.entityLiving instanceof EntityChicken) {
                float rnd = RandomHelper.nextFloat(Hodgepodge.RNG, IguanaConfig.eggTimeoutMultiplier);
                EntityChicken chicken = (EntityChicken) event.entityLiving;
                if (chicken.timeUntilNextEgg > 0 && rnd >= 1) chicken.timeUntilNextEgg += 1;
            }

            // Reduced milked value every second
            if (IguanaConfig.milkedTimeout > 0 && event.entityLiving instanceof EntityCow && event.entityLiving.worldObj.getTotalWorldTime() % 20 == 0) {
                NBTTagCompound tags = event.entityLiving.getEntityData();
                if (tags.hasKey("Milked")) {
                    int milked = tags.getInteger("Milked");
                    if (--milked <= 0) tags.removeTag("Milked");
                    else               tags.setInteger("Milked", milked);
                }
            }
        }

        if (!event.entityLiving.worldObj.isRemote && event.entityLiving instanceof EntityPlayer) {
            NBTTagCompound tags = event.entityLiving.getEntityData();

            // low stat effects
            if (tags.hasKey("HungerOverhaulCheck")) {
                int lastCheck = tags.getInteger("HungerOverhaulCheck");
                if (--lastCheck <= 0) tags.removeTag("HungerOverhaulCheck");
                else                  tags.setInteger("HungerOverhaulCheck", lastCheck);
            } else {
                EntityPlayer player = (EntityPlayer) event.entityLiving;
                // Fix for SoL Carrot & Tinkers hearts, cap both hearts at 20 for calculation
                final float healthPercent = Math.min(player.getHealth(), 20) / Math.min(player.getMaxHealth(), 20);
                final int foodLevel = Math.max(player.getFoodStats().getFoodLevel(), 1);
                final boolean creative = player.capabilities.isCreativeMode;

                if (IguanaConfig.constantHungerLoss && !creative && !player.isDead)
                    player.addExhaustion(0.01F);

                if (IguanaConfig.addLowStatEffects) {
                    int difficultyModifierEffects = 2;
                    if (IguanaConfig.difficultyScalingEffects && event.entityLiving.worldObj.difficultySetting != null) {
                        difficultyModifierEffects = event.entityLiving.worldObj.difficultySetting.getDifficultyId();
                    }

                    // low stat effects
                    if (!creative && !event.entityLiving.isDead && healthPercent > 0f) {
                        // Higher is worse
                        int moveSlowDown = 0, digSlowDown = 0, weakness = 0;
                        
                        if (foodLevel <= 5) {
                            if (IguanaConfig.addLowHungerSlowness) moveSlowDown = 6 - foodLevel;
                            if (IguanaConfig.addLowHungerMiningSlowdown) digSlowDown = 6 - foodLevel;
                            if (IguanaConfig.addLowHungerWeakness) weakness = Math.max(0, 4 - foodLevel);
                            
                        }
                        
                        if (healthPercent > 0.25F) {
                            // Do nothing
                        } else if (healthPercent <= 0.05F) {
                            if (IguanaConfig.addLowHealthSlowness) moveSlowDown = 5;
                            if (IguanaConfig.addLowHealthMiningSlowdown) digSlowDown  = 5;
                            if (IguanaConfig.addLowHealthWeakness) weakness = 3;
                        } else if (healthPercent <= 0.10F) {
                            if (IguanaConfig.addLowHealthSlowness) moveSlowDown = Math.max(moveSlowDown, 4);
                            if (IguanaConfig.addLowHealthMiningSlowdown) digSlowDown = Math.max(digSlowDown, 4);
                            if (IguanaConfig.addLowHealthWeakness) weakness = Math.max(weakness, 2);
                        } else if (healthPercent <= 0.15F) {
                            if (IguanaConfig.addLowHealthSlowness) moveSlowDown = Math.max(moveSlowDown, 3);
                            if (IguanaConfig.addLowHealthMiningSlowdown) digSlowDown = Math.max(digSlowDown, 3);
                            if (IguanaConfig.addLowHealthWeakness) weakness = Math.max(weakness, 1);
                        } else if (healthPercent <= 0.20F) {
                            if (IguanaConfig.addLowHealthSlowness) moveSlowDown = Math.max(moveSlowDown, 2);
                            if (IguanaConfig.addLowHealthMiningSlowdown) digSlowDown = Math.max(digSlowDown, 2);
                        } else if (healthPercent <= 0.25F) {
                            if (IguanaConfig.addLowHealthSlowness) moveSlowDown = Math.max(moveSlowDown, 1);
                            if (IguanaConfig.addLowHealthMiningSlowdown) digSlowDown = Math.max(digSlowDown, 1);
                            
                        }
                        
                        if (moveSlowDown != 0 && difficultyModifierEffects >= (-moveSlowDown + 4)) {
                            event.entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 19,  -4 + moveSlowDown + difficultyModifierEffects, true));
                        }
                        if (digSlowDown != 0 && difficultyModifierEffects >= (-digSlowDown + 4)) {
                            event.entityLiving.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 19, -4 + digSlowDown + difficultyModifierEffects, true));
                        }
                        if (weakness != 0 && difficultyModifierEffects >= (-weakness + 4)) {
                            event.entityLiving.addPotionEffect(new PotionEffect(Potion.weakness.id, 19, -4 + weakness + difficultyModifierEffects, true));
                        }
                        
                        if ((IguanaConfig.addLowHungerNausea && foodLevel <= 1) || (IguanaConfig.addLowHealthNausea && healthPercent <= 0.05F))
                            event.entityLiving.addPotionEffect(new PotionEffect(Potion.confusion.id, 19, 0, true));
                    } // End !isCreative && not dead
                } // End addLowStatEffect

                tags.setInteger("HungerOverhaulCheck", 9);
            } // End HungerOverhaulCheck
        } // End isRemote && isPlayer 
                
    }
}

