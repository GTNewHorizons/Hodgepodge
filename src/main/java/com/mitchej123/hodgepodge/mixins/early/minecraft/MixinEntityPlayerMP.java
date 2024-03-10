package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Collection;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Sets;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP extends EntityLivingBase {

    /*
     * Fix extra health modifiers disappearing on return from the end. Inspired from the comment on
     * https://github.com/MinecraftForge/MinecraftForge/pull/4830 Use clonePlayer on 1.7.10 instead of
     * PlayerList.recreatePlayerEntity on 1.12
     */
    @Inject(method = "clonePlayer(Lnet/minecraft/entity/player/EntityPlayer;Z)V", at = @At(value = "RETURN"))
    private void hodgepodge$injectClonePlayer(EntityPlayer oldPlayer, boolean copyEverything, CallbackInfo ci) {
        if (copyEverything) {
            // Grab the attribute map from the old player
            ServersideAttributeMap oldAttributeMap = (ServersideAttributeMap) oldPlayer.getAttributeMap();

            // Grab the watched attributes
            @SuppressWarnings("unchecked")
            Collection<IAttributeInstance> watchedAttribs = oldAttributeMap.getWatchedAttributes();

            if (!watchedAttribs.isEmpty()) {
                ServersideAttributeMap newAttributeMap = (ServersideAttributeMap) (this.getAttributeMap());

                for (IAttributeInstance oldAttr : watchedAttribs) {
                    if (!(oldAttr instanceof ModifiableAttributeInstance oldAttrModifiable)) continue;

                    // Get a new instance of a modifiable attribute based on the old one
                    ModifiableAttributeInstance newInst = newAttributeMap.getAttributeInstance(oldAttr.getAttribute());

                    // Get the modifiers for the old attribute
                    for (AttributeModifier modifier : getModifiers(oldAttrModifiable)) try {
                        // And apply them to the new attribute instance
                        newInst.applyModifier(modifier);
                    } catch (IllegalArgumentException ignored) {
                        // Be safe
                    }
                }
                // We've possibly changed the health, so set the health again, similar to what was already done earlier
                // in ClonePlayer
                this.setHealth(oldPlayer.getHealth());
            }
        }
    }

    // Helper method based on 1.12
    @SuppressWarnings("unchecked")
    @Unique
    private Collection<AttributeModifier> getModifiers(ModifiableAttributeInstance attr) {
        Set<AttributeModifier> toReturn = Sets.newHashSet();
        for (int i = 0; i < 3; ++i) {
            toReturn.addAll(attr.getModifiersByOperation(i));
        }
        return toReturn;
    }

    private MixinEntityPlayerMP(World p_i1594_1_) {
        // Needed because we're extending from EntityLivingBase
        super(p_i1594_1_);
    }
}
