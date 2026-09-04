package hodgepodge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mockito.MockedStatic;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.mitchej123.hodgepodge.mixins.interfaces.LoginSessionState;
import com.mitchej123.hodgepodge.util.LoginSessionIndex;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;

/** Runs the production mixins against Minecraft's lifecycle, with no sockets, world generation or mod handshake. */
class LoginSessionLifecycleTest {

    @TestFactory
    Stream<DynamicTest> lifecycle() throws Exception {
        // Minecraft must be loaded through LaunchWrapper before JUnit/Mockito can load any of its classes.
        Set<URL> classpath = new LinkedHashSet<>();
        for (ClassLoader loader = getClass().getClassLoader(); loader != null; loader = loader.getParent()) {
            if (loader instanceof URLClassLoader) {
                classpath.addAll(Arrays.asList(((URLClassLoader) loader).getURLs()));
            }
        }
        for (String entry : System.getProperty("java.class.path").split(File.pathSeparator)) {
            classpath.add(new File(entry).toURI().toURL());
        }
        Launch.classLoader = new LaunchClassLoader(classpath.toArray(new URL[0]));
        Launch.blackboard = new HashMap<>();
        Launch.blackboard.put("TweakClasses", new ArrayList<>());
        Launch.blackboard.put("Tweaks", new ArrayList<>());
        for (String prefix : new String[] { "org.spongepowered.", "org.objectweb.asm.", "com.llamalad7.", "org.junit.",
                "org.mockito.", "net.bytebuddy.", "org.objenesis." }) {
            Launch.classLoader.addClassLoaderExclusion(prefix);
        }
        // Do not launch unrelated FML coremods or discover their mixin configs from dependency manifests.
        Class.forName("org.spongepowered.asm.launch.platform.MixinContainer");
        List<String> agents = GlobalProperties.get(GlobalProperties.Keys.AGENTS);
        agents.clear();
        Launch.classLoader.registerTransformer("cpw.mods.fml.common.asm.transformers.EventSubscriptionTransformer");
        MixinBootstrap.init();
        MixinExtrasBootstrap.init();
        Mixins.addConfiguration("mixins.hodgepodge.login-test.json");
        Method phase = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
        phase.setAccessible(true);
        phase.invoke(null, MixinEnvironment.Phase.DEFAULT);
        MixinEnvironment.getCurrentEnvironment().setSide(MixinEnvironment.Side.SERVER);

        Class<?> scenarios = Launch.classLoader.loadClass(getClass().getName() + "$Scenarios");
        return Arrays.stream(scenarios.getDeclaredMethods()).filter(method -> method.getName().startsWith("test"))
                .map(method -> DynamicTest.dynamicTest(method.getName(), () -> {
                    Object fixture = scenarios.getConstructor().newInstance();
                    try {
                        scenarios.getMethod("setUp").invoke(fixture);
                        method.invoke(fixture);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    } finally {
                        scenarios.getMethod("tearDown").invoke(fixture);
                    }
                }));
    }

    // Use named test methods so JUnit cannot discover and execute this fixture without the transforming classloader.
    public static class Scenarios {

        private static final UUID PLAYER_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
        private final List<EmbeddedChannel> channels = new ArrayList<>();
        private final List<NetworkManager> managers = new ArrayList<>();
        private final List<Integer> savedInventory = new ArrayList<>();
        private final List<EntityPlayerMP> saveOverrides = new ArrayList<>();
        private final List<String> sequence = new ArrayList<>();
        private MinecraftServer server;
        private ServerConfigurationManager scm;
        private NetworkSystem network;
        private WorldServer world;
        private EventBus fmlBus;
        private MockedStatic<Loader> loaderLookup;
        private MockedStatic<FMLCommonHandler> fmlLookup;
        private MockedStatic<MinecraftServer> serverLookup;
        private MockedStatic<FMLNetworkHandler> handshake;
        private int disconnectPosts;

