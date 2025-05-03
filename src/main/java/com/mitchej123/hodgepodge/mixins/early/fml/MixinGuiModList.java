package com.mitchej123.hodgepodge.mixins.early.fml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.Hodgepodge;
import com.mitchej123.hodgepodge.client.bettermodlist.InfoButton;
import com.mitchej123.hodgepodge.client.bettermodlist.SortType;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.mixins.interfaces.IGuiModList;
import com.mitchej123.hodgepodge.mixins.interfaces.IGuiScrollingList;
import com.mitchej123.hodgepodge.mixins.interfaces.IGuiSlotModList;

import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.client.GuiSlotModList;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

@Mixin(GuiModList.class)
public class MixinGuiModList extends GuiScreen implements IGuiModList {

    @Shadow(remap = false)
    private ArrayList<ModContainer> mods;
    @Shadow(remap = false)
    private int listWidth;
    @Shadow(remap = false)
    private ModContainer selectedMod;
    @Shadow(remap = false)
    private int selected;
    @Shadow(remap = false)
    private GuiSlotModList modList;
    private int hodgepodge$buttonMargin = 1;
    private int hodgepodge$numButtons = SortType.values().length;

    private String hodgepodge$lastFilterText = "";

    private GuiTextField hodgepodge$search;
    private boolean hodgepodge$sorted = false;
    private SortType hodgepodge$sortType = SortType.values()[TweaksConfig.defaultModSort];

    // Method to initialize GUI components
    @Inject(method = "initGui", at = @At("TAIL"))
    public void onInitGui(CallbackInfo ci) {
        // Initialize search box
        int hodgepodge$xPosition = ((IGuiScrollingList) modList).getBottom() + 17;
        hodgepodge$search = new GuiTextField(this.fontRendererObj, 12, hodgepodge$xPosition, listWidth - 4, 14);
        hodgepodge$search.setFocused(true);
        hodgepodge$search.setCanLoseFocus(true);

        // Let's move some buttons
        for (GuiButton button : buttonList) {
            if (button.id == 6) // Done button
            {
                int min = listWidth + 10;
                int max = width;
                button.xPosition = ((min + max) / 2) - (button.width / 2);
                button.yPosition += 10;
            } else if (button.id == 20 || button.id == 21) // Config/Disable
            {
                button.yPosition += 10;
            }
        }

        // Initialize sorting buttons
        int buttonWidth = listWidth / hodgepodge$numButtons;
        int x = 10, y = 10;

        for (SortType type : SortType.values()) {
            GuiButton button = new GuiButton(
                    type.getButtonID(),
                    x,
                    y,
                    buttonWidth - hodgepodge$buttonMargin,
                    20,
                    type.getName());
            buttonList.add(button);
            x += buttonWidth + hodgepodge$buttonMargin;
        }
        buttonList.add(new InfoButton(this.hodgepodge$GuiModList()));
        // Load and sort mods
        hodgepodge$reloadMods();
        // Update button states to reflect the current sorting method
        hodgepodge$updateButtonStates();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        hodgepodge$search.updateCursorCounter();

        if (!hodgepodge$search.getText().equals(hodgepodge$lastFilterText)) {
            hodgepodge$reloadMods();
            hodgepodge$sorted = false;
        }

        if (!hodgepodge$sorted) {
            hodgepodge$reloadMods();
            Collections.sort(mods, hodgepodge$sortType.getComparator());
            selected = ((IGuiScrollingList) modList).setSelectedIndex(mods.indexOf(selectedMod));
            hodgepodge$sorted = true;
        }
    }

    // Method to reload and filter mods based on search text
    private void hodgepodge$reloadMods() {
        ArrayList<ModContainer> mods = ((IGuiSlotModList) modList).getMods();
        mods.clear();
        for (ModContainer m : Loader.instance().getActiveModList()) {
            if (m.getName().toLowerCase(Locale.US).contains(hodgepodge$search.getText().toLowerCase(Locale.US))
                    && m.getMetadata().parentMod == null) {
                mods.add(m);
            }
        }
        this.mods = mods;
        hodgepodge$lastFilterText = hodgepodge$search.getText();
    }

    // Handle the action when a button is clicked (e.g., for sorting)
    @Inject(method = "actionPerformed", at = @At("TAIL"))
    public void onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == 30) {
            hodgepodge$search.setText("");
            hodgepodge$reloadMods();
            for (ModContainer m : mods) {
                if (m.getName().equals(Hodgepodge.NAME)) {
                    selectedMod = m;
                    selected = ((IGuiScrollingList) modList).setSelectedIndex(mods.indexOf(m));
                }
            }
        }
        SortType type = SortType.getTypeForButton(button.id);
        if (type != null) {
            hodgepodge$sorted = false;
            hodgepodge$sortType = type;
            hodgepodge$updateButtonStates();
            this.mods = ((IGuiSlotModList) modList).getMods();
        }
    }

    // Update button states (disable active button, enable others)
    private void hodgepodge$updateButtonStates() {
        for (GuiButton button : buttonList) {
            if (SortType.getTypeForButton(button.id) != null) {
                button.enabled = true;
            }
            if (button.id == hodgepodge$sortType.getButtonID()) {
                button.enabled = false;
            }
        }
    }

    // Method to draw the UI, including the search bar and buttons
    @Inject(method = "drawScreen", at = @At("TAIL"))
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        int Bottom = ((IGuiScrollingList) modList).getBottom();
        int Right = ((IGuiScrollingList) modList).getRight();
        // Draw the search label and text field
        String text = I18n.format("fml.menu.mods.search").replace("\\", "");;
        int x = ((10 + Right) / 2) - (fontRendererObj.getStringWidth(text) / 2);
        fontRendererObj.drawString(text, x, Bottom + 5, 0xFFFFFF);
        hodgepodge$search.drawTextBox();
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        hodgepodge$search.mouseClicked(x, y, button);
        if (button == 1 && x >= hodgepodge$search.xPosition
                && x < hodgepodge$search.xPosition + hodgepodge$search.width
                && y >= hodgepodge$search.yPosition
                && y < hodgepodge$search.yPosition + hodgepodge$search.height) {
            hodgepodge$search.setText("");
        }
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        super.keyTyped(p_73869_1_, p_73869_2_);
        hodgepodge$search.textboxKeyTyped(p_73869_1_, p_73869_2_);
    }

    private GuiModList hodgepodge$GuiModList() {
        return (GuiModList) (Object) this;
    }

    @Override
    public ModContainer selectedMod() {
        return selectedMod;
    }
}
