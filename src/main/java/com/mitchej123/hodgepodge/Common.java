package com.mitchej123.hodgepodge;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.core.HodgepodgeCore;

import ic2.core.Ic2Items;
import ic2.core.block.EntityItnt;

public class Common {

    public static final Logger log = LogManager.getLogger("Hodgepodge");
    public static final Marker securityMarker = MarkerManager.getMarker("SuspiciousPackets");
    public static XSTR RNG = new XSTR();

    public static void logASM(Logger log, String message) {
        if (HodgepodgeCore.isObf()) {
            log.debug(message);
        } else {
            log.info(message);
        }
    }

    public static void init() {
        if (Compat.isIC2Present()) {
            ic2DispenserBehavior();
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
}
