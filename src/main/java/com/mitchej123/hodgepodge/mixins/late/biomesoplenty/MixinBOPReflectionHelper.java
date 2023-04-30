package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.gtnewhorizon.gtnhlib.reflect.Fields;

import biomesoplenty.common.helpers.BOPReflectionHelper;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;

@Mixin(BOPReflectionHelper.class)
public class MixinBOPReflectionHelper {

    /**
     * @author eigenraven
     * @reason Make it compatible with Java 12+.
     */
    @Overwrite(remap = false)
    public static <T, E> void setPrivateFinalValue(Class<? super T> classToAccess, T instance, E value,
            String... fieldNames) {
        final Field field = ReflectionHelper.findField(
                classToAccess,
                ObfuscationReflectionHelper.remapFieldNames(classToAccess.getName(), fieldNames));

        try {
            final Fields.ClassFields.Field accessor = Fields.ofClass(classToAccess)
                    .getUntypedField(Fields.LookupType.DECLARED_IN_HIERARCHY, field.getName());
            accessor.setValue(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