        public void setUp() throws Exception {
            loaderLookup = mockStatic(Loader.class);
            Loader loader = mock(Loader.class);
            when(loader.getCallableCrashInformation()).thenReturn(mock(ICrashCallable.class));
            ModContainer owner = mock(ModContainer.class);
            when(owner.getModId()).thenReturn("minecraft");
            when(loader.activeModContainer()).thenReturn(owner);
            loaderLookup.when(Loader::instance).thenReturn(loader);
            fmlLookup = mockStatic(FMLCommonHandler.class);
            FMLCommonHandler fml = mock(FMLCommonHandler.class);
            fmlLookup.when(FMLCommonHandler::instance).thenReturn(fml);
            // Expected listener failures should propagate without invoking Forge's global crash-reporting logger.
            fmlBus = new EventBus((bus, event, listeners, index, failure) -> {});
            when(fml.bus()).thenReturn(fmlBus);
            doCallRealMethod().when(fml).firePlayerLoggedOut(any());
            Bootstrap.func_151354_b();
            fmlBus.register(this);
            MinecraftForge.EVENT_BUS.register(this);

            server = mock(MinecraftServer.class);
            scm = spy(new ServerConfigurationManager(server) {

                @Override
                protected void writePlayerData(EntityPlayerMP player) {
                    // The injected condition must run before a subclass's save, too (integrated-server host NBT).
                    saveOverrides.add(player);
                    super.writePlayerData(player);
                }
            });
            network = new NetworkSystem(server);
            assertTrue(network instanceof LoginSessionIndex.Provider, "Production mixins must be applied");
            setField(NetworkSystem.class, network, "networkManagers", managers);
            when(server.getConfigurationManager()).thenReturn(scm);
            when(server.func_147137_ag()).thenReturn(network);
            serverLookup = mockStatic(MinecraftServer.class);
            serverLookup.when(MinecraftServer::getServer).thenReturn(server);
            handshake = mockStatic(FMLNetworkHandler.class);
            // Player construction/world loading and the remote FML client are outside this fixture.
            doAnswer(call -> {
                sequence.add("replacement created");
                return mock(EntityPlayerMP.class);
            }).when(scm).createPlayerForUser(any());

            world = mock(WorldServer.class);
            setField(World.class, world, "provider", mock(WorldProvider.class));
            world.playerEntities = new ArrayList<>();
            world.theChunkProviderServer = mock(ChunkProviderServer.class);
            when(world.getPlayerManager()).thenReturn(mock(PlayerManager.class));
            when(server.worldServerForDimension(0)).thenReturn(world);
            doCallRealMethod().when(world).removeEntity(any());
            IPlayerFileData playerData = mock(IPlayerFileData.class);
            doAnswer(call -> {
                EntityPlayerMP player = call.getArgument(0);
                ItemStack stack = player.inventory.getStackInSlot(0);
                savedInventory.add(stack == null ? 0 : stack.stackSize);
                sequence.add("save");
                return null;
            }).when(playerData).writePlayerData(any());
            setField(ServerConfigurationManager.class, scm, "playerNBTManagerObj", playerData);
        }

        public void tearDown() {
            channels.forEach(EmbeddedChannel::finish);
            if (fmlBus != null) fmlBus.unregister(this);
            MinecraftForge.EVENT_BUS.unregister(this);
            if (handshake != null) handshake.close();
            if (serverLookup != null) serverLookup.close();
            if (fmlLookup != null) fmlLookup.close();
            if (loaderLookup != null) loaderLookup.close();
        }

        @SubscribeEvent
        public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
            sequence.add("logout event");
        }

        @SubscribeEvent
        public void onToss(ItemTossEvent event) {
            sequence.add("drop " + event.entityItem.getEntityItem().stackSize);
        }

        @SubscribeEvent
        public void onNetworkDisconnect(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
            disconnectPosts++;
            throw new IllegalStateException("disconnect listener failed");
        }

        public void testDeniedDuplicateDoesNotKickPreviousSession() throws Exception {
            EntityPlayerMP previous = player(true);
            NetworkManager previousManager = previous.playerNetServerHandler.func_147362_b();
            doReturn("Denied by admission check").when(scm).allowUserToConnect(any(), any());
            NetworkManager replacement = login();
            network.networkTick();
            network.networkTick();

            assertFalse(replacement.isChannelOpen());
            assertEquals("Denied by admission check", replacement.getExitMessage().getUnformattedText());
            assertNull(LoginSessionState.getAcceptedUuid(replacement));
            assertTrue(previousManager.isChannelOpen());
            assertFalse(LoginSessionState.isSuperseded(previousManager));
            assertTrue(scm.playerEntityList.contains(previous));
            verify(previous.playerNetServerHandler, never()).kickPlayerFromServer(any());
            assertTrue(sequence.isEmpty(), "A denied login must not create a player or log out the existing one");
            assertTrue(saveOverrides.isEmpty());
        }

