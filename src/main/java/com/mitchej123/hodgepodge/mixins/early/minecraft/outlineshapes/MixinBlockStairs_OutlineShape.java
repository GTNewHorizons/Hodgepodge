package com.mitchej123.hodgepodge.mixins.early.minecraft.outlineshapes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizon.gtnhlib.api.IBlockWithOutlineShape;
import com.gtnewhorizon.gtnhlib.api.OutlineShape;

@Mixin(BlockStairs.class)
public abstract class MixinBlockStairs_OutlineShape implements IBlockWithOutlineShape {

    @Inject(method = "collisionRayTrace", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$rayTraceOutlineShape(World world, int x, int y, int z, Vec3 start, Vec3 end,
            CallbackInfoReturnable<MovingObjectPosition> cir) {
        OutlineShape shape = getOutlineShape(world, x, y, z);
        if (shape == null || shape.isEmpty()) return;
        ((Block) (Object) this).setBlockBoundsBasedOnState(world, x, y, z);
        cir.setReturnValue(shape.rayTrace(x, y, z, start, end));
    }
}
