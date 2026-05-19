package com.mitchej123.hodgepodge.mixins.early.fml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.gtnewhorizon.gtnhlib.util.FilesUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.Hodgepodge;
import com.mitchej123.hodgepodge.client.bettermodlist.InfoButton;
import com.mitchej123.hodgepodge.client.bettermodlist.SortType;
import com.mitchej123.hodgepodge.config.TweaksConfig;

import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.client.GuiSlotModList;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

@Mixin(GuiModList.class)
public class MixinGuiModList extends GuiScreen {

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

    @Unique
    private ModContainer hodgepodge$pendingSelection = null;
    @Unique
    private SortType hodgepodge$sortType = SortType.values()[TweaksConfig.defaultModSort];
    @Unique
    private String hodgepodge$lastFilterText = "";
    @Unique
    private GuiTextField hodgepodge$search;
    @Unique
    private boolean hodgepodge$sorted = false;
    @Unique
    private float hodgepodge$savedScrollDistance = 0f;

    @Unique
    private int hodgepodge$listBottom;
    @Unique
    private int hodgepodge$listRight;

    @Unique
    private int hodgepodge$urlX = 0;
    @Unique
    private int hodgepodge$urlY = 0;
    @Unique
    private String hodgepodge$urlStr;

    @Inject(method = "initGui", at = @At("HEAD"))
    public void onInitGuiHead(CallbackInfo ci) {
        // save before the new GuiSlotModList is constructed
        if (modList != null) {
            hodgepodge$savedScrollDistance = hodgepodge$GuiScrollingList().hodgepodge$getScrollDistance();
        }
    }

    // Method to initialize GUI components
    @Inject(method = "initGui", at = @At("TAIL"))
    public void onInitGui(CallbackInfo ci) {
        // Initialize search box
        hodgepodge$listBottom = hodgepodge$GuiScrollingList().hodgepodge$getBottom();
        hodgepodge$listRight = hodgepodge$GuiScrollingList().hodgepodge$getRight();

        hodgepodge$search = new GuiTextField(this.fontRendererObj, 12, hodgepodge$listBottom + 17, listWidth - 4, 14);
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
        int buttonWidth = listWidth / SortType.VALUES.length;
        int buttonMargin = 1;
        int x = 10, y = 10;

        for (SortType type : SortType.VALUES) {
            GuiButton button = new GuiButton(type.getButtonID(), x, y, buttonWidth - buttonMargin, 20, type.getName());
            buttonList.add(button);
            x += buttonWidth + buttonMargin;
        }
        buttonList.add(new InfoButton(this.hodgepodge$GuiModList(), () -> selectedMod));

        hodgepodge$sorted = false;
        hodgepodge$reloadMods();
        hodgepodge$updateButtonStates();
        hodgepodge$GuiScrollingList().hodgepodge$setScrollDistance(hodgepodge$savedScrollDistance);
    }

    @WrapOperation(
            method = "initGui",
            at = @At(value = "NEW", target = "cpw/mods/fml/client/GuiSlotModList"),
            remap = false)
    private GuiSlotModList hodgepodge$wrapNewGuiSlotModList(GuiModList parent, ArrayList<ModContainer> mods,
            int listWidth, Operation<GuiSlotModList> original) {
        int originalHeight = parent.height;
        parent.height = originalHeight - 25;
        GuiSlotModList result = original.call(parent, mods, listWidth);
        parent.height = originalHeight;
        return result;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        hodgepodge$search.updateCursorCounter();

        if (!hodgepodge$search.getText().equals(hodgepodge$lastFilterText)) {
            hodgepodge$sorted = false;
        }

        if (!hodgepodge$sorted) {
            hodgepodge$reloadMods();
            ArrayList<ModContainer> mods = hodgepodge$GuiSlotModList().hodgepodge$getMods();
            mods.sort(hodgepodge$sortType.getComparator());
            this.mods = mods;

            int index = mods.indexOf(selectedMod);
            hodgepodge$GuiScrollingList().hodgepodge$setSelectedIndex(index);
            selected = index;

            if (hodgepodge$pendingSelection != null) {
                selectedMod = hodgepodge$pendingSelection;
                index = mods.indexOf(selectedMod);
                hodgepodge$GuiScrollingList().hodgepodge$setSelectedIndex(index);
                selected = index;
                hodgepodge$scrollToSelected(index);
                hodgepodge$pendingSelection = null;
            }
            hodgepodge$sorted = true;
        }
    }

