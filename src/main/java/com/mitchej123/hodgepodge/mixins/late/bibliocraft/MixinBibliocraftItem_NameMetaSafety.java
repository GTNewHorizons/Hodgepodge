package com.mitchej123.hodgepodge.mixins.late.bibliocraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import jds.bibliocraft.blocks.BlockItemBookcaseCreative;
import jds.bibliocraft.blocks.BlockItemBookcaseMaster;
import jds.bibliocraft.blocks.BlockItemClock;
import jds.bibliocraft.blocks.BlockItemFancySign;
import jds.bibliocraft.blocks.BlockItemFancyWorkbench;
import jds.bibliocraft.blocks.BlockItemFurniturePaneler;
import jds.bibliocraft.blocks.BlockItemGenericShelfMaster;
import jds.bibliocraft.blocks.BlockItemLabelMaster;
import jds.bibliocraft.blocks.BlockItemLamp;
import jds.bibliocraft.blocks.BlockItemLantern;
import jds.bibliocraft.blocks.BlockItemMapFrame;
import jds.bibliocraft.blocks.BlockItemPainting;
import jds.bibliocraft.blocks.BlockItemPaintingFrameBorderless;
import jds.bibliocraft.blocks.BlockItemPaintingFrameFancy;
import jds.bibliocraft.blocks.BlockItemPaintingFrameFlat;
import jds.bibliocraft.blocks.BlockItemPaintingFrameMiddle;
import jds.bibliocraft.blocks.BlockItemPaintingFrameSimple;
import jds.bibliocraft.blocks.BlockItemPotionShelfMaster;
import jds.bibliocraft.blocks.BlockItemSeat;
import jds.bibliocraft.blocks.BlockItemStuffsMaster;
import jds.bibliocraft.blocks.BlockItemTable;
import jds.bibliocraft.blocks.BlockItemTypewriter;
import jds.bibliocraft.blocks.BlockItemWeaponCaseMaster;
import jds.bibliocraft.blocks.BlockItemWeaponRackMaster;
import jds.bibliocraft.blocks.BlockItemWritingDesk;
import jds.bibliocraft.items.ItemReadingGlasses;
import jds.bibliocraft.items.ItemSeatBack;
import jds.bibliocraft.items.ItemSeatBack2;
import jds.bibliocraft.items.ItemSeatBack3;
import jds.bibliocraft.items.ItemSeatBack4;
import jds.bibliocraft.items.ItemSeatBack5;

@Mixin({ ItemSeatBack.class, ItemSeatBack2.class, ItemSeatBack3.class, ItemSeatBack4.class, ItemSeatBack5.class,
        ItemReadingGlasses.class, BlockItemBookcaseCreative.class, BlockItemBookcaseMaster.class, BlockItemClock.class,
        BlockItemFancySign.class, BlockItemFancyWorkbench.class, BlockItemFurniturePaneler.class,
        BlockItemGenericShelfMaster.class, BlockItemLabelMaster.class, BlockItemLamp.class, BlockItemLantern.class,
        BlockItemMapFrame.class, BlockItemPainting.class, BlockItemPaintingFrameBorderless.class,
        BlockItemPaintingFrameFancy.class, BlockItemPaintingFrameFlat.class, BlockItemPaintingFrameMiddle.class,
        BlockItemPaintingFrameSimple.class, BlockItemPotionShelfMaster.class, BlockItemSeat.class,
        BlockItemStuffsMaster.class, BlockItemTable.class, BlockItemTypewriter.class, BlockItemWeaponCaseMaster.class,
        BlockItemWeaponRackMaster.class, BlockItemWritingDesk.class })
public class MixinBibliocraftItem_NameMetaSafety {

    @Redirect(
            method = "getUnlocalizedName",
            at = @At(value = "FIELD", target = "desc=/^\\[Ljava/lang/String;$/", args = "array=get"))
    private String safeIndexArray(String[] array, int index) {
        // This will result in unlocalized text, but that's arguably better than displaying one of the existing names or
        // crashing.
        return index < array.length ? array[index] : "invalid";
    }
}
