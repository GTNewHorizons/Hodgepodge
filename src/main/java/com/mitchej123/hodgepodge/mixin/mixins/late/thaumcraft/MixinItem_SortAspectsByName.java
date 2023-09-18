package com.mitchej123.hodgepodge.mixin.mixins.late.thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.util.AspectNameSorter;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.blocks.ItemJarFilled;
import thaumcraft.common.blocks.ItemJarNode;
import thaumcraft.common.items.ItemCrystalEssence;
import thaumcraft.common.items.ItemEssence;
import thaumcraft.common.items.ItemManaBean;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.items.ItemWispEssence;
import thaumcraft.common.items.armor.ItemHoverHarness;

@Mixin({ ItemFocusBasic.class, ItemJarFilled.class, ItemJarNode.class, ItemCrystalEssence.class, ItemEssence.class,
        ItemManaBean.class, ItemResource.class, ItemWispEssence.class, ItemHoverHarness.class })
public class MixinItem_SortAspectsByName {

    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lthaumcraft/api/aspects/AspectList;getAspectsSorted()[Lthaumcraft/api/aspects/Aspect;",
                    value = "INVOKE"),
            method = "addInformation")
    private Aspect[] hodgepodge$getAspectsSortedName(AspectList instance) {
        return AspectNameSorter.sort(instance);
    }

}
