package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import java.util.ArrayList;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.items.baubles.ItemAmuletVis;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileWandPedestal;

@Mixin(value = TileWandPedestal.class)
public abstract class MixinTileWandPedestal extends TileThaumcraft implements ISidedInventory {

    private int hodgepodge$ticksSinceLastSync = 0;
    private boolean hodgepodge$needSync;

    @Shadow(remap = false)
    ArrayList<ChunkCoordinates> nodes = null;

    @Shadow(remap = false)
    public boolean draining;

    @Shadow(remap = false)
    private boolean somethingChanged;

    @Inject(
            method = "updateEntity",
            at = @At(
                    remap = false,
                    value = "FIELD",
                    target = "Lthaumcraft/common/tiles/TileWandPedestal;draining:Z",
                    opcode = Opcodes.PUTFIELD,
                    ordinal = 0,
                    shift = At.Shift.AFTER,
                    by = 1),
            require = 1)
    private void hodgepodge$rechargeViaCV(CallbackInfo ci) {
        // no null check because vanilla does it
        ItemStack stack = getStackInSlot(0);

        if (stack.getItem() instanceof ItemWandCasting wand) {
            AspectList as = wand.getAspectsWithRoom(stack);
            if (as != null && as.size() > 0) {
                for (Aspect aspect : as.getAspects()) {
                    // Pedestal operates every 5 ticks
                    int drained = VisNetHandler
                            .drainVis(this.worldObj, this.xCoord, this.yCoord, this.zCoord, aspect, 25);
                    if (drained > 0) {
                        wand.addRealVis(stack, aspect, drained, true);
                        draining = true;
                        somethingChanged = true;
                        hodgepodge$needSync = true;
                    }
                }
            }
        } else if (stack.getItem() instanceof ItemAmuletVis amulet) {
            AspectList as = amulet.getAspectsWithRoom(stack);
            if (as != null && as.size() > 0) {
                for (Aspect aspect : as.getAspects()) {
                    // Pedestal operates every 5 ticks
                    int drained = VisNetHandler
                            .drainVis(this.worldObj, this.xCoord, this.yCoord, this.zCoord, aspect, 25);
                    if (drained > 0) {
                        amulet.addRealVis(stack, aspect, drained, true);
                        draining = true;
                        somethingChanged = true;
                        hodgepodge$needSync = true;
                    }
                }
            }
        }
    }

    @Inject(method = "updateEntity", at = @At("TAIL"))
    private void hodgepodge$onTickEnd(CallbackInfo ci) {
        if (!worldObj.isRemote && hodgepodge$needSync && hodgepodge$ticksSinceLastSync++ > 5) {
            hodgepodge$ticksSinceLastSync = 0;
            hodgepodge$needSync = false;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Inject(
            at = @At(
                    args = "classValue=thaumcraft/api/nodes/INode",
                    target = "Lnet/minecraft/world/World;getTileEntity(III)Lnet/minecraft/tileentity/TileEntity;",
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
