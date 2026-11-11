package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import biomesoplenty.api.biome.BiomeFeatures;

@Mixin(value = BiomeFeatures.class, remap = false)
public class MixinBiomeFeatures_CacheFields {

    @Unique
    private static final Map<Class<?>, Map<String, Field>> hodgepodge$fieldCache = new IdentityHashMap<>();

    @Redirect(
            method = "getFeature",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Class;getField(Ljava/lang/String;)Ljava/lang/reflect/Field;"))
    private Field hodgepodge$cachedGetField(Class<?> clazz, String name) throws NoSuchFieldException {
        Map<String, Field> classCache = hodgepodge$fieldCache.get(clazz);
        if (classCache == null) {
            classCache = hodgepodge$buildFieldCache(clazz);
            hodgepodge$fieldCache.put(clazz, classCache);
        }
        Field field = classCache.get(name);
        if (field == null) throw new NoSuchFieldException(name);
        return field;
    }

    @Unique
    private static Map<String, Field> hodgepodge$buildFieldCache(Class<?> clazz) {
        var cache = new HashMap<String, Field>();
        for (Field field : clazz.getFields()) {
            field.setAccessible(true);
            cache.put(field.getName(), field);
        }
        return cache;
    }
}
