package hodgepodge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.storage.IPlayerFileData;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mitchej123.hodgepodge.mixins.early.fml.MixinNetworkDispatcher_LoginSessionState;
import com.mitchej123.hodgepodge.mixins.early.minecraft.MixinNetHandlerLoginServer_AwaitPreviousSession;
import com.mitchej123.hodgepodge.mixins.early.minecraft.MixinNetHandlerPlayServer_PreWorldDisconnect;
import com.mitchej123.hodgepodge.mixins.early.minecraft.MixinNetworkSystem_LoginSessionIndex;
import com.mitchej123.hodgepodge.mixins.early.minecraft.MixinServerConfigurationManager_LoginSessionSave;
import com.mitchej123.hodgepodge.mixins.interfaces.LoginSessionState;
import com.mitchej123.hodgepodge.util.LoginSessionIndex;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventBus;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;

class LoginSessionBarrierTest {

    private static final UUID PLAYER_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final CountingList<NetworkManager> managers = new CountingList<>();
    private final CountingList<EntityPlayerMP> players = new CountingList<>();
    private final List<Channel> channels = new ArrayList<>();
    private final List<EntityPlayerMP> savedPlayers = new ArrayList<>();
    private ServerConfigurationManager scm;
    private MinecraftServer server;
    private MixinNetworkSystem_LoginSessionIndex networkHooks;
    private MixinServerConfigurationManager_LoginSessionSave playerHooks;
    private MockedStatic<MinecraftServer> serverLookup;
    private int tick;
    private boolean paused;

    @BeforeEach
    void setUp() throws Exception {
        server = mock(MinecraftServer.class);
        scm = new TestPlayerList(server);
        setField(ServerConfigurationManager.class, scm, "playerEntityList", players);
        IPlayerFileData playerData = mock(IPlayerFileData.class);
        doAnswer(call -> {
            savedPlayers.add(call.getArgument(0));
            return null;
        }).when(playerData).writePlayerData(any());
        Field saveHandler = ServerConfigurationManager.class.getDeclaredField("playerNBTManagerObj");
        saveHandler.setAccessible(true);
        saveHandler.set(scm, playerData);
        networkHooks = new MixinNetworkSystem_LoginSessionIndex();
        setField(MixinNetworkSystem_LoginSessionIndex.class, networkHooks, "mcServer", server);
        setField(MixinNetworkSystem_LoginSessionIndex.class, networkHooks, "networkManagers", managers);
        playerHooks = new MixinServerConfigurationManager_LoginSessionSave();
        setField(MixinServerConfigurationManager_LoginSessionSave.class, playerHooks, "mcServer", server);
        NetworkSystem network = mock(
                NetworkSystem.class,
                withSettings().extraInterfaces(LoginSessionIndex.Provider.class));
        when(((LoginSessionIndex.Provider) network).hodgepodge$getLoginSessionIndex())
                .thenAnswer(call -> networkHooks.hodgepodge$getLoginSessionIndex());
        when(server.getConfigurationManager()).thenReturn(scm);
        when(server.func_147137_ag()).thenReturn(network);
        when(server.getTickCounter()).thenAnswer(call -> tick);
        serverLookup = mockStatic(MinecraftServer.class);
        serverLookup.when(MinecraftServer::getServer).thenReturn(server);
    }

    @AfterEach
    void tearDown() {
        if (serverLookup != null) {
            serverLookup.close();
        }
        channels.forEach(Channel::close);
    }

    @Test
    void ordinaryReconnectKicksOnceAndWaitsForSaveAndRemoval() throws Exception {
        EntityPlayerMP previous = player(true, true);
        TestLogin login = login();
        assertFalse(login.poll());
        beginNetworkTick();
        assertFalse(login.poll());
        verify(previous.playerNetServerHandler, times(1)).kickPlayerFromServer(anyString());
        disconnect(previous);
        assertTrue(login.poll());
        assertEquals(Collections.singletonList(previous), savedPlayers);
        assertEquals(PLAYER_UUID, LoginSessionState.getAcceptedUuid(login.field_147333_a));
    }

