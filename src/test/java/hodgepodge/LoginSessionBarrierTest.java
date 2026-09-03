package hodgepodge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.early.minecraft.MixinNetHandlerLoginServer_AwaitPreviousSession;
import com.mitchej123.hodgepodge.mixins.early.minecraft.NetworkSystemAccessor;
import com.mitchej123.hodgepodge.mixins.interfaces.LoginSessionState;
import com.mojang.authlib.GameProfile;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;

class LoginSessionBarrierTest {

    private static final UUID PLAYER_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final List<NetworkManager> managers = new ArrayList<>();
    private final List<Channel> channels = new ArrayList<>();
    private final List<EntityPlayerMP> savedPlayers = new ArrayList<>();
    private ServerConfigurationManager scm;
    private MockedStatic<MinecraftServer> serverLookup;
    private int tick;

    @BeforeEach
    void setUp() throws Exception {
        MinecraftServer server = mock(MinecraftServer.class);
        scm = mock(ServerConfigurationManager.class);
        Field players = ServerConfigurationManager.class.getField("playerEntityList");
        players.setAccessible(true);
        players.set(scm, new ArrayList<EntityPlayerMP>());
        NetworkSystem network = mock(NetworkSystem.class, withSettings().extraInterfaces(NetworkSystemAccessor.class));
        when(((NetworkSystemAccessor) network).hodgepodge$getNetworkManagers()).thenAnswer(call -> managers);
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
        tick++;
        assertFalse(login.poll());
        verify(previous.playerNetServerHandler, times(1)).kickPlayerFromServer(anyString());
        disconnect(previous);
        assertTrue(login.poll());
        assertEquals(Collections.singletonList(previous), savedPlayers);
        assertEquals(PLAYER_UUID, LoginSessionState.getAcceptedUuid(login.field_147333_a));
    }

    @Test
    void strandedCloneCannotOverwriteLiveSaveEvenOnAnotherLoginAttempt() throws Exception {
        EntityPlayerMP stranded = player(false, true);
        EntityPlayerMP live = player(true, true);
        TestLogin first = login();
        assertFalse(first.poll());
        disconnect(live);
        for (tick = 1; tick <= 62; tick++) {
            assertFalse(first.poll());
        }
        assertTrue(first.rejected);
        managers.remove(first.field_147333_a);
        TestLogin retry = login();
        for (int i = 0; i <= 61; i++, tick++) {
            assertFalse(retry.poll());
        }
        assertTrue(retry.rejected);
        assertEquals(Collections.singletonList(live), savedPlayers);
        verify(stranded.playerNetServerHandler, never()).onDisconnect(any());
    }

    @Test
    void soleStrandedSessionStillRecovers() throws Exception {
        EntityPlayerMP stranded = player(false, true);
        TestLogin login = login();
        for (tick = 0; tick < 60; tick++) {
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
        for (tick = 0; tick <= 61; tick++) {
            assertFalse(login.poll());
        }
        assertTrue(login.rejected);
        assertTrue(savedPlayers.isEmpty());
    }

    @Test
    void waitersShareKickDeadlineAfterHandshakeCompletes() throws Exception {
        EntityPlayerMP arriving = player(true, false);
        NetworkManager previousManager = arriving.playerNetServerHandler.func_147362_b();
        TestLogin first = login();
        TestLogin second = login();
        for (tick = 0; tick < 60; tick++) {
            assertFalse(first.poll());
            assertFalse(second.poll());
        }
        scm.playerEntityList.add(arriving);
        assertFalse(first.poll());
        assertFalse(second.poll());
        assertFalse(first.rejected);
        assertFalse(second.rejected);
        verify(previousManager, never()).closeChannel(any());
        for (tick = 61; tick < 65; tick++) {
            assertFalse(first.poll());
            assertFalse(second.poll());
        }
        verify(previousManager, never()).closeChannel(any());
        for (; tick < 120; tick++) {
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
    }

    @Test
    void acceptedHandshakeWithoutPlayHandlerRemainsProtectedAndWaitIsBounded() throws Exception {
        NetworkManager arriving = manager();
        managers.add(arriving);
        LoginSessionState.setAcceptedUuid(arriving, PLAYER_UUID);
        TestLogin login = login();
        for (tick = 0; tick <= 61; tick++) {
            assertFalse(login.poll());
        }
        assertTrue(login.rejected);
        verify(arriving, never()).closeChannel(any());
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
        assertFalse(LoginSessionState.markDisconnectPosted(manager));
    }

    private NetworkManager manager() {
        NetworkManager manager = mock(NetworkManager.class);
        Channel channel = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        channels.add(channel);
        when(manager.channel()).thenReturn(channel);
        when(manager.isChannelOpen()).thenReturn(true);
        return manager;
    }

    private EntityPlayerMP player(boolean tracked, boolean installed) {
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
            savedPlayers.add(player);
            scm.playerEntityList.remove(player);
            return null;
        }).when(handler).onDisconnect(any());
        if (tracked) {
            managers.add(manager);
        }
        if (installed) {
            scm.playerEntityList.add(player);
        }
        LoginSessionState.setAcceptedUuid(manager, PLAYER_UUID);
        return player;
    }

    private void disconnect(EntityPlayerMP player) {
        managers.remove(player.playerNetServerHandler.func_147362_b());
        player.playerNetServerHandler.onDisconnect(new ChatComponentText("Disconnected"));
    }

    private TestLogin login() throws Exception {
        TestLogin login = new TestLogin();
        login.field_147333_a = manager();
        Field profile = MixinNetHandlerLoginServer_AwaitPreviousSession.class.getDeclaredField("field_147337_i");
        profile.setAccessible(true);
        profile.set(login, new GameProfile(PLAYER_UUID, "test-player"));
        managers.add(login.field_147333_a);
        return login;
    }

    private static class TestLogin extends MixinNetHandlerLoginServer_AwaitPreviousSession {

        private boolean rejected;

        @Override
        public void func_147322_a(String reason) {
            rejected = true;
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
}
