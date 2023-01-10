package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.Common;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RandomPositionGenerator.class)
public class MixinRandomPositionGenerator {

    /**
     * @author mitchej123
     * @reason Backported fix north/west bias
     */
    @Overwrite
    private static Vec3 findRandomTargetBlock(EntityCreature entityCreature, int hor, int ver, Vec3 facing) {
        boolean found = false;
        double tx = 0, ty = 0, tz = 0;
        float bestValue = -99999.0F;
        boolean tooFar = false;

        if (entityCreature.hasHome()) {
            final double d0 = entityCreature
                            .getHomePosition()
                            .getDistanceSquared(
                                    MathHelper.floor_double(entityCreature.posX),
                                    MathHelper.floor_double(entityCreature.posY),
                                    MathHelper.floor_double(entityCreature.posZ))
                    + 4.0F;
            final double d1 = entityCreature.func_110174_bM() /*getMaximumHomeDistance()*/ + (double) hor;
            tooFar = d0 < d1 * d1;
        }

        for (int i = 0; i < 10; ++i) {
            final int x1 = Common.RNG.nextInt(2 * hor + 1) - hor;
            final int y1 = Common.RNG.nextInt(2 * ver + 1) - ver;
            final int z1 = Common.RNG.nextInt(2 * hor + 1) - hor;

            if (facing == null || (double) x1 * facing.xCoord + (double) z1 * facing.zCoord >= 0.0D) {
                // Use the rounded coordinates for comparision since `isWithinHomeDistance` takes int params
                final int x2 = x1 + MathHelper.floor_double(entityCreature.posX);
                final int y2 = y1 + MathHelper.floor_double(entityCreature.posY);
                final int z2 = z1 + MathHelper.floor_double(entityCreature.posZ);

                if (!tooFar || entityCreature.isWithinHomeDistance(x2, y2, z2)) {
                    final float blockPathWeight = entityCreature.getBlockPathWeight(x2, y2, z2);

                    if (blockPathWeight > bestValue) {
                        bestValue = blockPathWeight;
                        // But use the un-rounded coordinates for moving (so we avoid chopping off fractional
                        // coordinates and biasing to the NW)
                        tx = entityCreature.posX + x1;
                        ty = entityCreature.posY + y1;
                        tz = entityCreature.posZ + z1;
                        found = true;
                        if (blockPathWeight == 0.0F) {
                            // Don't keep searching if the black path weight function isn't implemented
                            break;
                        }
                    }
                }
            }
        }

        return found ? Vec3.createVectorHelper(tx, ty, tz) : null;
    }
}