    @Test
    void earlyLogoutWithOpenConnectionStillSavesInventoryBeforeReconnect() throws Exception {
        EntityPlayerMP previous = player(true, true);
        NetworkManager manager = previous.playerNetServerHandler.func_147362_b();
        AtomicInteger inventory = new AtomicInteger(64);
        List<Integer> savedInventory = new ArrayList<>();
        IPlayerFileData playerData = mock(IPlayerFileData.class);
        doAnswer(call -> {
            savedInventory.add(inventory.get());
            return null;
        }).when(playerData).writePlayerData(previous);
        setField(ServerConfigurationManager.class, scm, "playerNBTManagerObj", playerData);

        // ServerUtilities' AFK cleanup logs the player out without closing their connection.
        previous.playerNetServerHandler.onDisconnect(new ChatComponentText("AFK"));
        assertEquals(Collections.singletonList(64), savedInventory);
        assertTrue(managers.contains(manager));
        assertTrue(players.isEmpty());
        beginNetworkTick();

        TestLogin reconnect = login();
        assertFalse(reconnect.poll());
        // The open connection can still process queued inventory/drop packets after the first save.
        inventory.set(0);
        for (int i = 0; i < 5; i++) {
            beginNetworkTick();
            assertFalse(reconnect.poll());
        }
        verify(manager, times(1)).closeChannel(any());
        disconnect(previous);
        assertTrue(reconnect.poll());
        assertEquals(2, savedInventory.size());
        assertEquals(0, savedInventory.get(1));
    }

    @Test
    void strandedCloneCannotOverwriteLiveSaveEvenOnAnotherLoginAttempt() throws Exception {
        EntityPlayerMP stranded = player(false, true);
        EntityPlayerMP live = player(true, true);
        TestLogin first = login();
        assertFalse(first.poll());
        disconnect(live);
        for (beginNetworkTick(); tick <= 62; beginNetworkTick()) {
            assertFalse(first.poll());
        }
        assertTrue(first.rejected);
        assertTrue(first.reason.contains("server administrator"));
        managers.remove(first.field_147333_a);
        TestLogin retry = login();
        for (int i = 0; i <= 61; i++, beginNetworkTick()) {
            assertFalse(retry.poll());
        }
        assertTrue(retry.rejected);
        assertTrue(retry.reason.contains("server administrator"));
        assertEquals(Collections.singletonList(live), savedPlayers);
        verify(stranded.playerNetServerHandler, never()).onDisconnect(any());
    }

    @Test
    void soleStrandedSessionStillRecovers() throws Exception {
        EntityPlayerMP stranded = player(false, true);
        TestLogin login = login();
        for (; tick < 60; beginNetworkTick()) {
            assertFalse(login.poll());
        }
        assertTrue(login.poll());
        assertEquals(Collections.singletonList(stranded), savedPlayers);
        assertFalse(login.rejected);
    }

    @Test
    void multipleStrandedSessionsAreNotSavedInArbitraryOrder() throws Exception {
        player(false, true);
        player(false, true);
        TestLogin login = login();
        for (; tick <= 61; beginNetworkTick()) {
            assertFalse(login.poll());
        }
        assertTrue(login.rejected);
        scm.saveAllPlayerData();
        assertTrue(savedPlayers.isEmpty());
    }

    @Test
    void duplicatePlayerListEntriesStillPreventUnsafeRecovery() throws Exception {
        EntityPlayerMP stranded = player(false, true);
        players.add(stranded);
        TestLogin login = login();
        for (; tick <= 60; beginNetworkTick()) {
            assertFalse(login.poll());
        }
        assertTrue(login.rejected);
        assertTrue(LoginSessionState.isPlayerSaveBlocked(stranded.playerNetServerHandler.func_147362_b()));
        verify(stranded.playerNetServerHandler, never()).onDisconnect(any());
        assertTrue(savedPlayers.isEmpty());
    }

    @Test
    void saveAllSkipsStrandedCloneButKeepsLiveAndDisconnectSaves() throws Exception {
        player(false, true);
        EntityPlayerMP live = player(true, true);
        TestLogin login = login();
        assertFalse(login.poll());
        scm.saveAllPlayerData();
        assertEquals(Collections.singletonList(live), savedPlayers);
        savedPlayers.clear();
        disconnect(live);
        assertEquals(Collections.singletonList(live), savedPlayers);
        savedPlayers.clear();
        scm.saveAllPlayerData();
        assertTrue(savedPlayers.isEmpty());
    }

