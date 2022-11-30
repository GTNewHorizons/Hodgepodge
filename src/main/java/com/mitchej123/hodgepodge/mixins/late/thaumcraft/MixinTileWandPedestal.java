package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import com.gtnewhorizon.mixinextras.injector.ModifyExpressionValue;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
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

    @ModifyExpressionValue(
            at =
                    @At(
                            remap = false,
                            target =
                                    "Lthaumcraft/common/items/wands/ItemWandCasting;getAspectsWithRoom(Lnet/minecraft/item/ItemStack;)Lthaumcraft/api/aspects/AspectList;",
                            value = "INVOKE"),
            method = "Lthaumcraft/common/tiles/TileWandPedestal;updateEntity()V",
            require = 1)
    private AspectList addVis(AspectList original, ItemWandCasting wand, ItemStack wandstack) {
        for (Aspect aspect : original.getAspects()) {
            int drained = VisNetHandler.drainVis(
                    worldObj, xCoord, yCoord, zCoord, aspect, 25); // Pedestal operates every 5 tick
            if (drained > 0) wand.addRealVis(wandstack, aspect, drained, true);
        }
        return original;
    }

    @ModifyExpressionValue(
            at =
                    @At(
                            target =
                                    "Lnet/minecraft/world/World;getTileEntity(III)Lnet/minecraft/tileentity/TileEntity;",
                            value = "INVOKE"),
            method = "Lthaumcraft/common/tiles/TileWandPedestal;findNodes()V",
            remap = false,
            require = 1)
    private TileEntity addCVNodes(TileEntity original) {
        if (original instanceof TileVisNode) {
            this.nodes.add(new ChunkCoordinates(original.xCoord, original.yCoord, original.zCoord));
        }
        return original;
    }
}
