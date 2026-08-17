package com.mitchej123.hodgepodge.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;

import org.junit.jupiter.api.Test;

public class PotionMetadataCacheTest {

    @Test
    void findsVanillaRelevantBits() {
        assertEquals(
                0x406F,
                PotionMetadataCache.findRelevantBits(
                        PotionHelper.potionRequirements.values(),
                        PotionHelper.potionAmplifiers.values()));
    }

    @Test
    void includesModdedRequirementAndAmplifierBits() {
        assertEquals(
                (1 << 14) | (1 << 12) | (1 << 8) | 1,
                PotionMetadataCache.findRelevantBits(Arrays.asList("0 & !8"), Arrays.asList("12")));
    }

    @Test
    void ignoresMultiplierValues() {
        assertEquals(
                (1 << 14) | 1,
                PotionMetadataCache.findRelevantBits(Arrays.asList("0*9"), Collections.emptyList()));
    }

    @Test
    void fallsBackForGlobalCountsAndUnknownSyntax() {
        assertEquals(
                PotionMetadataCache.ALL_METADATA_BITS,
                PotionMetadataCache.findRelevantBits(Arrays.asList("=3"), Collections.emptyList()));
        assertEquals(
                PotionMetadataCache.ALL_METADATA_BITS,
                PotionMetadataCache.findRelevantBits(Arrays.asList("0 / 1"), Collections.emptyList()));
        assertEquals(
                PotionMetadataCache.ALL_METADATA_BITS,
                PotionMetadataCache.findRelevantBits(Arrays.asList("\u06610"), Collections.emptyList()));
        assertEquals(
                PotionMetadataCache.ALL_METADATA_BITS,
                PotionMetadataCache.findRelevantBits(Arrays.<Object>asList(1), Collections.emptyList()));
    }

    @Test
    void cachesNullValues() {
        PotionMetadataCache<Object> cache = new PotionMetadataCache<>(0);
        AtomicInteger calls = new AtomicInteger();

        assertNull(cache.get(1, ignored -> {
            calls.incrementAndGet();
            return null;
        }));
        assertNull(cache.get(2, ignored -> {
            calls.incrementAndGet();
            return null;
        }));
        assertEquals(1, calls.get());
    }

    @Test
    void preservesVanillaPotionRegistrationOrderAndMetadata() {
        int metadataMask = PotionMetadataCache
                .findRelevantBits(PotionHelper.potionRequirements.values(), PotionHelper.potionAmplifiers.values());
        PotionMetadataCache<List<PotionEffect>> cache = new PotionMetadataCache<>(metadataMask);
        AtomicInteger calls = new AtomicInteger();
        Map<List<PotionEffect>, Integer> expected = new LinkedHashMap<>();
        Map<List<PotionEffect>, Integer> actual = new LinkedHashMap<>();

        for (int metadata = 1; metadata <= PotionMetadataCache.ALL_METADATA_BITS; metadata++) {
            List<PotionEffect> expectedEffects = PotionHelper.getPotionEffects(metadata, false);
            if (expectedEffects != null && !expectedEffects.isEmpty()) expected.put(expectedEffects, metadata);

            List<PotionEffect> actualEffects = cache.get(metadata, value -> {
                calls.incrementAndGet();
                return PotionHelper.getPotionEffects(value, false);
            });
            if (actualEffects != null && !actualEffects.isEmpty()) actual.put(actualEffects, metadata);
        }

        assertEquals(new ArrayList<>(expected.entrySet()), new ArrayList<>(actual.entrySet()));
        assertEquals(1 << Integer.bitCount(metadataMask), calls.get());
    }
}