    @Test
    void waitersShareKickDeadlineAfterHandshakeCompletes() throws Exception {
        EntityPlayerMP arriving = player(true, false);
        NetworkManager previousManager = arriving.playerNetServerHandler.func_147362_b();
        TestLogin first = login();
        TestLogin second = login();
        for (; tick < 4; beginNetworkTick()) {
            assertFalse(first.poll());
            assertFalse(second.poll());
        }
        addPlayer(arriving);
        assertFalse(first.poll());
        assertFalse(second.poll());
        assertFalse(first.rejected);
        assertFalse(second.rejected);
        verify(previousManager, never()).closeChannel(any());
        for (beginNetworkTick(); tick < 9; beginNetworkTick()) {
            assertFalse(first.poll());
            assertFalse(second.poll());
        }
        verify(previousManager, never()).closeChannel(any());
        for (; tick < 64; beginNetworkTick()) {
            assertFalse(first.poll());
            assertFalse(second.poll());
            assertFalse(first.rejected);
            assertFalse(second.rejected);
        }
        assertFalse(first.poll());
        assertFalse(second.poll());
        assertTrue(first.rejected);
        assertTrue(second.rejected);
        verify(arriving.playerNetServerHandler, times(1)).kickPlayerFromServer(anyString());
        verify(previousManager, times(1)).closeChannel(any());
        assertFalse(LoginSessionState.isPreWorldClose(previousManager));
    }

    @Test
    void acceptedHandshakeIsClosedAndReplacementProceedsAfterRemoval() throws Exception {
        NetworkManager arriving = manager();
        managers.add(arriving);
        LoginSessionState.setAcceptedUuid(arriving, PLAYER_UUID);
        TestLogin login = login();
        for (; tick < 5; beginNetworkTick()) {
            assertFalse(login.poll());
        }
        verify(arriving, never()).closeChannel(any());
        assertFalse(login.poll());
        verify(arriving, times(1)).closeChannel(any());
        assertTrue(LoginSessionState.isPreWorldClose(arriving));
        managers.remove(arriving);
        networkHooks.hodgepodge$getLoginSessionIndex().connectionRemoved(arriving);
        assertTrue(login.poll());
        assertFalse(login.rejected);
    }

    @Test
    void pausedNetworkTicksAdvanceSharedDeadlinesAcrossSnapshotResets() throws Exception {
        // ServerUtilities keeps servicing networking while cancelling the tick that advances this counter.
        tick = 1000;
        paused = true;
        EntityPlayerMP arriving = player(true, false);
        NetworkManager manager = arriving.playerNetServerHandler.func_147362_b();
        TestLogin first = login();
        TestLogin second = login();
        for (int elapsed = 0; elapsed <= 60; elapsed++) {
            if (elapsed > 0) {
                beginNetworkTick();
            }
            assertFalse(first.poll());
            assertFalse(second.poll());
            assertEquals(elapsed, networkHooks.hodgepodge$getLoginSessionIndex().getSessions(PLAYER_UUID).waited(0));
            verify(manager, times(elapsed >= 5 ? 1 : 0)).closeChannel(any());
            assertEquals(elapsed == 60, first.rejected);
            assertEquals(elapsed == 60, second.rejected);
        }
        assertEquals(1000, server.getTickCounter());
        assertTrue(LoginSessionState.isPreWorldClose(manager));
        assertTrue(savedPlayers.isEmpty());
    }

