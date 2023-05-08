package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.InventoryMob;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EnumGolemType;
import thaumcraft.common.entities.golems.Marker;

@Mixin(EntityGolemBase.class)
public class MixinEntityGolemBase extends EntityGolem {

    @Shadow
    protected ArrayList<Marker> markers;

    @Shadow
    public int homeFacing, essentiaAmount;

    @Shadow
    public boolean advanced;

    @Shadow
    public EnumGolemType golemType;

    @Shadow
    public FluidStack fluidCarried;

    @Shadow
    public Aspect essentia;

    @Shadow
    public ItemStack itemCarried;

    @Shadow
    public String decoration;

    @Shadow
    public byte[] upgrades, colors;

    @Shadow
    public InventoryMob inventory;

    public MixinEntityGolemBase(World p_i1686_1_) {
        super(p_i1686_1_);
    }

    @Shadow
    public void setCore(byte core) {}

    @Shadow
    public byte getCore() {
        return 0;
    }

    @Shadow
    public void setTogglesValue(byte tog) {}

    @Shadow
    public void updateCarried() {}

    @Shadow
    public void setGolemDecoration(String string) {}

    @Shadow
    public void setOwner(String par1Str) {}

    @Shadow
    public boolean setupGolem() {
        return true;
    }

    @Shadow
    public boolean setupGolemInventory() {
        return true;
    }

    @Overwrite
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);

        String str;
        String s;

        int hx = nbt.getInteger("HomeX");
        int hy = nbt.getInteger("HomeY");
        int hz = nbt.getInteger("HomeZ");
        this.homeFacing = nbt.getByte("HomeFacing");
        setHomeArea(hx, hy, hz, 32);
        this.advanced = nbt.getBoolean("advanced");
        this.golemType = EnumGolemType.getType(nbt.getByte("GolemType"));
        setCore(nbt.getByte("Core"));
        if (getCore() == 5) {
            this.fluidCarried = FluidStack.loadFluidStackFromNBT(nbt);
        }
        if (getCore() == 6 && (s = nbt.getString("essentia")) != null) {
            this.essentia = Aspect.getAspect(s);
            if (this.essentia != null) {
                this.essentiaAmount = nbt.getByte("essentiaAmount");
            }
        }
        setTogglesValue(nbt.getByte("toggles"));
        NBTTagCompound var4 = nbt.getCompoundTag("ItemCarried");
        this.itemCarried = ItemStack.loadItemStackFromNBT(var4);
        updateCarried();
        this.decoration = nbt.getString("Decoration");
        setGolemDecoration(this.decoration);
        String var2 = nbt.getString("Owner");
        if (var2.length() > 0) {
            setOwner(var2);
        }
        this.dataWatcher.updateObject(30, (byte) getHealth());
        NBTTagList nbttaglist = nbt.getTagList("Markers", 10);
        for (int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int x = nbttagcompound1.getInteger("x");
            int y = nbttagcompound1.getInteger("y");
            int z = nbttagcompound1.getInteger("z");
            int dim = nbttagcompound1.getInteger("dim");
            byte s2 = nbttagcompound1.getByte("side");
            this.markers.add(new Marker(x, y, z, dim, s2, nbttagcompound1.getByte("color")));
        }
        this.upgrades = new byte[this.golemType.upgrades + (this.advanced ? 1 : 0)];
        int ul = this.upgrades.length;
        this.upgrades = nbt.getByteArray("upgrades");
        if (ul != this.upgrades.length) {
            byte[] tt = new byte[ul];
            Arrays.fill(tt, (byte) -1);
            for (int a2 = 0; a2 < this.upgrades.length; a2++) {
                if (a2 < ul) {
                    tt[a2] = this.upgrades[a2];
                }
            }
            this.upgrades = tt;
        }
        StringBuilder st = new StringBuilder();
        byte[] arr$ = this.upgrades;
        for (byte b : arr$) {
            st.append(Integer.toHexString(b));
        }
        this.dataWatcher.updateObject(23, st.toString());
        setupGolem();
        setupGolemInventory();
        NBTTagList nbttaglist2 = nbt.getTagList("Inventory", 10);
        this.inventory.readFromNBT(nbttaglist2);
        this.colors = nbt.getByteArray("colors");
        byte[] oldcolors = this.colors;
        this.colors = new byte[this.inventory.slotCount];
        for (int a3 = 0; a3 < this.inventory.slotCount; a3++) {
            this.colors[a3] = -1;
            if (a3 < oldcolors.length) {
                this.colors[a3] = oldcolors[a3];
            }
        }
        String st2 = "";
        byte[] arr$2 = this.colors;
        for (byte c : arr$2) {
            if (c == -1) {
                str = st2 + "h";
            } else {
                str = st2 + Integer.toHexString(c);
            }
            st2 = str;
        }
        this.dataWatcher.updateObject(22, st2);
    }

}
