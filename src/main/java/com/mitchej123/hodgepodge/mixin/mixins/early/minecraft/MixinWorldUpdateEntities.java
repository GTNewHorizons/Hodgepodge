package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public class MixinWorldUpdateEntities {

    @Redirect(
            method = "updateEntities",
            at = @At(value = "INVOKE", target = "Ljava/util/List;removeAll(Ljava/util/Collection;)Z"))
    public boolean fasterRemoveAll(List<TileEntity> targetList, Collection<TileEntity> collectionToRemove) {
        // Borrowed from Forge for 1.12.2 -- forge: faster "contains" makes this removal much more efficient
        final Set<TileEntity> setToRemove = Collections.newSetFromMap(new IdentityHashMap<>());
        setToRemove.addAll(collectionToRemove);
        return targetList.removeAll(setToRemove);
    }
}
