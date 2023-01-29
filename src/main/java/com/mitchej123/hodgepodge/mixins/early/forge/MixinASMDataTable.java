package com.mitchej123.hodgepodge.mixins.early.forge;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;

@Mixin(ASMDataTable.class)
public class MixinASMDataTable {

    @Shadow(remap = false)
    private SetMultimap<String, ASMData> globalAnnotationData;

    @Shadow(remap = false)
    private Map<ModContainer, SetMultimap<String, ASMData>> containerAnnotationData;

    @Shadow(remap = false)
    private List<ModContainer> containers;

    /**
     * We will forget the guava immutable collections now, since everyone thought they are immutable and won't attempt
     * to mutate it.
     * 
     * @author glee8e
     * @reason to optimize the embarrassingly inefficient containerAnnotationData build process
     */
    @Overwrite(remap = false)
    public SetMultimap<String, ASMData> getAnnotationsFor(ModContainer container) {
        if (containerAnnotationData == null) {
            Map<ModContainer, SetMultimap<String, ASMData>> mapBuilder = new HashMap<>();
            Multimap<File, ModContainer> containersMap = Multimaps.index(containers, ModContainer::getSource);
            for (Entry<String, ASMData> entry : globalAnnotationData.entries()) {
                for (ModContainer modContainer : containersMap.get(entry.getValue().getCandidate().getModContainer())) {
                    mapBuilder.computeIfAbsent(modContainer, map -> HashMultimap.create())
                            .put(entry.getKey(), entry.getValue());
                }
            }
            containerAnnotationData = mapBuilder;
        }
        return containerAnnotationData.get(container);
    }
}