        public void testReconnectWaitsForRealSaveAndCursorAndCraftingCleanup() throws Exception {
            EntityPlayerMP previous = player(true);
            previous.inventory.setInventorySlotContents(0, new ItemStack(Items.diamond, 64));
            previous.inventory.setItemStack(new ItemStack(Items.diamond, 2));
            ContainerWorkbench workbench = new ContainerWorkbench(previous.inventory, world, 0, 0, 0) {

                @Override
                public void onCraftMatrixChanged(IInventory inventory) {
                    // Recipe discovery is unrelated to closing the real crafting inventory.
                }
            };
            workbench.craftMatrix.setInventorySlotContents(0, new ItemStack(Items.diamond, 3));
            previous.openContainer = workbench;
            NetworkManager replacement = login();

            network.networkTick();
            assertFalse(previous.playerNetServerHandler.func_147362_b().isChannelOpen());
            assertNull(LoginSessionState.getAcceptedUuid(replacement));
            assertTrue(savedInventory.isEmpty());
            verify(scm, never()).createPlayerForUser(any());

            network.networkTick();
            assertEquals(Arrays.asList("logout event", "save", "drop 2", "drop 3", "replacement created"), sequence);
            assertEquals(Arrays.asList(64), savedInventory);
            assertTrue(previous.isDead);
            assertNull(previous.inventory.getItemStack());
            assertNull(workbench.craftMatrix.getStackInSlot(0));
            assertFalse(scm.playerEntityList.contains(previous));
            assertFalse(world.playerEntities.contains(previous));
            assertEquals(PLAYER_UUID, LoginSessionState.getAcceptedUuid(replacement));
        }

        public void testEarlyModLogoutThenQueuedDropGetsFinalSaveBeforeReconnect() throws Exception {
            EntityPlayerMP previous = player(true);
            previous.inventory.setInventorySlotContents(0, new ItemStack(Items.diamond, 64));
            NetworkManager previousManager = previous.playerNetServerHandler.func_147362_b();
            ((EmbeddedChannel) previousManager.channel()).writeInbound(new C07PacketPlayerDigging(3, 0, 0, 0, 0));

            // ServerUtilities' AFK cleanup calls onDisconnect without closing the still-serviced connection.
            previous.playerNetServerHandler.onDisconnect(new ChatComponentText("AFK"));
            assertEquals(Arrays.asList(64), savedInventory);
            assertTrue(previousManager.isChannelOpen());
            assertTrue(scm.playerEntityList.isEmpty());

            NetworkManager replacement = login();
            network.networkTick();
            assertNull(previous.inventory.getStackInSlot(0));
            assertTrue(sequence.contains("drop 64"), "The queued packet must really drop the inventory stack");
            assertNull(LoginSessionState.getAcceptedUuid(replacement));
            for (int i = 0; i < 5; i++) network.networkTick();
            assertFalse(previousManager.isChannelOpen());
            assertEquals(Arrays.asList(64), savedInventory);
            assertNull(LoginSessionState.getAcceptedUuid(replacement));

            network.networkTick();
            assertEquals(Arrays.asList(64, 0), savedInventory);
            assertEquals(PLAYER_UUID, LoginSessionState.getAcceptedUuid(replacement));
            verify(previous.playerNetServerHandler, times(2)).onDisconnect(any());
        }

        public void testUnfinishedHandshakeClosesWithoutSavingOrClosingContainers() throws Exception {
            EntityPlayerMP arriving = player(false);
            NetworkManager manager = arriving.playerNetServerHandler.func_147362_b();
            // FML installs the play handler before the player enters the world, and clears this field meanwhile.
            arriving.playerNetServerHandler = null;
            NetworkManager replacement = login();
            for (int i = 0; i < 5; i++) {
                network.networkTick();
                assertTrue(manager.isChannelOpen());
                assertNull(LoginSessionState.getAcceptedUuid(replacement));
            }
            network.networkTick();
            assertFalse(manager.isChannelOpen());
            assertTrue(LoginSessionState.isPreWorldClose(manager));
            assertNull(LoginSessionState.getAcceptedUuid(replacement));
            network.networkTick();
            assertTrue(savedInventory.isEmpty());
            assertTrue(saveOverrides.isEmpty());
            assertFalse(arriving.isDead);
            assertEquals(PLAYER_UUID, LoginSessionState.getAcceptedUuid(replacement));
            assertEquals(0, server.getTickCounter(), "Network deadlines also work while world ticking is paused");
        }

