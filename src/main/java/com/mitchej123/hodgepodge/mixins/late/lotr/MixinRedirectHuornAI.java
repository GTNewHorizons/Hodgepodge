package com.mitchej123.hodgepodge.mixins.late.lotr;

import java.lang.reflect.Field;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAvoidEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.gtnewhorizon.gtnhlib.reflect.Fields;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.entity.ai.LOTREntityAIAvoidHuorn;
import lotr.common.entity.npc.LOTREntityHuornBase;

@Mixin(LOTREntityAIAvoidHuorn.class)
public class MixinRedirectHuornAI extends EntityAIAvoidEntity {

    private static final Field ENTITY_FIELD = ReflectionHelper.findField(
            EntityAIAvoidEntity.class,
            ObfuscationReflectionHelper
                    .remapFieldNames(EntityAIAvoidEntity.class.getName(), "field_75380_a", "theEntity"));

    @SuppressWarnings("rawtypes")
    private static final Fields.ClassFields.Field ACCESSOR = Fields.ofClass(EntityAIAvoidEntity.class)
            .getUntypedField(Fields.LookupType.DECLARED_IN_HIERARCHY, "field_98218_a");

    private MixinRedirectHuornAI(final EntityCreature entity, float range, double near, double far) {
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
    @SuppressWarnings("unchecked")
    private void hodgepodge$writeFinalValue(Field f) {
        try {
            EntityCreature entity = (EntityCreature) ENTITY_FIELD.get(this);

            IEntitySelector replaceSelect = target -> {
                if (target.isEntityAlive() && entity.getEntitySenses().canSee(target)) {
                    LOTREntityHuornBase huorn = (LOTREntityHuornBase) target;
                    return huorn.isHuornActive();
                } else {
                    return false;
                }
            };
            ACCESSOR.setValue(this, replaceSelect);
        } catch (Exception ignored) {
            FMLLog.warning("LOTR: Error adding Avoid Huorn AI");
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
    private void hodgepodge$ignoreCallToSet(Field instance, Object temp1, Object temp2) {}
}
