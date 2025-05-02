package com.mitchej123.hodgepodge.mixins.early.fml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.Hodgepodge;
import com.mitchej123.hodgepodge.client.bettermodlist.InfoButton;
import com.mitchej123.hodgepodge.client.bettermodlist.SortType;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.mixins.interfaces.GetSelectedMod;

import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.client.GuiSlotModList;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

@Mixin(GuiModList.class)
public abstract class MixinGuiModList extends GuiScreen implements GetSelectedMod {

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
    private GuiTextField search;
    private boolean sorted = false;
    private SortType sortType = SortType.values()[TweaksConfig.defaultModSort];
    private String lastFilterText = "";

    // Method to initialize GUI components
    @Inject(method = "initGui", at = @At("TAIL"))
    public void onInitGui(CallbackInfo ci) {
        // Initialize search box
        search = new GuiTextField(this.fontRendererObj, 12, height - 70, listWidth - 4, 14);
        search.setFocused(true);
        search.setCanLoseFocus(true);

        // Let's move some buttons
        for (GuiButton button : (List<GuiButton>) buttonList) {
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
        int buttonWidth = listWidth / SortType.values().length;
        int buttonMargin = 1;
        int x = 10, y = 10;

        for (SortType type : SortType.values()) {
            GuiButton button = new GuiButton(type.getButtonID(), x, y, buttonWidth - buttonMargin, 20, type.getName());
            buttonList.add(button);
            x += buttonWidth + buttonMargin;
        }
        buttonList.add(new InfoButton(this.hodgepodge$GuiModList()));
        // Load and sort mods
        reloadMods();
        // Update button states to reflect the current sorting method
        updateButtonStates();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        search.updateCursorCounter();

        if (!search.getText().equals(lastFilterText)) {
            reloadMods();
            sorted = false;
        }

        if (!sorted) {
            reloadMods();
            Collections.sort(mods, sortType.getComparator());
            setMods();
            sorted = true;
        }
    }

    // Method to reload and filter mods based on search text
    private void reloadMods() {
        mods.clear();
        for (ModContainer m : Loader.instance().getActiveModList()) {
            if (m.getName().toLowerCase(Locale.US).contains(search.getText().toLowerCase(Locale.US))
                    && m.getMetadata().parentMod == null) {
                mods.add(m);
            }
        }
        setMods();
        lastFilterText = search.getText();
    }

    // Handle the action when a button is clicked (e.g., for sorting)
    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == 30) {
            search.setText("");
            reloadMods();
            for (ModContainer m : mods) {
                if (m.getName().equals(Hodgepodge.NAME)) {
                    selectedMod = m;
                    setMods();
                }
            }
        }
        SortType type = SortType.getTypeForButton(button.id);
        if (type != null) {
            sorted = false;
            sortType = type;
            updateButtonStates();
        }
        setMods();
    }

    // Update button states (disable active button, enable others)
    private void updateButtonStates() {
        for (GuiButton button : buttonList) {
            if (SortType.getTypeForButton(button.id) != null) {
                button.enabled = true;
            }
            if (button.id == sortType.getButtonID()) {
                button.enabled = false;
            }
        }
    }

    // Method to draw the UI, including the search bar and buttons
    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        // Draw the search label and text field
        String searchText = "Search:";
        int x = ((listWidth + 20) - fontRendererObj.getStringWidth(searchText)) / 2;
        fontRendererObj.drawString(searchText, x, height - 82, 0xFFFFFF);
        search.drawTextBox();
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        search.mouseClicked(x, y, button);
        if (button == 1 && x >= search.xPosition
                && x < search.xPosition + search.width
                && y >= search.yPosition
                && y < search.yPosition + search.height) {
            search.setText("");
        }
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        super.keyTyped(p_73869_1_, p_73869_2_);
        search.textboxKeyTyped(p_73869_1_, p_73869_2_);
    }

    private GuiModList hodgepodge$GuiModList() {
        return (GuiModList) (Object) this;
    }

    private void setMods() {
        List<ModContainer> mod = mods;

        ModContainer sel = selectedMod;
        boolean found = false;
        for (int i = 0; !found && i < mod.size(); i++) {
            if (sel == mod.get(i)) {
                selected = i;
                found = true;
            }
        }
        if (!found) {
            selected = -1;
            selectedMod = null;
        }
        sorted = false;
    }

    @Override
    public ModContainer selectedMod() {
        return selectedMod;
    }
}
