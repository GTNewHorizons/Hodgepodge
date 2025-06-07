package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeEventFactory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.mixins.interfaces.SpawnListEntryExt;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;

import cpw.mods.fml.common.eventhandler.Event;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongLists;

@Mixin(value = SpawnerAnimals.class, priority = 900)
public class MixinSpawnerAnimals_optimizeSpawning {

    @SuppressWarnings("unused")
    @Shadow
    private final HashMap<ChunkCoordIntPair, Boolean> eligibleChunksForSpawning = null;

    @Unique
    private static final EnumCreatureType[] CREATURE_TYPE_VALUES = EnumCreatureType.values();
    @Unique
    private final Long2BooleanMap hodgepodge$eligibleChunks = new Long2BooleanOpenHashMap();
    @Unique
    private final LongList hodgepodge$shuffledChunks = new LongArrayList();

    @Unique
    private final BlockPos hodgepodge$reusableBlockPos = new BlockPos(0, 0, 0);

    @Unique
    private BlockPos hodgepodge$getRandomPosInChunk(World world, int chunkX, int chunkZ) {
        final Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        final int randX = chunkX * 16 + world.rand.nextInt(16);
        final int randZ = chunkZ * 16 + world.rand.nextInt(16);
        final int randY = world.rand
                .nextInt(chunk == null ? world.getActualHeight() : chunk.getTopFilledSegment() + 16 - 1);
        return hodgepodge$reusableBlockPos.set(randX, randY, randZ);
    }