    // Method to reload and filter mods based on search text
    @Unique
    private void hodgepodge$reloadMods() {
        String filter = hodgepodge$search.getText().toLowerCase(Locale.US);

        ArrayList<ModContainer> mods = hodgepodge$GuiSlotModList().hodgepodge$getMods();
        mods.clear();
        for (ModContainer m : Loader.instance().getActiveModList()) {
            if (m.getName().toLowerCase(Locale.US).contains(filter) && m.getMetadata().parentMod == null) {
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
            hodgepodge$sorted = false;
            hodgepodge$pendingSelection = Loader.instance().getActiveModList().stream()
                    .filter(m -> m.getName().equals(Hodgepodge.NAME)).findFirst().orElse(null);
        }
        SortType type = SortType.getTypeForButton(button.id);
        if (type != null) {
            hodgepodge$sorted = false;
            hodgepodge$sortType = type;
            hodgepodge$updateButtonStates();
        }
    }

    @Unique
    private void hodgepodge$updateButtonStates() {
        for (GuiButton button : buttonList) {
            if (SortType.getTypeForButton(button.id) != null) {
                button.enabled = (button.id != hodgepodge$sortType.getButtonID());
            }
        }
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        // Draw the search label and text field
        String text = "Search:";
        int x = ((10 + hodgepodge$listRight) / 2) - (fontRendererObj.getStringWidth(text) / 2);
        fontRendererObj.drawString(text, x, hodgepodge$listBottom + 5, 0xFFFFFF);
        hodgepodge$search.drawTextBox();
    }

    // Clickable URL
    @ModifyArgs(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/client/GuiModList;drawLine(Ljava/lang/String;II)I",
                    remap = false))
    private void replaceDrawLineArgs(Args args) {
        String line = args.get(0);
        if (line != null && line.startsWith("URL: ")) {
            int offset = args.get(1);
            int shifty = args.get(2);
            hodgepodge$urlStr = line.substring("URL: ".length());
            hodgepodge$urlX = offset + this.fontRendererObj.getStringWidth("URL: ");
            hodgepodge$urlY = shifty;
            args.set(0, "URL: §9§n" + hodgepodge$urlStr);
        }
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        hodgepodge$search.mouseClicked(x, y, button);
        if (button == 1 && x >= hodgepodge$search.xPosition
                && x < hodgepodge$search.xPosition + hodgepodge$search.width
                && y >= hodgepodge$search.yPosition
                && y < hodgepodge$search.yPosition + hodgepodge$search.height) {
            hodgepodge$search.setText("");
        }
        // Clickable URL
        if (hodgepodge$urlStr != null) {
            int height = this.fontRendererObj.FONT_HEIGHT;
            int width = this.fontRendererObj.getStringWidth(hodgepodge$urlStr);
            if (x >= hodgepodge$urlX && x <= hodgepodge$urlX + width
                    && y >= hodgepodge$urlY
                    && y <= hodgepodge$urlY + height) {
                try {
                    FilesUtil.openUri(new URI(hodgepodge$urlStr));
                } catch (URISyntaxException e) {
                    Common.log.warn("Invalid URL in mod metadata: {}", hodgepodge$urlStr, e);
                }
            }
        }
    }

    @Override
    public void keyTyped(char p_73869_1_, int p_73869_2_) {
        super.keyTyped(p_73869_1_, p_73869_2_);
        hodgepodge$search.textboxKeyTyped(p_73869_1_, p_73869_2_);
    }

    @Unique
    private void hodgepodge$scrollToSelected(int index) {
        float targetScroll = index * 35;
        hodgepodge$GuiScrollingList().hodgepodge$setScrollDistance(targetScroll);
    }

    @Unique
    private GuiModList hodgepodge$GuiModList() {
        return (GuiModList) (Object) this;
    }

    @Unique
    private AccessorGuiScrollingList hodgepodge$GuiScrollingList() {
        return (AccessorGuiScrollingList) this.modList;
    }

    @Unique
    private AccessorGuiSlotModList hodgepodge$GuiSlotModList() {
        return (AccessorGuiSlotModList) this.modList;
    }
}
