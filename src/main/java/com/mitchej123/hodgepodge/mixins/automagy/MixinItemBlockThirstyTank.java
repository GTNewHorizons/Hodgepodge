package com.mitchej123.hodgepodge.mixins.automagy;

import java.util.Arrays;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import org.spongepowered.asm.mixin.Mixin;
import tuhljin.automagy.blocks.ItemBlockThirstyTank;
import tuhljin.automagy.tiles.ModTileEntity;
import tuhljin.automagy.tiles.TileEntityThirstyTank;

@Mixin(ItemBlockThirstyTank.class)
public class MixinItemBlockThirstyTank implements IFluidContainerItem {
    @Override
    public FluidStack getFluid(ItemStack container) {
        return container.hasTagCompound() ? FluidStack.loadFluidStackFromNBT(container.stackTagCompound) : null;
    }

    @Override
    public int getCapacity(ItemStack container) {
        int capacityInBuckets = TileEntityThirstyTank.CAPACITY_IN_BUCKETS_DEFAULT;
        if (container != null && container.hasTagCompound()) {
            final int[] glyphs = ModTileEntity.getIntArrayFromNbtOrDefault(container.stackTagCompound, "Glyphs", 0, 6);
            final int glyphOfTheReservoirId = 8;
            final int glyphOfTheReservoirCount = (int) Arrays.stream(glyphs)
                    .filter(glyph -> glyph == glyphOfTheReservoirId)
                    .count();
            capacityInBuckets += glyphOfTheReservoirCount * TileEntityThirstyTank.CAPACITY_IN_BUCKETS_PER_UPGRADE;
        }
        return capacityInBuckets * 1000;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if (container.stackSize != 1) return 0;
        if (resource == null || resource.amount <= 0) return 0;
        FluidStack fluidStack = this.getFluid(container);
        if (fluidStack == null) fluidStack = new FluidStack(resource, 0);
        if (!fluidStack.isFluidEqual(resource)) return 0;
        int amount = Math.min(this.getCapacity(container) - fluidStack.amount, resource.amount);
        if (doFill && amount > 0) {
            fluidStack.amount += amount;
            this.setFluid(container, fluidStack);
        }
        return amount;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        if (container.stackSize != 1) return null;
        FluidStack fluidStack = this.getFluid(container);
        if (fluidStack == null || fluidStack.amount <= 0) return null;
        int drain = Math.min(fluidStack.amount, maxDrain);
        if (drain <= 0) return null;
        if (doDrain) {
            fluidStack.amount -= drain;
            this.setFluid(container, fluidStack);
        }
        return new FluidStack(fluidStack, drain);
    }

    public void setFluid(ItemStack container, FluidStack resource) {
        if (container == null) return;
        if (resource != null && 0 < resource.amount) {
            NBTTagCompound nbt = container.getTagCompound() != null ? container.getTagCompound() : new NBTTagCompound();
            resource.writeToNBT(nbt);
            container.setTagCompound(nbt);
            return;
        }
        NBTTagCompound nbt = container.getTagCompound();
        if (nbt == null) return;
        if (nbt.hasKey("Glyphs")) {
            final int[] glyphs = nbt.getIntArray("Glyphs");
            nbt = new NBTTagCompound();
            nbt.setIntArray("Glyphs", glyphs);
        } else {
            nbt = null;
        }
        container.setTagCompound(nbt);
    }
}