    @Test
    void resumingServerPreservesKickGraceAndFinalSave() throws Exception {
        tick = 1000;
        paused = true;
        EntityPlayerMP arriving = player(true, false);
        NetworkManager manager = arriving.playerNetServerHandler.func_147362_b();
        TestLogin reconnect = login();
        assertFalse(reconnect.poll());
        // The deadline must also advance through network ticks with no waiter polls or snapshot creation.
        for (int i = 0; i < 4; i++) {
            beginNetworkTick();
        }
        assertFalse(reconnect.poll());
        assertEquals(4, networkHooks.hodgepodge$getLoginSessionIndex().getSessions(PLAYER_UUID).waited(0));
        verify(manager, never()).closeChannel(any());

        // Installing the player resumes world ticking and gives the ordinary kick its own full grace period.
        addPlayer(arriving);
        paused = false;
        beginNetworkTick();
        assertFalse(reconnect.poll());
        for (int i = 0; i < 4; i++) {
            beginNetworkTick();
            assertFalse(reconnect.poll());
        }
        verify(manager, never()).closeChannel(any());
        beginNetworkTick();
        assertFalse(reconnect.poll());
        verify(manager, times(1)).closeChannel(any());
        verify(arriving.playerNetServerHandler, times(1)).kickPlayerFromServer(anyString());
        assertFalse(reconnect.rejected);
        assertFalse(LoginSessionState.isPreWorldClose(manager));
        assertTrue(savedPlayers.isEmpty());
        disconnect(arriving);
        assertTrue(reconnect.poll());
        assertEquals(Collections.singletonList(arriving), savedPlayers);
        assertEquals(1006, server.getTickCounter());
    }

    @Test
    void supersessionRetainsEarlierDisconnectPostAndOriginalDeadline() {
        NetworkManager manager = manager();
        assertFalse(LoginSessionState.isSuperseded(manager));
        assertTrue(LoginSessionState.markDisconnectPosted(manager));
        assertTrue(LoginSessionState.markSuperseded(manager, 0));
        assertTrue(LoginSessionState.isSuperseded(manager));
        assertFalse(LoginSessionState.markSuperseded(manager, 60));
        assertEquals(0, LoginSessionState.getSupersededTick(manager));
        assertTrue(LoginSessionState.markKicked(manager, 60));
        assertFalse(LoginSessionState.markKicked(manager, 61));
        assertEquals(60, LoginSessionState.getKickedTick(manager));
        assertFalse(LoginSessionState.markDisconnectPosted(manager));
    }

    @Test
    void saveBlockReportsOnlyItsFirstTransition() {
        NetworkManager manager = manager();
        assertTrue(LoginSessionState.blockPlayerSave(manager));
        assertFalse(LoginSessionState.blockPlayerSave(manager));
        assertTrue(LoginSessionState.isPlayerSaveBlocked(manager));
    }

    @Test
    void closePostedBeforeSupersessionIsNotPostedAgain() throws Exception {
        NetworkManager manager = manager();
        MixinNetworkDispatcher_LoginSessionState mixin = new MixinNetworkDispatcher_LoginSessionState() {};
        mixin.manager = manager;
        Method hook = MixinNetworkDispatcher_LoginSessionState.class
                .getDeclaredMethod("hodgepodge$finishSupersededClose", EventBus.class, Event.class, Operation.class);
        hook.setAccessible(true);
        AtomicInteger posts = new AtomicInteger();
        Operation<Boolean> post = args -> {
            posts.incrementAndGet();
            throw new IllegalStateException("disconnect listener failed");
        };

        InvocationTargetException failure = assertThrows(
                InvocationTargetException.class,
                () -> hook.invoke(mixin, mock(EventBus.class), mock(Event.class), post));
        assertTrue(failure.getCause() instanceof IllegalStateException);
        assertEquals(1, posts.get());

        LoginSessionState.markSuperseded(manager, tick);
        assertFalse((boolean) hook.invoke(mixin, mock(EventBus.class), mock(Event.class), post));
        assertEquals(1, posts.get());
    }

    @Test
    void forcedHandshakeCloseSkipsLogoutOnlyUntilPlayerEntersWorld() throws Exception {
        EntityPlayerMP arriving = player(true, false);
        NetHandlerPlayServer handler = arriving.playerNetServerHandler;
        NetworkManager manager = handler.func_147362_b();
        arriving.playerNetServerHandler = null;
        TestLogin login = login();
        for (; tick <= 5; beginNetworkTick()) {
            assertFalse(login.poll());
        }
        verify(manager, times(1)).closeChannel(any());
        assertTrue(preWorldLogoutCancelled(manager, arriving));

        arriving.playerNetServerHandler = handler;
        addPlayer(arriving);
        assertFalse(preWorldLogoutCancelled(manager, arriving));

        // Installation can win the race with the asynchronous close, followed by an early mod logout.
        removePlayer(arriving);
        beginNetworkTick();
        assertFalse(preWorldLogoutCancelled(manager, arriving));
        disconnect(arriving);
        assertTrue(login.poll());
        assertEquals(Collections.singletonList(arriving), savedPlayers);
    }

