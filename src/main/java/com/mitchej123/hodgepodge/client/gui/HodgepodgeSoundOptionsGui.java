package com.mitchej123.hodgepodge.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.mitchej123.hodgepodge.client.handlers.ReloadSoundsGui;
import com.mitchej123.hodgepodge.config.SoundConfig;
import com.mitchej123.hodgepodge.config.SoundConfig.Tristate;

import cpw.mods.fml.common.Loader;

public class HodgepodgeSoundOptionsGui extends GuiScreen {

    private static final int HRTF_BUTTON_ID = 0;
    private static final int LIMITER_BUTTON_ID = 1;
    private static final int REVERB_BUTTON_ID = 2;
    private static final int DOWNMIX_BUTTON_ID = 3;
    private static final int SPATIALIZE_BUTTON_ID = 4;
    private static final int RELOAD_BUTTON_ID = 5;
    private static final int DONE_BUTTON_ID = 200;

    private final GuiScreen parent;

    public HodgepodgeSoundOptionsGui(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        int left = width / 2 - 155;
        int top = height / 6 + 24;
        boolean deviceTweaksAvailable = Loader.isModLoaded("lwjgl3ify");

        GuiButton hrtf = new GuiButton(HRTF_BUTTON_ID, left, top, 150, 20, hrtfText());
        GuiButton limiter = new GuiButton(LIMITER_BUTTON_ID, left + 160, top, 150, 20, limiterText());
        hrtf.enabled = deviceTweaksAvailable;
        limiter.enabled = deviceTweaksAvailable;
        buttonList.add(hrtf);
        buttonList.add(limiter);
        buttonList.add(new GuiButton(REVERB_BUTTON_ID, left, top + 24, 310, 20, reverbText()));
        buttonList.add(new GuiButton(DOWNMIX_BUTTON_ID, left, top + 48, 150, 20, downmixText()));
        GuiButton spatialize = new GuiButton(SPATIALIZE_BUTTON_ID, left + 160, top + 48, 150, 20, spatializeText());
        spatialize.enabled = deviceTweaksAvailable;
        buttonList.add(spatialize);

        buttonList.add(
                new GuiButton(
                        RELOAD_BUTTON_ID,
                        left,
                        height / 6 + 168,
                        150,
                        20,
                        I18n.format("hodgepodge.soundsmenu.refreshsounds")));
        buttonList.add(new GuiButton(DONE_BUTTON_ID, left + 160, height / 6 + 168, 150, 20, I18n.format("gui.done")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case HRTF_BUTTON_ID -> {
                SoundConfig.hrtf = next(SoundConfig.hrtf);
                button.displayString = hrtfText();
                save();
            }
            case LIMITER_BUTTON_ID -> {
                SoundConfig.outputLimiter = next(SoundConfig.outputLimiter);
                button.displayString = limiterText();
                save();
            }
            case REVERB_BUTTON_ID -> {
                SoundConfig.environmentalReverb = !SoundConfig.environmentalReverb;
                button.displayString = reverbText();
                save();
            }
            case DOWNMIX_BUTTON_ID -> {
                SoundConfig.downmixStereoSounds = !SoundConfig.downmixStereoSounds;
                button.displayString = downmixText();
                save();
            }
            case SPATIALIZE_BUTTON_ID -> {
                SoundConfig.spatializeStereoSounds = !SoundConfig.spatializeStereoSounds;
                button.displayString = spatializeText();
                save();
            }
            case RELOAD_BUTTON_ID -> ReloadSoundsGui.reloadSounds();
            case DONE_BUTTON_ID -> mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            mc.displayGuiScreen(parent);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(
                fontRendererObj,
                I18n.format("hodgepodge.soundsmenu.enhancements.title"),
                width / 2,
                15,
                0xFFFFFF);
        if (!Loader.isModLoaded("lwjgl3ify")) {
            drawCenteredString(
                    fontRendererObj,
                    I18n.format("hodgepodge.soundsmenu.enhancements.lwjgl3ify"),
                    width / 2,
                    height / 6 + 108,
                    0xA0A0A0);
        }
        drawCenteredString(
                fontRendererObj,
                I18n.format("hodgepodge.soundsmenu.enhancements.reload"),
                width / 2,
                height / 6 + 120,
                0xA0A0A0);
        super.drawScreen(mouseX, mouseY, partialTicks);

        for (Object entry : buttonList) {
            if (entry instanceof GuiButton button && button.func_146115_a()) {
                String tooltip = tooltip(button.id);
                if (tooltip != null) {
                    List<String> lines = fontRendererObj.listFormattedStringToWidth(I18n.format(tooltip), 300);
                    drawHoveringText(lines, mouseX, mouseY, fontRendererObj);
                }
                break;
            }
        }
    }

    private static Tristate next(Tristate value) {
        Tristate[] values = Tristate.values();
        return values[(value.ordinal() + 1) % values.length];
    }

    private static void save() {
        ConfigurationManager.save(SoundConfig.class);
    }

    private static String hrtfText() {
        return optionText("hodgepodge.soundsmenu.hrtf", SoundConfig.hrtf);
    }

    private static String limiterText() {
        return optionText("hodgepodge.soundsmenu.output_limiter", SoundConfig.outputLimiter);
    }

    private static String reverbText() {
        return booleanOptionText("hodgepodge.soundsmenu.environmental_reverb", SoundConfig.environmentalReverb);
    }

    private static String downmixText() {
        return booleanOptionText("hodgepodge.soundsmenu.downmix_stereo", SoundConfig.downmixStereoSounds);
    }

    private static String spatializeText() {
        return booleanOptionText("hodgepodge.soundsmenu.spatialize_stereo", SoundConfig.spatializeStereoSounds);
    }

    private static String booleanOptionText(String name, boolean value) {
        return I18n.format(
                "hodgepodge.soundsmenu.option",
                I18n.format(name),
                I18n.format(value ? "options.on" : "options.off"));
    }

    private static String optionText(String name, Tristate value) {
        String valueKey = switch (value) {
            case DEFAULT -> "hodgepodge.soundsmenu.default";
            case ON -> "options.on";
            case OFF -> "options.off";
        };
        return I18n.format("hodgepodge.soundsmenu.option", I18n.format(name), I18n.format(valueKey));
    }

    private static String tooltip(int buttonId) {
        return switch (buttonId) {
            case HRTF_BUTTON_ID -> "hodgepodge.soundsmenu.hrtf.tooltip";
            case LIMITER_BUTTON_ID -> "hodgepodge.soundsmenu.output_limiter.tooltip";
            case REVERB_BUTTON_ID -> "hodgepodge.soundsmenu.environmental_reverb.tooltip";
            case DOWNMIX_BUTTON_ID -> "hodgepodge.soundsmenu.downmix_stereo.tooltip";
            case SPATIALIZE_BUTTON_ID -> "hodgepodge.soundsmenu.spatialize_stereo.tooltip";
            default -> null;
        };
    }
}
