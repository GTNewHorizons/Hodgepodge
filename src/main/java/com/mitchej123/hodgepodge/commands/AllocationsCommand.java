package com.mitchej123.hodgepodge.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;

import com.mitchej123.hodgepodge.client.AllocationRateHUD;

public final class AllocationsCommand extends CommandBase {

    private AllocationRateHUD handler = null;

    @Override
    public String getCommandName() {
        return "allocationshud";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.getCommandName();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (handler == null) {
            handler = new AllocationRateHUD(false);
            MinecraftForge.EVENT_BUS.register(handler);
        } else {
            MinecraftForge.EVENT_BUS.unregister(handler);
            handler = null;
        }
    }
}
