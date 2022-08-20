package com.mitchej123.hodgepodge.mixins.thaumcraft;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
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

@Mixin(value = TileWandPedestal.class)
public class MixinTileWandPedestal extends TileThaumcraft {

    @Shadow(remap = false)
    ArrayList<ChunkCoordinates> nodes = null;

    @Shadow(remap = false)
    int counter = 0;

    @Redirect(
            method = "Lthaumcraft/common/tiles/TileWandPedestal;updateEntity()V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lthaumcraft/common/items/wands/ItemWandCasting;getAspectsWithRoom(Lnet/minecraft/item/ItemStack;)Lthaumcraft/api/aspects/AspectList;",
                            remap = false),
            require = 1)
    AspectList getAspectsWithRoomReplacement(ItemWandCasting wand, ItemStack wandstack) {
        AspectList as = wand.getAspectsWithRoom(wandstack);
        if (as != null && as.size() > 0) {
            for (Aspect aspect : as.getAspects()) {
                int drained = VisNetHandler.drainVis(
                        worldObj, xCoord, yCoord, zCoord, aspect, 25); // Pedestal operates every 5 tick
                if (drained > 0) wand.addRealVis(wandstack, aspect, drained, true);
            }
        }
        return as;
    }

    @Redirect(
            method = "Lthaumcraft/common/tiles/TileWandPedestal;findNodes()V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/World;getTileEntity(III)Lnet/minecraft/tileentity/TileEntity;"),
            require = 1)
    TileEntity addCVNodes(World w, int x, int y, int z) {
        TileEntity te = this.worldObj.getTileEntity(x, y, z);
        if (te instanceof TileVisNode) nodes.add(new ChunkCoordinates(x, y, z));
        return te;
    }
}
