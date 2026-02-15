package com.mitchej123.hodgepodge.mixins.hooks;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.SpeedupsConfig;

/**
 * BFS-based leaf decay check. Searches outward from the leaf position through connected leaves, returning true as soon
 * as a sustaining log is found within the configured range.
 */
public class LeafDecayHooks {

    private static final int range = SpeedupsConfig.leafDecayRange;
    private static final int maxDepth = range - 1;
    private static final int side = 2 * range + 1;
    private static final int sideSquared = side * side;
    private static final boolean[] visited = new boolean[side * side * side];
    private static final int[] queue = new int[side * side * side];
    private static final int[] DX = { -1, 1, 0, 0, 0, 0 };
    private static final int[] DY = { 0, 0, -1, 1, 0, 0 };
    private static final int[] DZ = { 0, 0, 0, 0, -1, 1 };

    private static int toIndex(int dx, int dy, int dz) {
        return (dx + range) * sideSquared + (dy + range) * side + (dz + range);
    }

    /**
     * Shared updateTick replacement for vanilla and BOP leaves. Checks connectivity via BFS, then either clears the
     * decay flag or drops + removes the block.
     */
    public static void handleDecayTick(Block block, World world, int x, int y, int z, CallbackInfo ci) {
        final int meta = world.getBlockMetadata(x, y, z);

        // Only process leaves that need a decay check: bit 8 set (needs check) and bit 4 clear (not player-placed)
        if ((meta & 8) == 0 || (meta & 4) != 0) return;

        final int r = range + 1;
        if (!world.checkChunksExist(x - r, y - r, z - r, x + r, y + r, z + r)) {
            ci.cancel();
            return;
        }

        if (isConnectedToLog(world, x, y, z)) {
            world.setBlockMetadataWithNotify(x, y, z, meta & -9, 4);
        } else {
            block.dropBlockAsItem(world, x, y, z, meta, 0);
            world.setBlockToAir(x, y, z);
        }

        ci.cancel();
    }

    /**
     * Returns true if the leaf at (x,y,z) is connected to a log within the configured range.
     */
    public static boolean isConnectedToLog(World world, int x, int y, int z) {
        Arrays.fill(visited, false);

        int head = 0;
        int tail = 0;

        final int startIdx = toIndex(0, 0, 0);
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

                final int nIdx = toIndex(nx, ny, nz);
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
