package hodgepodge;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mockito.MockedStatic;

import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;

import cpw.mods.fml.common.Loader;

class PopulationOverflowTest {

    @TestFactory
    Stream<DynamicTest> population() throws Exception {
        return MixinScenarios.run(getClass());
    }

    public static class Scenarios {

        private static final int MARKER_Y = 250;
        private static final int[][] DISTANT_HORIZONS_LOADS = { { 1, 0 }, { 0, 1 }, { 1, 1 }, { 0, 0 } };
        private final MarkerGenerator generator = new MarkerGenerator();
        private MockedStatic<Loader> loaderLookup;
        private MockedStatic<MinecraftServer> serverLookup;
        private WorldServer world;
        private ChunkProviderServer cps;

        public void setUp() throws Exception {
            loaderLookup = mockStatic(Loader.class);
            loaderLookup.when(Loader::instance).thenReturn(mock(Loader.class));
            Bootstrap.func_151354_b();
            MinecraftServer server = mock(MinecraftServer.class);
            when(server.getTickCounter()).thenReturn(100);
            serverLookup = mockStatic(MinecraftServer.class);
            serverLookup.when(MinecraftServer::getServer).thenReturn(server);

            world = mock(WorldServer.class);
            setField(World.class, world, "provider", mock(WorldProvider.class));
            cps = new ChunkProviderServer(world, null, generator);
            setField(World.class, world, "chunkProvider", cps);
            doCallRealMethod().when(world).getChunkFromChunkCoords(anyInt(), anyInt());
            ChunkGenScheduler.onServerTickStart();
        }

        public void tearDown() {
            ChunkGenScheduler.enableChunkLoads();
            ChunkGenScheduler.clearDimensionData();
            if (serverLookup != null) serverLookup.close();
            if (loaderLookup != null) loaderLookup.close();
        }

        public void testWriteLandsAfterLoadChunk() {
            load(DISTANT_HORIZONS_LOADS);
            assertMarkerLanded();
            assertGuardBlocks();
        }

        public void testWriteLandsAfterPopulateChunkOnDiskLoad() {
            for (int[] pos : DISTANT_HORIZONS_LOADS) {
                Chunk chunk = generator.provideChunk(pos[0], pos[1]);
                cps.loadedChunkHashMap.add(ChunkCoordIntPair.chunkXZ2Int(pos[0], pos[1]), chunk);
                cps.loadedChunks.add(chunk);
            }
            loaded(1, 1).populateChunk(cps, cps, 1, 1);
            assertMarkerLanded();
            assertGuardBlocks();
        }

        public void testUnpopulatedLoadIsTracked() {
            cps.loadChunk(5, 5);
            assertFalse(loaded(5, 5).isTerrainPopulated, "isolated chunk was populated without its neighbors");
            assertTrue(
                    ChunkGenScheduler.forDimension(0).shouldProtectFromUnload(ChunkCoordIntPair.chunkXZ2Int(5, 5)),
                    "chunk generated without population is not tracked as TERRAIN");
        }

        public void testNestedPopulationIsDeferred() {
            load(new int[][] { { -1, 0 }, { -2, 1 }, { -1, 1 } });
            load(DISTANT_HORIZONS_LOADS);
            assertTrue(cps.chunkExists(-2, 0), "population write target (-2,0) was never generated");
            assertFalse(loaded(-2, 0).isTerrainPopulated, "nested population of (-2,0) ran instead of being deferred");
            assertTrue(
                    ChunkGenScheduler.forDimension(0).shouldProtectFromUnload(ChunkCoordIntPair.chunkXZ2Int(-2, 0)),
                    "deferred population of (-2,0) is not tracked as TERRAIN");
        }

        public void testNestedPopulationRunsBeforeTicking() throws Exception {
            setField(ChunkGenScheduler.class, null, "tickingStarted", false);
            setField(ChunkGenScheduler.class, null, "chunkLoadBlocked", false);
            try {
                load(new int[][] { { -1, 0 }, { -2, 1 }, { -1, 1 } });
                load(DISTANT_HORIZONS_LOADS);
                assertTrue(cps.chunkExists(-2, 0), "population write target (-2,0) was never generated");
                assertTrue(loaded(-2, 0).isTerrainPopulated, "nested population of (-2,0) was deferred before ticking");
            } finally {
                setField(ChunkGenScheduler.class, null, "tickingStarted", true);
            }
        }

        private void assertGuardBlocks() {
            assertSame(
                    cps.defaultEmptyChunk,
                    cps.provideChunk(10, 10),
                    "blocked provideChunk outside population must return EmptyChunk");
            assertFalse(cps.chunkExists(10, 10), "blocked provideChunk outside population generated a chunk");
        }

        private void load(int[][] positions) {
            for (int[] pos : positions) cps.loadChunk(pos[0], pos[1]);
        }

        private Chunk loaded(int x, int z) {
            return (Chunk) cps.loadedChunkHashMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x, z));
        }

        private void assertMarkerLanded() {
            assertTrue(
                    cps.chunkExists(-2, 0),
                    "population write target (-2,0) was never generated; the write went to EmptyChunk");
            assertSame(
                    Blocks.lapis_block,
                    loaded(-2, 0).getBlock(0, MARKER_Y, 0),
                    "population write into ungenerated chunk was dropped");
        }

        private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        }

        private class MarkerGenerator implements IChunkProvider {

            @Override
            public boolean chunkExists(int x, int z) {
                return true;
            }

            @Override
            public Chunk provideChunk(int x, int z) {
                return new Chunk(world, x, z);
            }

            @Override
            public Chunk loadChunk(int x, int z) {
                return new Chunk(world, x, z);
            }

            @Override
            public void populate(IChunkProvider provider, int x, int z) {
                world.getChunkFromChunkCoords(x - 2, z).func_150807_a(0, MARKER_Y, 0, Blocks.lapis_block, 0);
            }

            @Override
            public boolean saveChunks(boolean all, IProgressUpdate progress) {
                return true;
            }

            @Override
            public boolean unloadQueuedChunks() {
                return false;
            }

            @Override
            public boolean canSave() {
                return false;
            }

            @Override
            public String makeString() {
                return "MarkerGenerator";
            }

            @Override
            public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType type, int x, int y, int z) {
                return Collections.emptyList();
            }

            @Override
            public ChunkPosition func_147416_a(World world, String structure, int x, int y, int z) {
                return null;
            }

            @Override
            public int getLoadedChunkCount() {
                return 0;
            }

            @Override
            public void recreateStructures(int x, int z) {}

            @Override
            public void saveExtraData() {}
        }
    }
}
