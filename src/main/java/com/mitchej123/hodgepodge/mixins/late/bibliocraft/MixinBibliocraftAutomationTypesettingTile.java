package com.mitchej123.hodgepodge.mixins.late.bibliocraft;

import static com.mitchej123.hodgepodge.Compat.isAutomagyPresent;
import static com.mitchej123.hodgepodge.Compat.isEnderIoPresent;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.gtnewhorizon.gtnhlib.geometry.CubeIterator;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import crazypants.enderio.machine.obelisk.xp.TileExperienceObelisk;
import crazypants.enderio.xp.ExperienceContainer;
import jds.bibliocraft.items.ItemAtlas;
import jds.bibliocraft.items.ItemBigBook;
import jds.bibliocraft.items.ItemLoader;
import jds.bibliocraft.items.ItemPlate;
import jds.bibliocraft.items.ItemRecipeBook;
import jds.bibliocraft.items.ItemStockroomCatalog;
import jds.bibliocraft.tileentities.TileEntityTypeMachine;
import tuhljin.automagy.tiles.TileEntityJarXP;

@Mixin(TileEntityTypeMachine.class)
public class MixinBibliocraftAutomationTypesettingTile extends TileEntity {

    @WrapMethod(method = "isItemValidForSlot")
    private boolean hodgepodge$allowAllValidInputTypes(int slot, ItemStack itemstack, Operation<Boolean> original) {
        NBTTagCompound nbt;
        Item stackItem = itemstack.getItem();
        if (slot == 0) return stackItem instanceof ItemPlate || stackItem instanceof ItemEditableBook || stackItem instanceof ItemEnchantedBook || stackItem instanceof ItemStockroomCatalog || ((stackItem instanceof ItemBigBook || stackItem instanceof ItemRecipeBook) && (nbt = itemstack.getTagCompound()) != null && nbt.getBoolean("signed")) || stackItem instanceof ItemAtlas && itemstack.getTagCompound() != null;
        return original.call(slot, itemstack);
    }

    @Shadow(remap = false)
    private abstract boolean addBookOrPlate(ItemStack playerstack, World world);
    
    @WrapMethod(method = "setInventorySlotContents")
    private void hodgepodge$useAddBookOrPlate(int slot, ItemStack itemstack, Operation<Void> original) {
        if (slot == 0 && itemstack != null) addBookOrPlate(itemstack, this.worldObj);
        else original.call(slot, itemstack);
    }

    @Shadow(remap = false)
    private abstract boolean enchantPlate(EntityPlayer player);

    @WrapMethod(method = "setPlate")
    private void hodgepodge$enchIfCanEnch(Operation<Void> op) {
        if (enchantPlate(null)) return;
        op.call();
    }

    @WrapOperation(method = "enchantPlate", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/player/EntityPlayer;experienceLevel:I"))
    private int hdogepodge$cursedXpDrainImpl(EntityPlayer instance, Operation<Integer> original, @Local int levelcost) {
        if (instance != null) return instance.experienceLevel;
        int xpCost = levelcost < 16 ? 17 * levelcost : levelcost < 30 ? (((3 * levelcost - 59) * levelcost) >> 1) + 360 : (((7 * levelcost - 303) * levelcost) >> 1) + 2220;
        CubeIterator iter = new CubeIterator(8);
        ArrayList<ExperienceContainer> obeliskstodrain;
        int xp;
        if (isEnderIoPresent()) {
            if (!worldObj.isRemote) obeliskstodrain = new ArrayList<>();
            while (iter.hasNext()) {
                iter.next();
                if (this.worldObj.getTileEntity(
                    iter.n + this.xCoord,
                   iter.l + this.yCoord,
                    iter.m + this.zCoord) instanceof TileExperienceObelisk obelisk) {
                    ExperienceContainer cont = obelisk.getContainer();
                    xp = cont.getExperienceTotal();
                    int r11d = xpCost - xp;
                    if (r11d < 0) {
                        if (obeliskstodrain != null) {
                            obeliskstodrain.forEach(c -> c.drain(null, Integer.MAX_VALUE, true));
                            cont.drain(null, Integer.MAX_VALUE, true);
                            if (r11d != 0) cont.addExperience(-r11d);
                        }
                        return Integer.MAX_VALUE;
                    } if (obeliskstodrain != null) obeliskstodrain.add(cont);
                    xpCost = r11d;
                }
            }
        }
        if (isAutomagyPresent()) {
            ArrayList<TileEntityJarXP> jarstodrain = worldObj.isRemote ? null : new ArrayList<>();
            iter.n = 0;
            iter.l = 0;
            iter.m = 0;
            while (iter.hasNext()) {
                iter.next();
                if (this.worldObj.getTileEntity(
                    iter.n + this.xCoord,
                    iter.l + this.yCoord,
                    iter.m + this.zCoord) instanceof TileEntityJarXP jar) {
                    xp = jar.getXP();
                    xpCost -= xp;
                    if (xpCost < 0) {
                        if (jarstodrain != null) {
                            if (obeliskstodrain != null) obeliskstodrain.forEach(c -> c.drain(null, Integer.MAX_VALUE, true));
                            jarstodrain.forEach(j -> j.setXP(0));
                            jar.setXP(-xpCost);
                        }
                        return Integer.MAX_VALUE;
                    }
                }
            }
        }

        return Integer.MIN_VALUE;
    
    }

    @WrapOperation(method = "enchantPlate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;addExperienceLevel(Lnet/minecraft/entity/player/EntityPlayer;I)V"))
    private void hodgepodge$avoidDrainIfNull(EntityPlayer instance, int lvl, Operation<Void> op) {
        if(instance == null) return;
        op.call(lvl);
    }

}
