package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Collection;
import java.util.List;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

@Mixin(World.class)
public class MixinWorldUpdateEntities {

    @Redirect(
            method = "updateEntities",
            at = @At(value = "INVOKE", target = "Ljava/util/List;removeAll(Ljava/util/Collection;)Z"))
    public <E> boolean fasterRemoveAll(List<E> targetList, Collection<E> collectionToRemove) {
        // Borrowed from Forge for 1.12.2 -- forge: faster "contains" makes this removal much more efficient
        return targetList.removeAll(new ReferenceOpenHashSet<>(collectionToRemove));
    }
}