    @Test
    void sameTickAdmissionsReserveUuidWithoutAnotherFullScan() throws Exception {
        List<TestLogin> arrivals = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            arrivals.add(login(new UUID(0, i)));
        }
        TestLogin first = login();
        TestLogin second = login();
        for (TestLogin arrival : arrivals) {
            assertTrue(arrival.poll());
        }
        assertTrue(first.poll());
        assertFalse(second.poll());
        verify(first.field_147333_a, never()).closeChannel(any());
        assertEquals(managers.size(), managers.visits);
        assertEquals(0, managers.membershipChecks);
    }

    @Test
    void idleTickDiscardsOldSnapshotWithoutScanningAgain() throws Exception {
        assertTrue(login().poll());
        LoginSessionIndex previous = networkHooks.hodgepodge$getLoginSessionIndex();
        beginNetworkTick();
        assertNotSame(previous, networkHooks.hodgepodge$getLoginSessionIndex());
        assertEquals(managers.size(), managers.visits);
        assertEquals(0, players.visits);
    }

    @Test
    void manyWaitersShareOneScanAndOneBlockerPassPerTick() throws Exception {
        List<NetworkManager> blockers = new ArrayList<>();
        List<TestLogin> waiters = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            blockers.add(player(true, true).playerNetServerHandler.func_147362_b());
            waiters.add(login());
        }
        for (TestLogin waiter : waiters) {
            assertFalse(waiter.poll());
        }
        assertEquals(managers.size(), managers.visits);
        assertEquals(players.size(), players.visits);
        // Once to index, once to classify, once to kick; later waiters reuse the prepared bucket.
        for (NetworkManager manager : blockers) {
            verify(manager, times(3)).getNetHandler();
        }
        beginNetworkTick();
        for (TestLogin waiter : waiters) {
            assertFalse(waiter.poll());
        }
        assertEquals(2 * managers.size(), managers.visits);
        assertEquals(2 * players.size(), players.visits);
        assertEquals(0, managers.membershipChecks);
        assertEquals(0, players.membershipChecks);
        for (NetworkManager manager : blockers) {
            verify(manager, times(5)).getNetHandler();
        }
    }

    @Test
    void sameTickHandshakeCompletionInvalidatesWaitersDeadline() throws Exception {
        EntityPlayerMP arriving = player(true, false);
        TestLogin first = login();
        TestLogin second = login();
        for (; tick < 59; beginNetworkTick()) {
            assertFalse(first.poll());
            assertFalse(second.poll());
        }
        assertFalse(first.poll());
        addPlayer(arriving);
        assertFalse(second.poll());
        assertFalse(first.poll());
        assertFalse(first.rejected);
        assertFalse(second.rejected);
        verify(arriving.playerNetServerHandler, times(1)).kickPlayerFromServer(anyString());
        verify(arriving.playerNetServerHandler.func_147362_b(), times(1)).closeChannel(any());
        assertEquals(0, LoginSessionState.getSupersededTick(arriving.playerNetServerHandler.func_147362_b()));
        assertEquals(tick, LoginSessionState.getKickedTick(arriving.playerNetServerHandler.func_147362_b()));
    }

    @Test
    void respawnReplacementKeepsConnectionReservedWithinTick() throws Exception {
        EntityPlayerMP oldPlayer = player(true, true);
        TestLogin login = login();
        assertFalse(login.poll());
        removePlayer(oldPlayer);
        assertFalse(login.poll());
        EntityPlayerMP replacement = mock(EntityPlayerMP.class);
        when(replacement.getUniqueID()).thenReturn(PLAYER_UUID);
        replacement.playerNetServerHandler = oldPlayer.playerNetServerHandler;
        addPlayer(replacement);
        replacement.playerNetServerHandler.playerEntity = replacement;
        assertFalse(login.poll());
        assertFalse(login.rejected);
        assertEquals(managers.size(), managers.visits);
        verify(oldPlayer.playerNetServerHandler, times(1)).kickPlayerFromServer(anyString());
        assertFalse(LoginSessionState.isPlayerSaveBlocked(replacement.playerNetServerHandler.func_147362_b()));
    }

    @Test
    void failedDisconnectRemainsStrandedAndBlockedWithinTick() throws Exception {
        EntityPlayerMP previous = player(true, true);
        TestLogin login = login();
        assertFalse(login.poll());
        NetworkManager manager = previous.playerNetServerHandler.func_147362_b();
        managers.remove(manager);
        InvocationTargetException failure = assertThrows(
                InvocationTargetException.class,
                () -> {
                    finishDisconnect(manager, () -> { throw new IllegalStateException("logout listener failed"); });
                });
        assertTrue(failure.getCause() instanceof IllegalStateException);
        assertFalse(login.poll());
        assertTrue(networkHooks.hodgepodge$getLoginSessionIndex().getSessions(PLAYER_UUID).hasStranded());
        assertTrue(savedPlayers.isEmpty());
    }

    @Test
    void failedKickDoesNotCacheIncompleteDeadlineForOtherWaiters() throws Exception {
        EntityPlayerMP previous = player(true, true);
        TestLogin first = login();
        TestLogin second = login();
        doThrow(new IllegalStateException("kick failed")).when(previous.playerNetServerHandler)
                .kickPlayerFromServer(anyString());
        InvocationTargetException failure = assertThrows(InvocationTargetException.class, first::poll);
        assertTrue(failure.getCause() instanceof IllegalStateException);
        assertFalse(second.poll());
        assertFalse(second.rejected);
        assertTrue(savedPlayers.isEmpty());
    }

    private NetworkManager manager() {
        NetworkManager manager = mock(NetworkManager.class);
        Channel channel = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        channels.add(channel);
        when(manager.channel()).thenReturn(channel);
        when(manager.isChannelOpen()).thenReturn(true);
        return manager;
    }

    private EntityPlayerMP player(boolean tracked, boolean installed) throws Exception {
        NetworkManager manager = manager();
        EntityPlayerMP player = mock(EntityPlayerMP.class);
        NetHandlerPlayServer handler = mock(NetHandlerPlayServer.class);
        when(player.getUniqueID()).thenReturn(PLAYER_UUID);
        when(player.getCommandSenderName()).thenReturn("test-player");
        player.playerNetServerHandler = handler;
        handler.playerEntity = player;
        when(manager.getNetHandler()).thenReturn(handler);
        when(handler.func_147362_b()).thenReturn(manager);
        doAnswer(call -> {
            ((TestPlayerList) scm).writePlayerData(player);
            removePlayer(player);
            return null;
        }).when(handler).onDisconnect(any());
        if (tracked) {
            managers.add(manager);
        }
        if (installed) {
            addPlayer(player);
        }
        LoginSessionState.setAcceptedUuid(manager, PLAYER_UUID);
        return player;
    }

    private void disconnect(EntityPlayerMP player) throws Exception {
        NetworkManager manager = player.playerNetServerHandler.func_147362_b();
        managers.remove(manager);
        boolean cancelled = preWorldLogoutCancelled(manager, player);
        finishDisconnect(manager, () -> {
            if (!cancelled) {
                player.playerNetServerHandler.onDisconnect(new ChatComponentText("Disconnected"));
            }
        });
    }

    private void beginNetworkTick() throws Exception {
        if (!paused) {
            tick++;
        }
        Method beginTick = MixinNetworkSystem_LoginSessionIndex.class
                .getDeclaredMethod("hodgepodge$discardPreviousTick", CallbackInfo.class);
        beginTick.setAccessible(true);
        beginTick.invoke(networkHooks, new CallbackInfo("networkTick", false));
    }

    private TestLogin login() throws Exception {
        return login(PLAYER_UUID);
    }

    private TestLogin login(UUID uuid) throws Exception {
        TestLogin login = new TestLogin();
        login.field_147333_a = manager();
        Field profile = MixinNetHandlerLoginServer_AwaitPreviousSession.class.getDeclaredField("field_147337_i");
        profile.setAccessible(true);
        profile.set(login, new GameProfile(uuid, "test-player"));
        managers.add(login.field_147333_a);
        return login;
    }

    private void addPlayer(EntityPlayerMP player) throws Exception {
        mutatePlayers("hodgepodge$indexPlayerAdded", player, args -> players.add((EntityPlayerMP) args[1]));
    }

    private void removePlayer(EntityPlayerMP player) throws Exception {
        mutatePlayers("hodgepodge$indexPlayerRemoved", player, args -> players.remove(args[1]));
    }

    private void mutatePlayers(String name, EntityPlayerMP player, Operation<Boolean> operation) throws Exception {
        Method hook = MixinServerConfigurationManager_LoginSessionSave.class
                .getDeclaredMethod(name, List.class, Object.class, Operation.class);
        hook.setAccessible(true);
        hook.invoke(playerHooks, players, player, operation);
    }

    private void finishDisconnect(NetworkManager manager, Runnable cleanup) throws Exception {
        Method hook = MixinNetworkSystem_LoginSessionIndex.class.getDeclaredMethod(
                "hodgepodge$finishDisconnect",
                INetHandler.class,
                IChatComponent.class,
                Operation.class,
                NetworkManager.class);
        hook.setAccessible(true);
        Operation<Void> operation = args -> {
            cleanup.run();
            return null;
        };
        hook.invoke(networkHooks, manager.getNetHandler(), new ChatComponentText("Disconnected"), operation, manager);
    }

    private boolean preWorldLogoutCancelled(NetworkManager manager, EntityPlayerMP player) throws Exception {
        MixinNetHandlerPlayServer_PreWorldDisconnect mixin = new MixinNetHandlerPlayServer_PreWorldDisconnect();
        setField(MixinNetHandlerPlayServer_PreWorldDisconnect.class, mixin, "netManager", manager);
        setField(MixinNetHandlerPlayServer_PreWorldDisconnect.class, mixin, "serverController", server);
        setField(MixinNetHandlerPlayServer_PreWorldDisconnect.class, mixin, "playerEntity", player);
        Method hook = MixinNetHandlerPlayServer_PreWorldDisconnect.class
                .getDeclaredMethod("hodgepodge$skipPreWorldLogout", CallbackInfo.class);
        hook.setAccessible(true);
        CallbackInfo ci = new CallbackInfo("onDisconnect", true);
        hook.invoke(mixin, ci);
        return ci.isCancelled();
    }

    private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static class CountingList<E> extends ArrayList<E> {

        private int visits;
        private int membershipChecks;

        @Override
        public boolean contains(Object value) {
            membershipChecks++;
            return super.contains(value);
        }

        @Override
        public Iterator<E> iterator() {
            Iterator<E> delegate = super.iterator();
            return new Iterator<E>() {

                @Override
                public boolean hasNext() {
                    return delegate.hasNext();
                }

                @Override
                public E next() {
                    visits++;
                    return delegate.next();
                }
            };
        }
    }

    private static class TestLogin extends MixinNetHandlerLoginServer_AwaitPreviousSession {

        private boolean rejected;
        private String reason;

        @Override
        public void func_147322_a(String reason) {
            rejected = true;
            this.reason = reason;
        }

        private boolean poll() throws Exception {
            Method barrier = MixinNetHandlerLoginServer_AwaitPreviousSession.class
                    .getDeclaredMethod("hodgepodge$awaitPreviousSession", CallbackInfo.class);
            barrier.setAccessible(true);
            CallbackInfo ci = new CallbackInfo("func_147326_c", true);
            barrier.invoke(this, ci);
            return !ci.isCancelled();
        }
    }

    private static class TestPlayerList extends ServerConfigurationManager {

        private TestPlayerList(MinecraftServer server) {
            super(server);
        }

        @Override
        public void writePlayerData(EntityPlayerMP player) {
            // Exercise the production condition and vanilla save implementation without booting a Mixin launcher.
            try {
                Method guard = MixinServerConfigurationManager_LoginSessionSave.class.getDeclaredMethod(
                        "hodgepodge$allowPlayerSave",
                        ServerConfigurationManager.class,
                        EntityPlayerMP.class);
                guard.setAccessible(true);
                if ((boolean) guard.invoke(new MixinServerConfigurationManager_LoginSessionSave(), this, player)) {
                    super.writePlayerData(player);
                }
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
        }
    }
}
