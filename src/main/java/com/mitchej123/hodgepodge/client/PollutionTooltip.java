package com.mitchej123.hodgepodge.client;

import com.mitchej123.hodgepodge.Compat;
import com.mitchej123.hodgepodge.config.PollutionConfig;
import com.mitchej123.hodgepodge.util.PollutionHelper;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Utility;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class PollutionTooltip {

    public static final PollutionTooltip INSTANCE = new PollutionTooltip();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void getTooltip(ItemTooltipEvent event) {
        if (event.itemStack == null) return;

        String producesPollutionFormat = "Produces %d Pollution/Second";

        if (PollutionConfig.furnacesPollute) {
            String furnacePollution = String.format(producesPollutionFormat, PollutionConfig.furnacePollutionAmount);

            // Furnace and Iron Furnace
            if (GT_Utility.areStacksEqual(event.itemStack, new ItemStack(Blocks.furnace)) || GT_Utility
                    .areStacksEqual(event.itemStack, GT_ModHandler.getModItem("IC2", "blockMachine", 1, 1))) {
                event.toolTip.add(furnacePollution);
            }

            // Alchemical Furnace
            if (Compat.isThaumcraftPresent()) {
                if (GT_Utility.areStacksEqual(
                        event.itemStack,
                        GT_ModHandler.getModItem("Thaumcraft", "blockStoneDevice", 1, 0))) {
                    event.toolTip.add(furnacePollution);
                }
            }

            // Advanced Alchemical Furnace
            if (Compat.isThaumicBasesPresent()) {
                if (GT_Utility.areStacksEqual(
                        event.itemStack,
                        GT_ModHandler.getModItem("thaumicbases", "advAlchFurnace", 1, 0))) {
                    event.toolTip.add(furnacePollution);
                }
            }
        }

        if (Compat.isRailcraftPresent() && PollutionConfig.railcraftPollutes) {
            String multiProducesPollutionFormat = "A complete Multiblock produces %d Pollution/Second";

            // Solid and Liquid Boiler Firebox
            if (GT_Utility.areStacksEqual(event.itemStack, GT_ModHandler.getModItem("Railcraft", "machine.beta", 1, 5))
                    || GT_Utility.areStacksEqual(
                            event.itemStack,
                            GT_ModHandler.getModItem("Railcraft", "machine.beta", 1, 6))) {
                event.toolTip.add(
                        String.format(
                                "Produces %d Pollution/Second per firebox",
                                PollutionConfig.fireboxPollutionAmount));
            }

            // Tunnel Bore
            if (GT_Utility.areStacksEqual(event.itemStack, GT_ModHandler.getModItem("Railcraft", "cart.bore", 1, 0))) {
                event.toolTip.add(String.format(producesPollutionFormat, PollutionConfig.tunnelBorePollutionAmount));
            }

            // Coke Oven Brick
            if (GT_Utility
                    .areStacksEqual(event.itemStack, GT_ModHandler.getModItem("Railcraft", "machine.alpha", 1, 7))) {
                event.toolTip.add(String.format(multiProducesPollutionFormat, PollutionConfig.cokeOvenPollutionAmount));
            }

            // Advanced Coke Oven Brick
            if (GT_Utility
                    .areStacksEqual(event.itemStack, GT_ModHandler.getModItem("Railcraft", "machine.alpha", 1, 12))) {
                event.toolTip.add(
                        String.format(multiProducesPollutionFormat, PollutionConfig.advancedCokeOvenPollutionAmount));
            }

            // Hobbyist's Steam Engine
            if (GT_Utility
                    .areStacksEqual(event.itemStack, GT_ModHandler.getModItem("Railcraft", "machine.beta", 1, 7))) {
                event.toolTip
                        .add(String.format(producesPollutionFormat, PollutionConfig.hobbyistEnginePollutionAmount));
            }
        }

        // Galacticraft (and Galaxy Space) rockets
        if (Compat.isGalacticraftPresent() && PollutionConfig.rocketsPollute && event.itemStack.getItem() != null) {
            String simpleName = event.itemStack.getItem().getClass().getSimpleName();
            if (simpleName.contains("Rocket")) {
                for (char d : simpleName.toCharArray()) {
                    if (Character.isDigit(d)) {
                        int tier = Character.getNumericValue(d);
                        event.toolTip.add(
                                String.format(
                                        "Produces %d Pollution/Second when ignited",
                                        PollutionHelper.rocketIgnitionPollutionAmount(tier)));
                        event.toolTip.add(
                                String.format(
                                        "Produces %d Pollution/Second when flying",
                                        PollutionHelper.flyingRocketPollutionAmount(tier)));
                        break;
                    }
                }
            }
        }
    }
}
