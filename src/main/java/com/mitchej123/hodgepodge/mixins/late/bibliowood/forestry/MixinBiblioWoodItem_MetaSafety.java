package com.mitchej123.hodgepodge.mixins.late.bibliowood.forestry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import jds.bibliowood.forestrywood.blocks.ItemBookcase;
import jds.bibliowood.forestrywood.blocks.ItemBookcase2;
import jds.bibliowood.forestrywood.blocks.ItemCase0;
import jds.bibliowood.forestrywood.blocks.ItemCase1;
import jds.bibliowood.forestrywood.blocks.ItemClock;
import jds.bibliowood.forestrywood.blocks.ItemClock2;
import jds.bibliowood.forestrywood.blocks.ItemDesk;
import jds.bibliowood.forestrywood.blocks.ItemDesk2;
import jds.bibliowood.forestrywood.blocks.ItemFancySign;
import jds.bibliowood.forestrywood.blocks.ItemFancySign2;
import jds.bibliowood.forestrywood.blocks.ItemFancyWorkbench;
import jds.bibliowood.forestrywood.blocks.ItemFancyWorkbench2;
import jds.bibliowood.forestrywood.blocks.ItemLabel;
import jds.bibliowood.forestrywood.blocks.ItemLabel2;
import jds.bibliowood.forestrywood.blocks.ItemMapFrame;
import jds.bibliowood.forestrywood.blocks.ItemMapFrame2;
import jds.bibliowood.forestrywood.blocks.ItemPaintingFrameT0;
import jds.bibliowood.forestrywood.blocks.ItemPaintingFrameT02;
import jds.bibliowood.forestrywood.blocks.ItemPaintingFrameT1;
import jds.bibliowood.forestrywood.blocks.ItemPaintingFrameT12;
import jds.bibliowood.forestrywood.blocks.ItemPaintingFrameT2;
import jds.bibliowood.forestrywood.blocks.ItemPaintingFrameT22;
import jds.bibliowood.forestrywood.blocks.ItemPaintingFrameT3;
import jds.bibliowood.forestrywood.blocks.ItemPaintingFrameT32;
import jds.bibliowood.forestrywood.blocks.ItemPaintingFrameT4;
import jds.bibliowood.forestrywood.blocks.ItemPaintingFrameT42;
import jds.bibliowood.forestrywood.blocks.ItemPotionShelf;
import jds.bibliowood.forestrywood.blocks.ItemPotionShelf2;
import jds.bibliowood.forestrywood.blocks.ItemSeat;
import jds.bibliowood.forestrywood.blocks.ItemSeat2;
import jds.bibliowood.forestrywood.blocks.ItemShelf;
import jds.bibliowood.forestrywood.blocks.ItemShelf2;
import jds.bibliowood.forestrywood.blocks.ItemTable;
import jds.bibliowood.forestrywood.blocks.ItemTable2;
import jds.bibliowood.forestrywood.blocks.ItemToolrack;
import jds.bibliowood.forestrywood.blocks.ItemToolrack2;
import jds.bibliowood.forestrywood.items.ItemSeatBackF1;
import jds.bibliowood.forestrywood.items.ItemSeatBackF2;
import jds.bibliowood.forestrywood.items.ItemSeatBackF3;
import jds.bibliowood.forestrywood.items.ItemSeatBackF4;
import jds.bibliowood.forestrywood.items.ItemSeatBackF5;

@Mixin({ ItemBookcase.class, ItemBookcase2.class, ItemCase0.class, ItemCase1.class, ItemClock.class, ItemClock2.class,
        ItemDesk.class, ItemDesk2.class, ItemFancySign.class, ItemFancySign2.class, ItemFancyWorkbench.class,
        ItemFancyWorkbench2.class, ItemLabel.class, ItemLabel2.class, ItemMapFrame.class, ItemMapFrame2.class,
        ItemPaintingFrameT0.class, ItemPaintingFrameT1.class, ItemPaintingFrameT2.class, ItemPaintingFrameT02.class,
        ItemPaintingFrameT3.class, ItemPaintingFrameT4.class, ItemPaintingFrameT12.class, ItemPaintingFrameT22.class,
        ItemPaintingFrameT32.class, ItemPaintingFrameT42.class, ItemPotionShelf.class, ItemPotionShelf2.class,
        ItemSeat.class, ItemSeat2.class, ItemShelf.class, ItemShelf2.class, ItemTable.class, ItemTable2.class,
        ItemToolrack.class, ItemToolrack2.class, ItemSeatBackF1.class, ItemSeatBackF2.class, ItemSeatBackF3.class,
        ItemSeatBackF4.class, ItemSeatBackF5.class })
public class MixinBiblioWoodItem_MetaSafety {

    @Redirect(
            method = "getUnlocalizedName(Lnet/minecraft/item/ItemStack;)Ljava/lang/String;",
            at = @At(value = "FIELD", target = "desc=/^\\[Ljava/lang/String;$/", args = "array=get"))
    private String safeIndexArray(String[] array, int index) {
        // This will result in unlocalized text, but that's arguably better than displaying one of the existing names or
        // crashing.
        return index < array.length ? array[index] : "invalid";
    }
}