    /**
     * adds all chunks within the spawn radius of the players to eligibleChunksForSpawning. pars: the world,
     * hostileCreatures, passiveCreatures. returns number of eligible chunks.
     *
     * @author mitchej123
     * @reason Optimize mob spawning - Switch out the HashMap for a Long2BooleanMap
     */
    @Overwrite
    public int findChunksForSpawning(WorldServer world, boolean spawnHostileMobs, boolean spawnPeacefulMobs,
            boolean spawnOnSetTickRate) {
        if (!spawnHostileMobs && !spawnPeacefulMobs) {
            return 0;
        }
        this.hodgepodge$eligibleChunks.clear();
        final int spawnRange = SpeedupsConfig.limitMobSpawningToViewDistance
                ? Math.min(8, MinecraftServer.getServer().getConfigurationManager().getViewDistance())
                : 8;

        for (int playerIndex = 0; playerIndex < world.playerEntities.size(); ++playerIndex) {
            final EntityPlayer player = world.playerEntities.get(playerIndex);
            final int playerChunkX = MathHelper.floor_double(player.posX / 16.0D);
            final int playerChunkZ = MathHelper.floor_double(player.posZ / 16.0D);

            for (int offsetX = -spawnRange; offsetX <= spawnRange; ++offsetX) {
                for (int offsetZ = -spawnRange; offsetZ <= spawnRange; ++offsetZ) {
                    boolean isEdge = Math.abs(offsetX) == spawnRange || Math.abs(offsetZ) == spawnRange;
                    final long packedPos = ChunkPosUtil.toLong(offsetX + playerChunkX, offsetZ + playerChunkZ);

                    if (!isEdge) {
                        // False - not at the edge, eligible for spawning
                        this.hodgepodge$eligibleChunks.put(packedPos, false);
                    } else if (!this.hodgepodge$eligibleChunks.containsKey(packedPos)) {
                        // True - counts for loaded chunks, but not eligible for spawning
                        this.hodgepodge$eligibleChunks.put(packedPos, true);
                    }
                }
            }
        }

        final int spawnRangeOffset = 6;
        final ChunkCoordinates spawnPoint = world.getSpawnPoint();
        final int creatureTypeCount = CREATURE_TYPE_VALUES.length;
        int totalSpawnedEntities = 0;

        final double playerDistanceCheck = 24.0D;

        // noinspection ForLoopReplaceableByForEach
        for (int creatureTypeIndex = 0; creatureTypeIndex < creatureTypeCount; ++creatureTypeIndex) {
            EnumCreatureType creatureType = CREATURE_TYPE_VALUES[creatureTypeIndex];

            if ((creatureType.getPeacefulCreature() && !spawnPeacefulMobs)
                    || (!creatureType.getPeacefulCreature() && !spawnHostileMobs)
                    || (creatureType.getAnimal() && !spawnOnSetTickRate)
                    || world.countEntities(creatureType, true)
                            > creatureType.getMaxNumberOfCreature() * this.hodgepodge$eligibleChunks.size() / 256) {

                continue;
            }
            // Clear doesn't reset the backing capacity, just sets the size to 0, so we can reuse it
            hodgepodge$shuffledChunks.clear();
            // AddAll will take a fast path with a LongCollection - which the keySet() is a subclass of
            hodgepodge$shuffledChunks.addAll(hodgepodge$eligibleChunks.keySet());
            LongLists.shuffle(hodgepodge$shuffledChunks, world.rand);

            // We can't use a for-each loop here as java will use the boxed variant
            LongListIterator iterator = hodgepodge$shuffledChunks.iterator();
            while (iterator.hasNext()) {
                final long chunkPos = iterator.nextLong();

                if (this.hodgepodge$eligibleChunks.get(chunkPos)) continue;
                final int chunkX = ChunkPosUtil.getPackedX(chunkPos);
                final int chunkZ = ChunkPosUtil.getPackedZ(chunkPos);

                final BlockPos spawnPosition = hodgepodge$getRandomPosInChunk(world, chunkX, chunkZ);
                final int spawnX = spawnPosition.x;
                final int spawnY = spawnPosition.y;
                final int spawnZ = spawnPosition.z;

                if (!world.getBlock(spawnX, spawnY, spawnZ).isNormalCube()
                        && world.getBlock(spawnX, spawnY, spawnZ).getMaterial() == creatureType.getCreatureMaterial()) {
                    int packSize = 0;

                    for (int spawnAttempt = 0; spawnAttempt < 3; ++spawnAttempt) {
                        int attemptX = spawnX;
                        int attemptY = spawnY;
                        int attemptZ = spawnZ;
                        BiomeGenBase.SpawnListEntry spawnListEntry = null;
                        IEntityLivingData entityLivingData = null;

                        for (int attempt = 0; attempt < 4; ++attempt) {
                            attemptX += world.rand.nextInt(spawnRangeOffset) - world.rand.nextInt(spawnRangeOffset);
                            attemptZ += world.rand.nextInt(spawnRangeOffset) - world.rand.nextInt(spawnRangeOffset);

                            if (canCreatureTypeSpawnAtLocation(creatureType, world, attemptX, attemptY, attemptZ)) {
                                final float spawnPosX = (float) attemptX + 0.5F;
                                final float spawnPosY = (float) attemptY;
                                final float spawnPosZ = (float) attemptZ + 0.5F;

                                if (world.getClosestPlayer(spawnPosX, spawnPosY, spawnPosZ, playerDistanceCheck)
                                        == null) {
                                    float distanceX = spawnPosX - (float) spawnPoint.posX;
                                    float distanceY = spawnPosY - (float) spawnPoint.posY;
                                    float distanceZ = spawnPosZ - (float) spawnPoint.posZ;
                                    float distanceSquared = distanceX * distanceX + distanceY * distanceY
                                            + distanceZ * distanceZ;

                                    if (distanceSquared >= 576.0F) {
                                        if (spawnListEntry == null) {
                                            spawnListEntry = world
                                                    .spawnRandomCreature(creatureType, attemptX, attemptY, attemptZ);

                                            if (spawnListEntry == null) {
                                                break;
                                            }
                                        }

                                        EntityLiving entityLiving;

                                        try {
                                            entityLiving = ((SpawnListEntryExt) spawnListEntry).constructEntity(world);
                                        } catch (Exception exception) {
                                            exception.printStackTrace();
                                            return totalSpawnedEntities;
                                        }

                                        entityLiving.setLocationAndAngles(
                                                spawnPosX,
                                                spawnPosY,
                                                spawnPosZ,
                                                world.rand.nextFloat() * 360.0F,
                                                0.0F);

                                        Event.Result canSpawn = ForgeEventFactory
                                                .canEntitySpawn(entityLiving, world, spawnPosX, spawnPosY, spawnPosZ);
                                        if (canSpawn == Event.Result.ALLOW || (canSpawn == Event.Result.DEFAULT
                                                && entityLiving.getCanSpawnHere())) {
                                            ++packSize;
                                            world.spawnEntityInWorld(entityLiving);
                                            if (!ForgeEventFactory.doSpecialSpawn(
                                                    entityLiving,
                                                    world,
                                                    spawnPosX,
                                                    spawnPosY,
                                                    spawnPosZ)) {
                                                entityLivingData = entityLiving.onSpawnWithEgg(entityLivingData);
                                            }

                                            if (spawnAttempt >= ForgeEventFactory.getMaxSpawnPackSize(entityLiving)) {
                                                break;
                                            }
                                        }
                                        totalSpawnedEntities += packSize;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return totalSpawnedEntities;
    }

    /**
     * Returns whether the specified creature type can spawn at the specified location.
     *
     * @author mitchej123
     * @reason Optimize mob spawning - Reorder shortcircuit checks and avoid calling getBlock more than needed Also
     *         leaves room to add additional checks
     */
    @Overwrite
    public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType creatureType, World world, int x, int y,
            int z) {
        if (creatureType.getCreatureMaterial() == Material.water) {
            return world.getBlock(x, y, z).getMaterial().isLiquid()
                    && world.getBlock(x, y - 1, z).getMaterial().isLiquid()
                    && !world.getBlock(x, y + 1, z).isNormalCube();
        } else if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) {
            return false;
        } else {
            final Block blockBelow = world.getBlock(x, y - 1, z);
            if (blockBelow != Blocks.bedrock && blockBelow.canCreatureSpawn(creatureType, world, x, y - 1, z)) {
                final Block block = world.getBlock(x, y, z);
                return !block.isNormalCube() && !block.getMaterial().isLiquid()
                        && !world.getBlock(x, y + 1, z).isNormalCube();
            } else {
                return false;
            }
        }
    }

    /**
     * Called during chunk generation to spawn initial creatures.
     *
     * @author mitchej123
     * @reason Optimize mob spawning - Swaps out the constructor with a cached methodref; other micro-optimizations
     *         Leaves room for additional optimizations and options for server operators
     */
    @Overwrite
    public static void performWorldGenSpawning(World world, BiomeGenBase biome, int startX, int startZ, int areaWidth,
            int areaHeight, Random random) {
        List<BiomeGenBase.SpawnListEntry> spawnableList = biome.getSpawnableList(EnumCreatureType.creature);

        if (spawnableList.isEmpty()) {
            return;
        }
        while (random.nextFloat() < biome.getSpawningChance()) {
            BiomeGenBase.SpawnListEntry spawnListEntry = (BiomeGenBase.SpawnListEntry) WeightedRandom
                    .getRandomItem(world.rand, spawnableList);
            IEntityLivingData entityLivingData = null;
            int groupCount = spawnListEntry.minGroupCount
                    + random.nextInt(1 + spawnListEntry.maxGroupCount - spawnListEntry.minGroupCount);
            int posX = startX + random.nextInt(areaWidth);
            int posZ = startZ + random.nextInt(areaHeight);
            final int initialPosX = posX;
            final int initialPosZ = posZ;

            for (int i = 0; i < groupCount; ++i) {
                boolean spawned = false;

                for (int j = 0; !spawned && j < 4; ++j) {
                    int posY = world.getTopSolidOrLiquidBlock(posX, posZ);

                    if (canCreatureTypeSpawnAtLocation(EnumCreatureType.creature, world, posX, posY, posZ)) {
                        float spawnPosX = (float) posX + 0.5F;
                        float spawnPosY = (float) posY;
                        float spawnPosZ = (float) posZ + 0.5F;
                        EntityLiving entityLiving;

                        try {
                            entityLiving = ((SpawnListEntryExt) spawnListEntry).constructEntity(world);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            continue;
                        }

                        entityLiving.setLocationAndAngles(
                                spawnPosX,
                                spawnPosY,
                                spawnPosZ,
                                random.nextFloat() * 360.0F,
                                0.0F);
                        world.spawnEntityInWorld(entityLiving);
                        entityLivingData = entityLiving.onSpawnWithEgg(entityLivingData);
                        spawned = true;
                    }

                    posX += random.nextInt(5) - random.nextInt(5);
                    posZ += random.nextInt(5) - random.nextInt(5);

                    while (posX < startX || posX >= startX + areaWidth
                            || posZ < startZ
                            || posZ >= startZ + areaHeight) {
                        posX = initialPosX + random.nextInt(5) - random.nextInt(5);
                        posZ = initialPosZ + random.nextInt(5) - random.nextInt(5);
                    }
                }
            }
        }
    }

}