        public void testSupersededHandshakeClosingEarlySkipsLogout() throws Exception {
            EntityPlayerMP arriving = player(false);
            NetworkManager manager = arriving.playerNetServerHandler.func_147362_b();
            arriving.playerNetServerHandler = null;
            when(server.isSinglePlayer()).thenReturn(true);
            when(server.getServerOwner()).thenReturn("test-player");
            NetworkManager replacement = login();
            network.networkTick();
            assertTrue(manager.isChannelOpen());
            assertNull(LoginSessionState.getAcceptedUuid(replacement));

            // The client can cancel before the barrier's five-tick forced close.
            manager.closeChannel(new ChatComponentText("Client cancelled handshake"));
            network.networkTick();
            assertEquals(Arrays.asList("replacement created"), sequence);
            assertTrue(savedInventory.isEmpty());
            assertTrue(saveOverrides.isEmpty(), "Never enter the integrated-server host NBT save override");
            assertFalse(arriving.isDead);
            verify(server, never()).initiateShutdown();
            assertEquals(PLAYER_UUID, LoginSessionState.getAcceptedUuid(replacement));
        }

        public void testWaitersShareGraceWhenHandshakeCompletesBeforeForcedClose() throws Exception {
            EntityPlayerMP arriving = player(false);
            NetHandlerPlayServer handler = arriving.playerNetServerHandler;
            NetworkManager manager = handler.func_147362_b();
            arriving.playerNetServerHandler = null;
            NetworkManager first = login();
            NetworkManager second = login();
            for (int i = 0; i < 4; i++) network.networkTick();
            assertTrue(manager.isChannelOpen());
            assertTrue(LoginSessionState.isPreWorldClose(manager));
            verify(handler, never()).kickPlayerFromServer(any());

            // FML's main-thread completion packet may install the player before the forced-close deadline.
            arriving.playerNetServerHandler = handler;
            scm.playerLoggedIn(arriving);
            network.networkTick();
            verify(handler, times(1)).kickPlayerFromServer(any());
            assertFalse(manager.isChannelOpen());
            assertFalse(LoginSessionState.isPreWorldClose(manager));
            assertNull(LoginSessionState.getAcceptedUuid(first));
            assertNull(LoginSessionState.getAcceptedUuid(second));
            network.networkTick();
            assertEquals(Arrays.asList(arriving), saveOverrides);
            assertEquals(PLAYER_UUID, LoginSessionState.getAcceptedUuid(first));
            assertNull(
                    LoginSessionState.getAcceptedUuid(second),
                    "The first waiter reserves the UUID in the same tick");
        }

