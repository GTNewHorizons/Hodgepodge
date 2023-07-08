package com.mitchej123.hodgepodge.mixins.late.lotr;

import static com.mitchej123.hodgepodge.util.FinalValueHelper.setPrivateFinalValue;

import java.lang.reflect.Field;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAvoidEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.entity.ai.LOTREntityAIAvoidHuorn;
import lotr.common.entity.npc.LOTREntityHuornBase;

@Mixin(LOTREntityAIAvoidHuorn.class)
public class MixinRedirectHuornAI extends EntityAIAvoidEntity {

    public MixinRedirectHuornAI(final EntityCreature entity, float range, double near, double far) {
        super(entity, LOTREntityHuornBase.class, range, near, far);
    }

    /**
     * Replace call to unlockFinalField with a call to set the value using Util.setPrivateFinalValue for java 12+
     * compatability
     */
    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Llotr/common/LOTRReflection;unlockFinalField(Ljava/lang/reflect/Field;)V",
                    remap = false),
            remap = false)
    private void WriteFinalValue(Field f) {
        try {
            Field entityField = ReflectionHelper.findField(EntityAIAvoidEntity.class, "field_75380_a", "theEntity");
            EntityCreature entity = (EntityCreature) entityField.get(this);

            IEntitySelector replaceSelect = target -> {
                if (target.isEntityAlive() && entity.getEntitySenses().canSee(target)) {
                    LOTREntityHuornBase huorn = (LOTREntityHuornBase) target;
                    return huorn.isHuornActive();
                } else {
                    return false;
                }
            };
            setPrivateFinalValue(EntityAIAvoidEntity.class, this, replaceSelect, "field_98218_a");
        } catch (Exception ignored) {

        }

    }

    /**
     * Replaces the call to {@link Field#set(Object, Object)} with a dud as the other redirect handles the actual
     * writing
     */
    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/reflect/Field;set(Ljava/lang/Object;Ljava/lang/Object;)V",
                    remap = false),
            remap = false)
    private void IgnoreCallToSet(Field instance, Object temp1, Object temp2) {

    }

}
