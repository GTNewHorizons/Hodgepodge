package com.mitchej123.hodgepodge.mixins.wandPedestalCV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileWandPedestal;

@Mixin(TileWandPedestal.class)
public class MixinTileWandPedestal extends TileThaumcraft {
    @Shadow
    ArrayList<ChunkCoordinates> nodes = null;
    @Shadow int counter = 0;
    Map<Aspect,Integer> accumulatedCV = new HashMap<>();
    private static Aspect[] PRIMALS = null;
    @Redirect(method ="Lthaumcraft/common/tiles/TileWandPedestal;updateEntity()V",
        at=@At(value = "INVOKE",target="Lthaumcraft/common/items/wands/ItemWandCasting;getAspectsWithRoom(Lnet/minecraft/item/ItemStack;)Lthaumcraft/api/aspects/AspectList;"))
    AspectList getAspectsWithRoomReplacement(ItemWandCasting wand, ItemStack wandstack)
    {
        AspectList as = wand.getAspectsWithRoom(wandstack);
        if (as != null && as.size() > 0 && this.counter % 5 == 0) {
            for(Aspect aspect : as.getAspects()) {
                int drained = VisNetHandler.drainVis(worldObj, xCoord, yCoord, zCoord, aspect, 100);
                if (drained > 0)
                    accumulatedCV.compute(aspect, (a,c) ->
                    {
                        if (a == null) return drained;
                        else {
                            if (c + drained > 100) {
                                wand.addVis(wandstack, aspect, 1, true);
                                return c + drained - 100;
                            }
                            return c + drained;
                        }
                    });
            }
        }
        return as;
    }

    @Redirect(method="Lthaumcraft/common/tiles/TileWandPedestal;findNodes()V", at=@At(value="INVOKE", target="Lnet/minecraft/world/World;getTileEntity(III)Lnet/minecraft/tileentity/TileEntity;"))
    TileEntity addCVNodes(World w, int x, int y, int z)
    {
        TileEntity te = this.worldObj.getTileEntity(x, y, z);
        if (te instanceof TileVisNode)
            nodes.add(new ChunkCoordinates(x, y, z));
        return te;
    }

    private void initPrimals() {
        if (PRIMALS == null)
            PRIMALS = new Aspect[]{Aspect.AIR, Aspect.EARTH, Aspect.FIRE, Aspect.WATER, Aspect.ORDER, Aspect.ENTROPY};
    }

    @Redirect(method="Lthaumcraft/common/tiles/TileWandPedestal;readCustomNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at=@At(value="INVOKE", target="Lnet/minecraft/nbt/NBTTagCompound;getTagList(Ljava/lang/String;I)Lnet/minecraft/nbt/NBTTagList;"))
    public NBTTagList getTagList(NBTTagCompound compound, String name, int count)
    {
        NBTTagList nbttaglist = compound.getTagList(name, count);
        NBTTagCompound cv = compound.getCompoundTag("AccumulatedCV");
        initPrimals();
        for (Aspect a : PRIMALS)
            accumulatedCV.put(a, cv.getInteger(a.getTag()));
        return nbttaglist;
    }
    @Redirect(method="Lthaumcraft/common/tiles/TileWandPedestal;writeCustomNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at=@At(value="INVOKE", target="Lnet/minecraft/nbt/NBTTagCompound;setTag(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V"))
    public void setTag(NBTTagCompound compound, String name, NBTBase nbt)
    {
        compound.setTag(name, nbt);
        NBTTagCompound cv = new NBTTagCompound();
        initPrimals();
        for (Aspect a : PRIMALS)
            cv.setInteger(a.getTag(), accumulatedCV.getOrDefault(a, 0));
        compound.setTag("AccumulatedCV", cv);
    }
}