        public void testFailedTransportCloseTimesOutAllWaitersWithoutSaving() throws Exception {
            EntityPlayerMP previous = player(true);
            NetworkManager manager = previous.playerNetServerHandler.func_147362_b();
            int[] closeRequests = { 0 };
            // A broken pipeline can hold both the kick's write promise and close itself. Ordinary channels do close.
            ChannelOutboundHandlerAdapter stalled = new ChannelOutboundHandlerAdapter() {

                @Override
                public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) {
                    if (!(packet instanceof S40PacketDisconnect)) context.write(packet, promise);
                }

                @Override
                public void close(ChannelHandlerContext context, ChannelPromise promise) {
                    closeRequests[0]++;
                }
            };
            manager.channel().pipeline().addFirst(stalled);
            try {
                NetworkManager first = login();
                NetworkManager second = login();
                for (int i = 0; i < 60; i++) {
                    network.networkTick();
                    assertTrue(first.isChannelOpen());
                    assertTrue(second.isChannelOpen());
                }
                network.networkTick();
                assertFalse(first.isChannelOpen());
                assertFalse(second.isChannelOpen());
                assertTrue(manager.isChannelOpen());
                assertEquals(1, closeRequests[0]);
                verify(previous.playerNetServerHandler, times(1)).kickPlayerFromServer(any());
                assertTrue(savedInventory.isEmpty());
                verify(scm, never()).createPlayerForUser(any());
            } finally {
                manager.channel().pipeline().remove(stalled);
            }
        }

        public void testAmbiguousStrandedSaveIsBlockedBeforeSubclassAndLiveLogoutStillSaves() throws Exception {
            EntityPlayerMP stranded = player(true);
            managers.remove(stranded.playerNetServerHandler.func_147362_b());
            EntityPlayerMP live = player(true);
            live.inventory.setInventorySlotContents(0, new ItemStack(Items.diamond, 32));
            NetworkManager replacement = login();
            network.networkTick();
            scm.saveAllPlayerData();
            assertEquals(Arrays.asList(live), saveOverrides);
            assertEquals(Arrays.asList(32), savedInventory);
            network.networkTick();
            assertEquals(Arrays.asList(live, live), saveOverrides);
            assertEquals(Arrays.asList(32, 32), savedInventory);

            // Even an explicit mod logout must not let the stranded copy overwrite the live player's save.
            stranded.playerNetServerHandler.onDisconnect(new ChatComponentText("mod cleanup"));
            assertTrue(stranded.isDead);
            assertEquals(Arrays.asList(live, live), saveOverrides);
            network.networkTick();
            assertEquals(PLAYER_UUID, LoginSessionState.getAcceptedUuid(replacement));
        }

        public void testClosePostedBeforeSupersessionIsNotPostedAgain() throws Exception {
            NetworkManager manager = manager();
            NetworkDispatcher dispatcher = new NetworkDispatcher(manager, scm);
            ChannelHandlerContext context = manager.channel().pipeline().context(manager);
            assertThrows(IllegalStateException.class, () -> dispatcher.close(context, manager.channel().newPromise()));
            assertTrue(manager.isChannelOpen());
            assertEquals(1, disconnectPosts);

            LoginSessionState.markSuperseded(manager, 0);
            ChannelPromise closed = manager.channel().newPromise();
            dispatcher.close(context, closed);
            assertTrue(closed.isSuccess());
            assertFalse(manager.isChannelOpen());
            assertEquals(1, disconnectPosts);
        }

        public void testSupersededCloseFinishesEvenWhenDisconnectListenerThrows() throws Exception {
            NetworkManager manager = manager();
            NetworkDispatcher dispatcher = new NetworkDispatcher(manager, scm);
            LoginSessionState.markSuperseded(manager, 0);
            ChannelPromise closed = manager.channel().newPromise();
            dispatcher.close(manager.channel().pipeline().context(manager), closed);
            assertTrue(closed.isSuccess());
            assertFalse(manager.isChannelOpen());
            assertEquals(1, disconnectPosts);
        }

        private EntityPlayerMP player(boolean installed) throws Exception {
            EntityPlayerMP player = mock(EntityPlayerMP.class);
            when(player.getUniqueID()).thenReturn(PLAYER_UUID);
            when(player.getCommandSenderName()).thenReturn("test-player");
            when(player.getServerForPlayer()).thenReturn(world);
            player.worldObj = world;
            player.inventory = new InventoryPlayer(player);
            player.capturedDrops = new ArrayList<>();
            setField(Entity.class, player, "rand", new Random(0));
            player.inventoryContainer = new Container() {

                @Override
                public boolean canInteractWith(EntityPlayer ignored) {
                    return true;
                }
            };
            player.openContainer = player.inventoryContainer;
            doCallRealMethod().when(player).setDead();
            doCallRealMethod().when(player).dropOneItem(anyBoolean());
            doCallRealMethod().when(player).dropPlayerItemWithRandomChoice(any(), anyBoolean());
            doCallRealMethod().when(player).func_146097_a(any(), anyBoolean(), anyBoolean());
            doCallRealMethod().when(player).joinEntityItemWithWorld(any());
            NetworkManager manager = manager();
            manager.setConnectionState(EnumConnectionState.PLAY);
            NetHandlerPlayServer handler = spy(new NetHandlerPlayServer(server, manager, player));
            manager.setNetHandler(handler);
            player.playerNetServerHandler = handler;
            // Keep-alives/world updates need a running server; packet processing and disconnect remain real.
            doNothing().when(handler).onNetworkTick();
            LoginSessionState.setAcceptedUuid(manager, PLAYER_UUID);
            if (installed) {
                world.playerEntities.add(player);
                scm.playerLoggedIn(player);
            }
            return player;
        }

        private NetworkManager login() {
            NetworkManager manager = manager();
            manager.setConnectionState(EnumConnectionState.LOGIN);
            NetHandlerLoginServer handler = new NetHandlerLoginServer(server, manager);
            manager.setNetHandler(handler);
            ((EmbeddedChannel) manager.channel())
                    .writeInbound(new C00PacketLoginStart(new GameProfile(PLAYER_UUID, "test-player")));
            return manager;
        }

        private NetworkManager manager() {
            NetworkManager manager = new NetworkManager(false);
            EmbeddedChannel channel = new EmbeddedChannel(manager);
            channels.add(channel);
            managers.add(manager);
            return manager;
        }

        private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        }
    }
}
