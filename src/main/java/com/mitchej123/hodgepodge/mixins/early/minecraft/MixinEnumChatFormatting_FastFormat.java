package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mitchej123.hodgepodge.util.StringUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mixin(EnumChatFormatting.class)
public class MixinEnumChatFormatting_FastFormat {

    /**
     * @author Alexdoru
     * @reason It's faster
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public static String getTextWithoutFormattingCodes(String text) {
        return StringUtil.removeFormattingCodes(text);
    }

}
