package com.mitchej123.hodgepodge;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;

import ic2.core.Ic2Items;
import ic2.core.block.EntityItnt;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class Common {

    public static final Logger log = LogManager.getLogger("Hodgepodge");
    public static final Marker securityMarker = MarkerManager.getMarker("SuspiciousPackets");
    public static XSTR RNG = new XSTR();

    public static void init() {
        if (Compat.isIC2Present()) {
            ic2DispenserBehavior();
        }
        if (FixesConfig.shutdownGracefullyOnSignal) {
            registerSignalHandler();
        }
    }

    private static void ic2DispenserBehavior() {
        if (TweaksConfig.ic2DispenserITNT) {
            BlockDispenser.dispenseBehaviorRegistry
                    .putObject(Ic2Items.industrialTnt.getItem(), new BehaviorDefaultDispenseItem() {

                        @Override
                        protected ItemStack dispenseStack(IBlockSource dispenser, ItemStack dispensedItem) {
                            EnumFacing enumfacing = BlockDispenser.func_149937_b(dispenser.getBlockMetadata());
                            World world = dispenser.getWorld();
                            int x = dispenser.getXInt() + enumfacing.getFrontOffsetX();
                            int y = dispenser.getYInt() + enumfacing.getFrontOffsetY();
                            int z = dispenser.getZInt() + enumfacing.getFrontOffsetZ();

                            EntityItnt itnt = new EntityItnt(world, x + 0.5F, y + 0.5F, z + 0.5F);
                            world.spawnEntityInWorld(itnt);
                            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "game.tnt.primed", 1.0F, 1.0F);

                            dispensedItem.stackSize--;
                            return dispensedItem;
                        }
                    });
        }
    }

    private static void registerSignalHandler() {
        SignalHandler signalHandler = sig -> {
            log.info("Received {} signal", sig.getName());
            MinecraftServer server = MinecraftServer.getServer();
            if (server != null) {
                if (server.isServerRunning()) {
                    server.initiateShutdown();
                    long start = System.currentTimeMillis();
                    while (!server.isServerStopped()) {
                        try {
                            if (System.currentTimeMillis() - start > 30_000) {
                                break;
                            }
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
        };
        Signal.handle(new Signal("INT"), signalHandler);
        Signal.handle(new Signal("TERM"), signalHandler);
    }

}
