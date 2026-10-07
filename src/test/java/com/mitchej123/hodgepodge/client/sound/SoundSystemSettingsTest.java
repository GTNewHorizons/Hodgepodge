package com.mitchej123.hodgepodge.client.sound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.mitchej123.hodgepodge.config.SoundConfig;

import paulscode.sound.SoundSystemConfig;

class SoundSystemSettingsTest {

    private static final int ROWS = 13;

    @Test
    void everyRowReadsBackWhatItWrites() throws Exception {
        final List<Field> fields = new ArrayList<>();
        for (Field f : SoundConfig.class.getFields()) {
            final Class<?> t = f.getType();
            final boolean settable = Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers());
            if (settable
                    && (t == int.class || t == float.class || t == boolean.class || t == String.class || t.isEnum()))
                fields.add(f);
        }
        final List<Object> savedFields = new ArrayList<>();
        for (Field f : fields) savedFields.add(f.get(null));
        final Map<Method, Object> savedConfig = saveConfig();
        try {
            final List<Object> a = applied(fields, 0);
            final List<Object> b = applied(fields, 1);
            assertEquals(ROWS, a.size());
            for (int i = 0; i < ROWS; i++) assertNotEquals(a.get(i), b.get(i), "row " + i + " did not read its write");
            assertEquals(ROWS, new HashSet<>(a).size(), "two rows read the same setting: " + a);
        } finally {
            for (Map.Entry<Method, Object> e : savedConfig.entrySet()) e.getKey().invoke(null, e.getValue());
            for (int i = 0; i < fields.size(); i++) fields.get(i).set(null, savedFields.get(i));
        }
    }

    // Distinct value per field so a row reading another row's setting shows up.
    private static List<Object> applied(List<Field> fields, int k) throws Exception {
        for (int i = 0; i < fields.size(); i++) {
            final Field f = fields.get(i);
            final Class<?> t = f.getType();
            if (t == int.class) f.setInt(null, 100 + 10 * i + k);
            else if (t == float.class) f.setFloat(null, 0.5f + i + 0.25f * k);
            else if (t == boolean.class) f.setBoolean(null, k == 1);
            else if (t == String.class) f.set(null, "probe" + i + "_" + k);
            else f.set(null, t.getEnumConstants()[k]);
        }
        SoundSystemSettings.apply();
        final List<Object> out = new ArrayList<>();
        SoundSystemSettings.snapshot(out);
        return out;
    }

    // setter -> current value, for every SoundSystemConfig getter that has one
    private static Map<Method, Object> saveConfig() throws Exception {
        final Map<Method, Object> out = new LinkedHashMap<>();
        for (Method get : SoundSystemConfig.class.getMethods()) {
            if (!get.getName().startsWith("get") || get.getParameterCount() != 0) continue;
            try {
                final String setter = "set" + get.getName().substring(3);
                out.put(SoundSystemConfig.class.getMethod(setter, get.getReturnType()), get.invoke(null));
            } catch (NoSuchMethodException ignored) {}
        }
        return out;
    }
}
