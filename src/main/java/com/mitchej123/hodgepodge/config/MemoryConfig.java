package com.mitchej123.hodgepodge.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "hodgepodge", category = "memory")
public class MemoryConfig {

    @Config.Comment("Memory leak fixes")
    public static final MemoryLeakFixes leaks = new MemoryLeakFixes();

    @Config.Comment("Memory allocation fixes")
    public static final AllocationFixes allocs = new AllocationFixes();

    public static class MemoryLeakFixes {

        @Config.Comment("Fix ItemRenderer keeping a reference to the last rendered item when leaving a world")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean fixEntityRendererItemRendererLeak;

        @Config.Comment("Fix EventBus keeping object references after unregistering event handlers.")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean fixEventBusMemoryLeak;

        @Config.Comment("Clears the reference to the minecraft server once it has stopped")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean fixMinecraftServerLeak;

        @Config.Comment("Fix CoFH WorldServer leak in main mod class")
        @Config.DefaultBoolean(true)
        public boolean fixCoFHWorldLeak;

        @Config.Comment("Fix memory leak caused by FML network channels attributes")
        @Config.DefaultBoolean(true)
        public boolean fixNetworkChannelsMemoryLeak;

        @Config.Comment("Fix memory leak caused by minecraft's commandBase keeping a static reference to the server command handler")
        @Config.DefaultBoolean(true)
        public boolean fixServerCommandHandlerLeak;

        @Config.Comment("Fix redstone torch leaking world")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean fixRedstoneTorchWorldLeak;

        @Config.Comment("Fix EffectRenderer and RenderGlobal leaking world instance when leaving world")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean fixRenderersWorldLeak;

        @Config.Comment("Fix TileEntityRenderer leaking world instance when leaving world")
        @Config.DefaultBoolean(true)
        public boolean fixTileEntityRendererWorldLeak;

        @Config.Comment("Fix RenderManager leaking world instance when leaving world")
        @Config.DefaultBoolean(true)
        public boolean fixRenderManagerWorldLeak;

        @Config.Comment("Fix RenderBlocks static singleton leaking world instance when leaving world")
        @Config.DefaultBoolean(true)
        public boolean fixRenderBlocksWorldLeak;

        @Config.Comment("Fix PointedEntity leaking world instance when leaving world")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean fixPointedEntityLeak;

        @Config.Comment("Fix Enchantment Helper leaking world instance when leaving world")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean fixEnchantmentHelperLeak;

        @Config.Comment("Fix PlayerController leaking world instance when leaving world")
        @Config.DefaultBoolean(true)
        public boolean fixPlayerControllerWorldLeak;

        @Config.Comment("Fix NetHandlerClient leaking world instance when leaving world")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean fixNetHandlerClientWorldLeak;

        @Config.Comment("Fix WorldServer leaking entities when no players are present in a dimension")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean fixWorldServerLeakingUnloadedEntities;

        @Config.Comment("Fix skin manager leaking client world")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean fixSkinManagerLeakingClientWorld;

        @Config.Comment("Fix World static map storage leaking the server world")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean fixWorldMapStorageLeak;
    }

    public static class AllocationFixes {

        @Config.Comment("Clear FML Texture Errors to free memory")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean clearFMLTextureErrors;

        @Config.Comment("Stops witchery from spamming Enum#values()")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean fixWitcheryEnumValuesSpam;

        @Config.Comment("Caches the advanced Model renderers to speedup loading and avoid wasting memory with duplicate models")
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean cacheAdvancedModels;
    }
}
