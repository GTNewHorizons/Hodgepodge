package com.mitchej123.hodgepodge.mixins.early.fml;

import net.minecraft.client.resources.I18n;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.LoaderState.ModState;

@Mixin(GuiModList.class)
public class MixinGuiModList_Localize {

    @ModifyConstant(method = "initGui", constant = @Constant(stringValue = "Config"))
    private String hodgepodge$localizeConfigButton(String original) {
        return I18n.format("hodgepodge.modlist.config");
    }

    @ModifyConstant(method = "initGui", constant = @Constant(stringValue = "Disable"))
    private String hodgepodge$localizeDisableButton(String original) {
        return I18n.format("hodgepodge.modlist.disable");
    }

    @ModifyConstant(
            method = "drawScreen",
            constant = { @Constant(stringValue = "Mod List"), @Constant(stringValue = "No child mods for this mod"),
                    @Constant(stringValue = "No mod information found"),
                    @Constant(stringValue = "Ask your mod author to provide a mod mcmod.info file") })
    private String hodgepodge$localizeLabels(String original) {
        return switch (original) {
            case "Mod List" -> I18n.format("hodgepodge.modlist.title");
            case "No child mods for this mod" -> I18n.format("hodgepodge.modlist.no_children");
            case "No mod information found" -> I18n.format("hodgepodge.modlist.no_information");
            case "Ask your mod author to provide a mod mcmod.info file" -> I18n
                    .format("hodgepodge.modlist.ask_for_metadata");
            default -> original;
        };
    }

    @WrapOperation(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    private String hodgepodge$localizeDetails(String format, Object[] args, Operation<String> original) {
        return switch (format) {
            case "Version: %s (%s)" -> I18n.format("hodgepodge.modlist.version_details", args);
            case "Version: %s" -> I18n.format("hodgepodge.modlist.version", args);
            case "Mod ID: '%s' Mod State: %s" -> I18n
                    .format("hodgepodge.modlist.id_and_state", args[0], hodgepodge$state((ModState) args[1]));
            case "Mod State: %s" -> I18n.format("hodgepodge.modlist.state", hodgepodge$state((ModState) args[0]));
            case "Credits: %s" -> I18n.format("hodgepodge.modlist.credits", args);
            case "Authors: %s" -> I18n.format("hodgepodge.modlist.authors", args);
            case "Child mods: %s" -> I18n.format("hodgepodge.modlist.children", args);
            default -> original.call(format, args);
        };
    }

    private static String hodgepodge$state(ModState state) {
        return I18n.format("hodgepodge.modlist.state." + state.name());
    }
}
