package com.mitchej123.hodgepodge.util;

import java.lang.reflect.Field;

import com.gtnewhorizon.gtnhlib.reflect.Fields;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class FinalValueHelper {

    /**
     * @author eigenraven
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
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
