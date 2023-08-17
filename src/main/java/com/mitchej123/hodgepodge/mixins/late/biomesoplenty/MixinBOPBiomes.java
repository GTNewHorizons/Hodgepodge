package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import java.lang.reflect.Field;

import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.gtnewhorizon.gtnhlib.reflect.Fields;

import biomesoplenty.api.biome.BOPOverriddenBiome;
import biomesoplenty.common.configuration.BOPConfigurationBiomeGen;
import biomesoplenty.common.core.BOPBiomes;
import biomesoplenty.common.world.BOPBiomeManager;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;

@Mixin(BOPBiomes.class)
public class MixinBOPBiomes {

    /**
     * @author eigenraven
     * @reason To make it compatible with Java 12+
     */
    @Overwrite(remap = false)
    private static void registerOverriddenBiome(Class<? extends BOPOverriddenBiome> biomeClass,
            String[]... overriddenBiomeNames) {
        final Fields.ClassFields<BiomeGenBase> biomeGenFields = Fields.ofClass(BiomeGenBase.class);
        for (String[] overriddenBiomeNameCandidates : overriddenBiomeNames) {
            final String[] overriddenBiomeNameRemappedCandidates = ObfuscationReflectionHelper
                    .remapFieldNames(biomeGenFields.klass.getName(), overriddenBiomeNameCandidates);
            final Field foundField = ReflectionHelper
                    .findField(biomeGenFields.klass, overriddenBiomeNameRemappedCandidates);

            final Fields.ClassFields<BiomeGenBase>.Field<BiomeGenBase> field = biomeGenFields
                    .getField(Fields.LookupType.PUBLIC, foundField.getName(), BiomeGenBase.class);

            try {
                BiomeGenBase biomeToOverride = field.getValue(null);
                if (biomeToOverride != null) {
                    BiomeGenBase newBiome = BOPBiomeManager
                            .createBiome(biomeClass, biomeToOverride.biomeName, biomeToOverride.biomeID);
                    if (BOPConfigurationBiomeGen.config
                            .get("Vanilla Biomes To Override", biomeToOverride.biomeName, true).getBoolean(false)) {
                        field.setValue(null, newBiome);
                        BiomeGenBase.getBiomeGenArray()[biomeToOverride.biomeID] = newBiome;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
