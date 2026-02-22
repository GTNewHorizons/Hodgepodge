package com.mitchej123.hodgepodge.mixins.hooks;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * BFS-based leaf decay check. Searches outward from the leaf position through connected leaves, returning true as soon
 * as a sustaining log is found within range.
 */
public class LeafDecayHooks {

    private static final int MAX_RANGE = 7;
    private static final int MAX_SIDE = 2 * MAX_RANGE + 1;
    private static final int MAX_VOLUME = MAX_SIDE * MAX_SIDE * MAX_SIDE;
    private static final boolean[] visited = new boolean[MAX_VOLUME];
    private static final int[] queue = new int[MAX_VOLUME];
    private static final int[] DX = { -1, 1, 0, 0, 0, 0 };
    private static final int[] DY = { 0, 0, -1, 1, 0, 0 };
    private static final int[] DZ = { 0, 0, 0, 0, -1, 1 };

    /**
     * BFS decay check, called after isRemote and meta-bit checks have already passed.
     */
    public static void handleDecayChecked(Block block, World world, int x, int y, int z, int meta, int range,
            CallbackInfo ci) {
        final int r = range + 1;
        if (!world.checkChunksExist(x - r, y - r, z - r, x + r, y + r, z + r)) {
            ci.cancel();
            return;
        }

        if (isConnectedToLog(world, x, y, z, range)) {
            world.setBlockMetadataWithNotify(x, y, z, meta & -9, 4);
        } else {
            block.dropBlockAsItem(world, x, y, z, meta, 0);
            world.setBlockToAir(x, y, z);
        }

        ci.cancel();
    }

    /**
     * Returns true if the leaf at (x,y,z) is connected to a log within the given range.
     */
    public static boolean isConnectedToLog(World world, int x, int y, int z, int range) {
        final int side = 2 * range + 1;
        final int sideSquared = side * side;
        final int volume = sideSquared * side;
        final int maxDepth = range - 1;

        Arrays.fill(visited, 0, volume, false);

        int head = 0;
        int tail = 0;

        final int startIdx = range * sideSquared + range * side + range;
        visited[startIdx] = true;
        queue[tail++] = startIdx;

        while (head < tail) {
            final int entry = queue[head++];
            final int dist = entry >>> 16;
            final int idx = entry & 0xFFFF;

            final int dx = (idx / sideSquared) - range;
            final int dy = ((idx % sideSquared) / side) - range;
            final int dz = (idx % side) - range;

            for (int face = 0; face < 6; face++) {
                final int nx = dx + DX[face];
                final int ny = dy + DY[face];
                final int nz = dz + DZ[face];

                if (nx < -range || nx > range || ny < -range || ny > range || nz < -range || nz > range) continue;

                final int nIdx = (nx + range) * sideSquared + (ny + range) * side + (nz + range);
                if (visited[nIdx]) continue;
                visited[nIdx] = true;

                final Block block = world.getBlock(x + nx, y + ny, z + nz);

                if (block.canSustainLeaves(world, x + nx, y + ny, z + nz)) {
                    return true;
                }

                if (dist < maxDepth && block.isLeaves(world, x + nx, y + ny, z + nz)) {
                    queue[tail++] = ((dist + 1) << 16) | nIdx;
                }
            }
        }

        return false;
    }
}
