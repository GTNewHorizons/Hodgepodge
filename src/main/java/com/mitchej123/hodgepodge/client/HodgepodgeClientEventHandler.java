package com.mitchej123.hodgepodge.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.oredict.OreDictionary;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@EventBusSubscriber(side = Side.CLIENT, phase = Phase.PRE)
public class HodgepodgeClientEventHandler {

    private static final String ADVANCED_INFO_TEXT_PRE = EnumChatFormatting.DARK_GRAY + "     ";
    private static final String ADVANCED_INFO_HEADER_PRE = EnumChatFormatting.GRAY + "  -";

    // TODO localize
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTooltip(ItemTooltipEvent event) {
        if (event.itemStack == null) return;
        if (event.itemStack.getItem() == null) return;
        if (!event.showAdvancedItemTooltips) return;
        if (!GuiScreen.isCtrlKeyDown()) {
            event.toolTip.add(
                    EnumChatFormatting.DARK_GRAY + ""
                            + EnumChatFormatting.ITALIC
                            + StatCollector.translateToLocal("hodgepodge.aainfo.ctrl_hint"));
            return;
        }
        ItemStack stack = event.itemStack;

        var oreIDs = OreDictionary.getOreIDs(stack);
        if (oreIDs.length > 0) {
            event.toolTip.add(ADVANCED_INFO_HEADER_PRE + StatCollector.translateToLocal("hodgepodge.aainfo.oredicts"));
            for (int oreID : oreIDs) {
                event.toolTip.add(ADVANCED_INFO_TEXT_PRE + OreDictionary.getOreName(oreID));
            }
        }

        event.toolTip.add(ADVANCED_INFO_HEADER_PRE + StatCollector.translateToLocal("hodgepodge.aainfo.reg_name"));
        event.toolTip.add(ADVANCED_INFO_TEXT_PRE + Item.itemRegistry.getNameForObject(stack.getItem()));

        String baseName = stack.getItem().getUnlocalizedName();
        if (baseName != null) {
            event.toolTip.add(
                    ADVANCED_INFO_HEADER_PRE
                            + StatCollector.translateToLocal("hodgepodge.aainfo.item_unlocalized_name"));
            event.toolTip.add(ADVANCED_INFO_TEXT_PRE + baseName);
        }

        // Unlocalized Name
        String metaName = stack.getItem().getUnlocalizedName(stack);
        if (metaName != null && baseName != null && !metaName.equals(baseName)) {
            event.toolTip.add(
                    ADVANCED_INFO_HEADER_PRE
                            + StatCollector.translateToLocal("hodgepodge.aainfo.meta_unlocalized_name"));
            event.toolTip.add(ADVANCED_INFO_TEXT_PRE + metaName);
        }

        // NBT
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && !compound.hasNoTags()) {
            event.toolTip.add(ADVANCED_INFO_HEADER_PRE + StatCollector.translateToLocal("hodgepodge.aainfo.nbt"));
            if (GuiScreen.isShiftKeyDown()) {
                event.toolTip.add(ADVANCED_INFO_TEXT_PRE + compound);
            } else {
                event.toolTip.add(
                        ADVANCED_INFO_TEXT_PRE + EnumChatFormatting.ITALIC
                                + StatCollector.translateToLocal("hodgepodge.aainfo.nbt_hint"));
            }
        }

    }
}
