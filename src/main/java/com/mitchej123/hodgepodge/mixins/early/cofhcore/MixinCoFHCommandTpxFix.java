package com.mitchej123.hodgepodge.mixins.early.cofhcore;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.DimensionManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import cofh.core.command.CommandHandler;
import cofh.core.command.CommandTPX;
import cofh.core.command.ISubCommand;
import cofh.lib.util.helpers.EntityHelper;

@Mixin(value = CommandTPX.class, remap = false)
public abstract class MixinCoFHCommandTpxFix implements ISubCommand {

    /**
     * @author SST-03
     * @reason change command behavior
     */
    @Overwrite
    public void handleCommand(ICommandSender sender, String[] arguments) {
        final EntityPlayerMP playerSender = CommandBase.getCommandSenderAsPlayer(sender);
        // tpx [Player] {(<DimensionID>|<TargetPlayer>) | <x> <y> <z> [DimensionID]}
        switch (arguments.length) {
            case 2: // (tpx {<dimension>|<player>}) teleporting self to player or dimension
                try {
                    teleportDimXYZ(sender, playerSender, arguments[1], "~", "~", "~");
                } catch (NumberInvalidException t) {
                    final EntityPlayerMP player = CommandBase.getPlayer(sender, arguments[1]);
                    teleportPlayer2Player(sender, playerSender, player);
                }
                break;
            case 3: // (tpx <player1> {<dimension>|<player2>}) teleporting player1 to player2 or player1 to dimension
            {
                final EntityPlayerMP player = CommandBase.getPlayer(sender, arguments[1]);
                try {
                    teleportDimXYZ(sender, player, arguments[2], "~", "~", "~");
                } catch (NumberInvalidException t) {
                    final EntityPlayerMP otherPlayer = CommandBase.getPlayer(sender, arguments[2]);
                    teleportPlayer2Player(sender, player, otherPlayer);
                }
                break;
            }
            case 4: // (tpx <x> <y> <z>) teleporting self within dimension
                teleportXYZ(sender, playerSender, arguments[1], arguments[2], arguments[3]);
                break;
            case 5: // (tpx {<x> <y> <z> <dimension> | <player> <x> <y> <z>}) teleporting player within player's
                    // dimension orself to dimension
                try {
                    teleportDimXYZ(sender, playerSender, arguments[4], arguments[1], arguments[2], arguments[3]);
                } catch (NumberInvalidException t) {
                    final EntityPlayerMP player = CommandBase.getPlayer(sender, arguments[1]);
                    teleportXYZ(sender, player, arguments[2], arguments[3], arguments[4]);
                }
                break;
            case 6: // (tpx <player> <x> <y> <z> <dimension>) teleporting player to dimension and location
            {
                final EntityPlayerMP player = CommandBase.getPlayer(sender, arguments[1]);
                teleportDimXYZ(sender, player, arguments[5], arguments[2], arguments[3], arguments[4]);
                break;
            }
            default:
                sender.addChatMessage(new ChatComponentTranslation("info.cofh.command.syntaxError"));
                throw new WrongUsageException("info.cofh.command.tpx.syntax");
        }
    }

    // just like vanilla tp, allows to teleport to oneself
    @Unique
    private void teleportPlayer2Player(ICommandSender sender, EntityPlayerMP player, EntityPlayerMP playerTarget) {
        player.mountEntity((Entity) null);
        if (playerTarget.dimension == player.dimension) {
            player.setPositionAndUpdate(playerTarget.posX, playerTarget.posY, playerTarget.posZ);
            CommandHandler.logAdminCommand(
                    sender,
                    this,
                    "info.cofh.command.tpx.otherTo",
                    player.getCommandSenderName(),
                    playerTarget.getCommandSenderName(),
                    player.posX,
                    player.posY,
                    player.posZ);
        } else {
            EntityHelper.transferPlayerToDimension(
                    player,
                    playerTarget.dimension,
                    playerTarget.mcServer.getConfigurationManager());
            player.setPositionAndUpdate(playerTarget.posX, playerTarget.posY, playerTarget.posZ);
            CommandHandler.logAdminCommand(
                    sender,
                    this,
                    "info.cofh.command.tpx.dimensionOtherTo",
                    player.getCommandSenderName(),
                    playerTarget.getCommandSenderName(),
                    player.worldObj.provider.getDimensionName(),
                    player.posX,
                    player.posY,
                    player.posZ);
        }
    }

    @Unique
    private void teleportDimXYZ(ICommandSender sender, EntityPlayerMP player, String dimension, String X, String Y,
            String Z) {
        final int dim = CommandBase.parseInt(sender, dimension);
        if (!DimensionManager.isDimensionRegistered(dim)) {
            throw new CommandException("info.cofh.command.world.notFound");
        }
        teleportDimXYZ(
                sender,
                player,
                dim,
                CommandBase.func_110666_a(player, player.posX, X),
                CommandBase.func_110666_a(player, player.posY, Y),
                CommandBase.func_110666_a(player, player.posZ, Z));
    }

    @Unique
    private void teleportDimXYZ(ICommandSender sender, EntityPlayerMP player, int dimension, double X, double Y,
            double Z) {
        player.mountEntity((Entity) null);
        if (player.dimension != dimension) {
            EntityHelper.transferPlayerToDimension(player, dimension, player.mcServer.getConfigurationManager());
        }
        player.setPositionAndUpdate(X, Y, Z);
        CommandHandler.logAdminCommand(
                sender,
                this,
                "info.cofh.command.tpx.dimensionOther",
                player.getCommandSenderName(),
                player.worldObj.provider.getDimensionName(),
                player.posX,
                player.posY,
                player.posZ);
    }

    @Unique
    private void teleportXYZ(ICommandSender sender, EntityPlayerMP player, String X, String Y, String Z) {
        player.mountEntity((Entity) null);
        final double x = CommandBase.func_110666_a(player, player.posX, X);
        final double y = CommandBase.func_110666_a(player, player.posY, Y);
        final double z = CommandBase.func_110666_a(player, player.posZ, Z);
        player.setPositionAndUpdate(x, y, z);
        CommandHandler.logAdminCommand(
                sender,
                this,
                "info.cofh.command.tpx.other",
                player.getCommandSenderName(),
                player.posX,
                player.posY,
                player.posZ);
    }
}
