package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
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

    @Redirect(
            at =
                    @At(
                            remap = false,
                            target =
                                    "Lthaumcraft/common/items/wands/ItemWandCasting;getAspectsWithRoom(Lnet/minecraft/item/ItemStack;)Lthaumcraft/api/aspects/AspectList;",
                            value = "INVOKE"),
            method = "Lthaumcraft/common/tiles/TileWandPedestal;updateEntity()V",
            require = 1)
    private AspectList hodgepodge$getAspectsWithRoomReplacement(ItemWandCasting wand, ItemStack wandstack) {
        AspectList as = wand.getAspectsWithRoom(wandstack);
        if (as != null && as.size() > 0) {
            for (Aspect aspect : as.getAspects()) {
                int drained = VisNetHandler.drainVis(
                        this.worldObj,
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        aspect,
                        25); // Pedestal operates every 5 tick
                if (drained > 0) {
                    wand.addRealVis(wandstack, aspect, drained, true);
                }
            }
        }
        return as;
    }

    @Inject(
            at =
                    @At(
                            args = "classValue=thaumcraft/api/nodes/INode",
                            target =
                                    "Lnet/minecraft/world/World;getTileEntity(III)Lnet/minecraft/tileentity/TileEntity;",
                            value = "CONSTANT"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            method = "Lthaumcraft/common/tiles/TileWandPedestal;findNodes()V",
            remap = false,
            require = 1)
    private void hodgepodge$addCVNodes(CallbackInfo ci, int xx, int yy, int zz, TileEntity te) {
        if (te instanceof TileVisNode) {
            this.nodes.add(new ChunkCoordinates(te.xCoord, te.yCoord, te.zCoord));
        }
    }
}
