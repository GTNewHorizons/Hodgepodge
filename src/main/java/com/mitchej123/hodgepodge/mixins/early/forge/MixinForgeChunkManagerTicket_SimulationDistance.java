package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraftforge.common.ForgeChunkManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.mixins.interfaces.SimulationDistanceTicket;

@Mixin(value = ForgeChunkManager.Ticket.class, remap = false)
public class MixinForgeChunkManagerTicket_SimulationDistance implements SimulationDistanceTicket {

    @Unique
    private volatile boolean hodgepodge$simulationDisabled;

    @Override
    public boolean hodgepodge$isSimulationDisabled() {
        return hodgepodge$simulationDisabled;
    }

    @Override
    public void hodgepodge$setSimulationDisabled(boolean disabled) {
        hodgepodge$simulationDisabled = disabled;
    }
}
