package com.mitchej123.hodgepodge.mixins.late.bibliocraft;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import jds.bibliocraft.tileentities.TileEntityBookcase;
import jds.bibliocraft.tileentities.TileEntityClipboard;
import jds.bibliocraft.tileentities.TileEntityCookieJar;
import jds.bibliocraft.tileentities.TileEntityDinnerPlate;
import jds.bibliocraft.tileentities.TileEntityDiscRack;
import jds.bibliocraft.tileentities.TileEntityFancyWorkbench;
import jds.bibliocraft.tileentities.TileEntityFramedChest;
import jds.bibliocraft.tileentities.TileEntityGenericShelf;
import jds.bibliocraft.tileentities.TileEntityPaintPress;
import jds.bibliocraft.tileentities.TileEntityPainting;
import jds.bibliocraft.tileentities.TileEntityPotionShelf;
import jds.bibliocraft.tileentities.TileEntitySeat;
import jds.bibliocraft.tileentities.TileEntitySwordPedestal;
import jds.bibliocraft.tileentities.TileEntityTable;
import jds.bibliocraft.tileentities.TileEntityTypeMachine;
import jds.bibliocraft.tileentities.TileEntityTypewriter;
import jds.bibliocraft.tileentities.TileEntityWeaponCase;
import jds.bibliocraft.tileentities.TileEntityWeaponRack;

/**
 * Bibliocraft never marks its inventories dirty. Changes made by right-clicking the block (e.g. putting a book on a
 * shelf or table) therefore don't mark the chunk as modified, and the item is lost if the chunk isn't saved for another
 * reason. The Armor Stand has its own mixin ({@link MixinTileEntityArmorStand_MarkDirty}).
 */
@Mixin({ TileEntityBookcase.class, TileEntityClipboard.class, TileEntityCookieJar.class, TileEntityDinnerPlate.class,
        TileEntityDiscRack.class, TileEntityFancyWorkbench.class, TileEntityFramedChest.class,
        TileEntityGenericShelf.class, TileEntityPaintPress.class, TileEntityPainting.class, TileEntityPotionShelf.class,
        TileEntitySeat.class, TileEntitySwordPedestal.class, TileEntityTable.class, TileEntityTypeMachine.class,
        TileEntityTypewriter.class, TileEntityWeaponCase.class, TileEntityWeaponRack.class })
public class MixinBibliocraftInventories_MarkDirty extends TileEntity {

    @Inject(method = "setInventorySlotContents", at = @At("TAIL"))
    private void hodgepodge$markDirtyOnSet(int slot, ItemStack stack, CallbackInfo ci) {
        this.markDirty();
    }

    @Inject(method = "decrStackSize", at = @At("TAIL"))
    private void hodgepodge$markDirtyOnDecr(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
        this.markDirty();
    }
}
