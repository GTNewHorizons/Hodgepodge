package com.mitchej123.hodgepodge.core.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.Sys;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.common.DimensionManager;

import com.mitchej123.hodgepodge.Hodgepodge;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorWorld;
import mods.railcraft.common.plugins.forge.PlayerPlugin;

public class AnchorAlarm {
    private static final String NBT_KEY = "GT_RC_AnchorAlarmList";
    public static boolean AnchorDebug = false;
    public static void addNewAnchor(EntityLivingBase entityliving, TileEntity te) {
        if (entityliving instanceof EntityPlayerMP) {
            byte[] oldbuf = null;
            if (entityliving.getEntityData().hasKey(NBT_KEY))
                oldbuf = entityliving.getEntityData().getByteArray(NBT_KEY);
            byte[] newbuf = new byte[(oldbuf == null ? 0 : oldbuf.length) + 16];
            ByteBuf newbufwrap = Unpooled.wrappedBuffer(newbuf);
            newbufwrap.setIndex(0, 0);
            if (oldbuf != null)
                newbufwrap.writeBytes(oldbuf);
            saveCoordinatesToPlayer(entityliving, newbufwrap, newbuf, te);
        }
    }
    public static boolean listSavedAnchors(String playerName, World w) {
        for (Object obj : w.playerEntities)
        {
            if (((EntityPlayer)obj).getDisplayName().equals(playerName)) {
                NBTTagCompound nbt = ((EntityPlayer)obj).getEntityData();
                if (!nbt.hasKey(NBT_KEY)){
                    Hodgepodge.log.debug("[AnchorDebug] No anchors listed for player " + playerName);
                } else {
                    byte[] bytes = nbt.getByteArray(NBT_KEY);
                    ByteBuf buf = Unpooled.wrappedBuffer(bytes);
                    int N = bytes.length / 16;
                    for (int i = 0; i < N; ++i) {
                        int dim = buf.readInt();
                        int x = buf.readInt();
                        int y = buf.readInt();
                        int z = buf.readInt();
                        Hodgepodge.log.debug("[AnchorDebug] Anchor (" + x + ", " + y + ", " + z + ") at dim " + dim + " for player " + playerName);
                    }
                }
                return true;
            }
        }
        return false;
    }
    private static void saveCoordinatesToPlayer(EntityLivingBase player, ByteBuf buf, byte[] bytes, TileEntity te) {
        buf.writeInt(te.getWorldObj().provider.dimensionId);
        buf.writeInt(te.xCoord);
        buf.writeInt(te.yCoord);
        buf.writeInt(te.zCoord);
        player.getEntityData().setByteArray(NBT_KEY, bytes);
    }
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            if (AnchorDebug) {
                Hodgepodge.log.debug("[AnchorDebug] Loading anchors for player " + event.player.getDisplayName());
            }
            if (event.player.getEntityData().hasKey(NBT_KEY)) {
                byte[] bytes = event.player.getEntityData().getByteArray(NBT_KEY);
                ByteBuf buf = Unpooled.wrappedBuffer(bytes);
                int N = bytes.length / 16;
                ByteBuf newbuf = Unpooled.buffer(bytes.length);
                int validAlarmCount = 0;
                try {
                    Set<TileEntity> loadedTiles = new HashSet<>();
                    for (int i = 0; i < N; ++i) {
                        int dim = buf.readInt();
                        int x = buf.readInt();
                        int y = buf.readInt();
                        int z = buf.readInt();
                        WorldServer w = DimensionManager.getWorld(dim);
                        if (w == null) {
                            if (AnchorDebug)
                                System.out.println("[AnchorDebug] Loading dimension " + dim);
                            DimensionManager.initDimension(dim);
                            w = DimensionManager.getWorld(dim);
                        }
                        if (w != null) {
                            // if there is some different tile at the place, ok, we will load this chunk one last time
                            w.getChunkProvider().provideChunk(x >> 4, z >> 4);
                            TileEntity t = w.getTileEntity(x, y, z);
                            if (loadedTiles.contains(t))
                                continue;
                            loadedTiles.add(t);
                            if (AnchorDebug)
                                System.out.println("[AnchorDebug] Loading anchor at (" + x + ", " + y + ", " + z + ") at dim " + dim);
                            if (t instanceof TileAnchorWorld) {
                                if (PlayerPlugin.isSamePlayer(((TileAnchorWorld)t).getOwner(), event.player.getGameProfile())) {
                                    // if there is still our tile there, save it for later
                                    ++validAlarmCount;
                                    newbuf.writeInt(dim);
                                    newbuf.writeInt(x);
                                    newbuf.writeInt(y);
                                    newbuf.writeInt(z);
                                }
                                else if (AnchorDebug) {
                                    System.out.println("[AnchorDebug] Someone else\'s anchor at (" + x + ", " + y + ", " + z + ") at dim " + dim);
                                }
                            } else if (AnchorDebug){
                                System.out.println("[AnchorDebug] Failed loading anchor at (" + x + ", " + y + ", " + z + ") at dim " + dim);
                            }
                        }
                        else if (AnchorDebug)
                            System.out.println("[AnchorDebug] Failed loading dimension " + dim);
                    }
                }
                catch (IndexOutOfBoundsException ignored) {
                    Hodgepodge.log.error("Error reading anchor list!");
                }
                byte[] newbytes = new byte[validAlarmCount * 16];
                newbuf.readBytes(newbytes);
                event.player.getEntityData().setByteArray(NBT_KEY, newbytes);
            }
            else {
                if (AnchorDebug)
                    System.out.println("[AnchorDebug] No listed anchors for player " + event.player.getDisplayName());
            }
        }
    }
}
