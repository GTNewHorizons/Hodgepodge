package com.mitchej123.hodgepodge.mixins.early.fml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.StatCollector;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;

@Mixin(GuiConfig.class)
@SuppressWarnings("rawtypes")
public abstract class MixinGuiConfig extends GuiScreen {

    @Shadow(remap = false)
    @Final
    public List<IConfigElement> configElements;

    @Shadow(remap = false)
    public String title;

    @Shadow(remap = false)
    public GuiConfigEntries entryList;

    @Unique
    private List<GuiConfigEntries.IConfigEntry> hodgepodge$allEntries;

    @Unique
    private GuiTextField hodgepodge$searchBar;

    @Unique
    private static String hodgepodge$lastSearch = "";

    @Unique
    private String hodgepodge$localLastSearch = "";

    @Unique
    private int hodgepodge$oldWidth;

    @Shadow
    public abstract void initGui();

    @Inject(
            method = "<init>(Lnet/minecraft/client/gui/GuiScreen;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;ZZLjava/lang/String;Ljava/lang/String;)V",
            at = @At(value = "TAIL"))
    private void hodgepodge$init(GuiScreen parentScreen, List<IConfigElement> configElements, String modID,
            String configID, boolean allRequireWorldRestart, boolean allRequireMcRestart, String title,
            String titleLine2, CallbackInfo ci) {
        hodgepodge$oldWidth = width;
        hodgepodge$allEntries = new ArrayList<>(entryList.listEntries);
        if (!(parentScreen instanceof GuiConfig)) {
            hodgepodge$lastSearch = "";
        }
    }

    @Inject(method = "initGui", at = @At(value = "TAIL"))
    private void hodgepodge$initSearchBar(CallbackInfo ci) {
        int centeredTitleWidth = fontRendererObj.getStringWidth(this.title) / 2;
        int x = width / 2 + centeredTitleWidth + 10;
        int searchWidth = width - x - 10;
        if (hodgepodge$searchBar == null) {
            hodgepodge$searchBar = new GuiTextField(fontRendererObj, x, 5, searchWidth, 12);
        } else if (width != hodgepodge$oldWidth) {
            hodgepodge$oldWidth = width;
            hodgepodge$searchBar.xPosition = x;
            hodgepodge$searchBar.width = searchWidth;
        }

        hodgepodge$searchBar.setText(hodgepodge$lastSearch);
    }

    @Inject(
            method = "initGui",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lcpw/mods/fml/client/config/GuiConfig;needsRefresh:Z",
                    remap = false))
    private void hodgepodge$captureEntries(CallbackInfo ci) {
        hodgepodge$allEntries = new ArrayList<>(entryList.listEntries);
    }

    @Inject(method = "keyTyped", at = @At(value = "TAIL"))
    private void hodgepodge$keyTyped(char eventChar, int eventKey, CallbackInfo ci) {
        if (hodgepodge$searchBar.isFocused()) {
            hodgepodge$searchBar.textboxKeyTyped(eventChar, eventKey);
        }
    }

    @Inject(method = "mouseClicked", at = @At(value = "TAIL"))
    private void hodgepodge$clearSearchbar(int x, int y, int mouseEvent, CallbackInfo ci) {
        hodgepodge$searchBar.mouseClicked(x, y, mouseEvent);
        if (mouseEvent == 1 && x >= hodgepodge$searchBar.xPosition
                && x < hodgepodge$searchBar.xPosition + hodgepodge$searchBar.width
                && y >= hodgepodge$searchBar.yPosition
                && y < hodgepodge$searchBar.yPosition + hodgepodge$searchBar.height) {
            hodgepodge$searchBar.setText("");
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "TAIL"))
    private void hodgepodge$renderSearchBar(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        hodgepodge$searchBar.drawTextBox();
        if (!hodgepodge$searchBar.isFocused() && hodgepodge$searchBar.getText().isEmpty()) {
            fontRendererObj.drawString("Search...", hodgepodge$searchBar.xPosition + 2, 8, -1);
        }
    }

    @Inject(method = "updateScreen", at = @At(value = "TAIL"))
    private void hodgepodge$updateElements(CallbackInfo ci) {
        hodgepodge$searchBar.updateCursorCounter();
        String searchText = hodgepodge$searchBar.getText().toLowerCase();
        if (!searchText.isEmpty()) {
            if (searchText.equals(hodgepodge$localLastSearch)) return;
            hodgepodge$lastSearch = hodgepodge$localLastSearch = searchText;

            Set<String> matches = new HashSet<>();
            for (IConfigElement<?> element : configElements) {
                if (hodgepodge$doesElementMatchSearch(element, searchText)) {
                    matches.add(element.getName());
                    matches.add(StatCollector.translateToLocal(element.getLanguageKey()));
                }
            }

            List<GuiConfigEntries.IConfigEntry> entries = new ArrayList<>();
            for (GuiConfigEntries.IConfigEntry<?> entry : hodgepodge$allEntries) {
                if (matches.contains(entry.getName())) {
                    entries.add(entry);
                }
            }

            entryList.listEntries = entries;
        } else if (!hodgepodge$localLastSearch.isEmpty()) {
            entryList.listEntries = hodgepodge$allEntries;
            hodgepodge$lastSearch = hodgepodge$localLastSearch = "";
        }
    }

    @Unique
    private boolean hodgepodge$doesElementMatchSearch(IConfigElement<?> element, String search) {
        String name = element.getName().toLowerCase();
        String lang = StatCollector.translateToLocal(element.getLanguageKey()).toLowerCase();
        boolean matches = name.contains(search) || lang.contains(search);

        if (!element.isProperty() && !matches) {
            List<IConfigElement> children = element.getChildElements();
            if (children == null) return false;
            for (IConfigElement<?> child : children) {
                if (child != null && hodgepodge$doesElementMatchSearch(child, search)) {
                    return true;
                }
            }
        }

        return matches;
    }
}
