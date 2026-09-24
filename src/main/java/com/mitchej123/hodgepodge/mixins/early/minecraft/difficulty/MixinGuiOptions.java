package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumDifficulty;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.client.gui.GuiDifficultyLockButton;
import com.mitchej123.hodgepodge.client.gui.GuiYesNoMultiline;
import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;
import com.mitchej123.hodgepodge.net.MessageSetDifficulty;
import com.mitchej123.hodgepodge.net.NetworkHandler;

@Mixin(GuiOptions.class)
public abstract class MixinGuiOptions extends GuiScreen implements GuiYesNoCallback {

    @Unique
    private GuiButton hodgepodge$difficultyButton = null;

    @WrapOperation(
            method = "initGui",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/GuiOptions;field_146440_f:[Lnet/minecraft/client/settings/GameSettings$Options;",
                    opcode = Opcodes.GETSTATIC))
    private GameSettings.Options[] wrapOptionsArray(Operation<GameSettings.Options[]> original) {
        return new GameSettings.Options[] { GameSettings.Options.FOV };
    }

    @Inject(method = "initGui", at = @At("RETURN"))
    private void onInitGui(CallbackInfo ci) {
        if (this.mc.theWorld != null) {
            final IWorldDifficulty worldInfo = (IWorldDifficulty) mc.theWorld.getWorldInfo();
            EnumDifficulty difficulty = worldInfo.hodgepodge$getDifficulty();
            if (difficulty == null) difficulty = mc.theWorld.difficultySetting;
            final int x = this.width / 2 + 5;
            final int y = this.height / 6 - 12;

            this.hodgepodge$difficultyButton = new GuiButton(
                    108,
                    x,
                    y,
                    150,
                    20,
                    this.hodgepodge$getDifficultyText(difficulty));
            this.buttonList.add(this.hodgepodge$difficultyButton);

            if (this.mc.isSingleplayer() && !this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
                this.hodgepodge$difficultyButton.width = this.hodgepodge$difficultyButton.getButtonWidth() - 20;
                GuiDifficultyLockButton hodgepodge$lockButton = new GuiDifficultyLockButton(
                        109,
                        this.hodgepodge$difficultyButton.xPosition + this.hodgepodge$difficultyButton.getButtonWidth(),
                        this.hodgepodge$difficultyButton.yPosition);
                this.buttonList.add(hodgepodge$lockButton);

                hodgepodge$lockButton.setLocked(worldInfo.hodgepodge$isDifficultyLocked());
                hodgepodge$lockButton.enabled = !hodgepodge$lockButton.isLocked();
                this.hodgepodge$difficultyButton.enabled = !hodgepodge$lockButton.isLocked();
            } else {
                this.hodgepodge$difficultyButton.enabled = false;
            }
        }
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"))
    private void onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (mc.theWorld == null || !button.enabled) return;

        final IWorldDifficulty worldInfo = (IWorldDifficulty) mc.theWorld.getWorldInfo();

        if (button.id == 108) {
            final EnumDifficulty next = EnumDifficulty
                    .getDifficultyEnum(mc.theWorld.difficultySetting.getDifficultyId() + 1);
            worldInfo.hodgepodge$setDifficulty(next);
            mc.theWorld.difficultySetting = next;
            this.hodgepodge$difficultyButton.displayString = this.hodgepodge$getDifficultyText(next);
            NetworkHandler.instance.sendToServer(new MessageSetDifficulty(next, false));
        }

        if (button.id == 109) {
            mc.displayGuiScreen(
                    new GuiYesNoMultiline(
                            this,
                            new ChatComponentTranslation("difficulty.lock.title").getFormattedText(),
                            new ChatComponentTranslation(
                                    "difficulty.lock.question",
                                    new ChatComponentTranslation(
                                            worldInfo.hodgepodge$getDifficulty().getDifficultyResourceKey()))
                                                    .getFormattedText(),
                            109));
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {

        if (id == 109 && result && this.mc.theWorld != null) {
            final IWorldDifficulty worldInfo = (IWorldDifficulty) mc.theWorld.getWorldInfo();
            worldInfo.hodgepodge$setDifficultyLocked(true);
            NetworkHandler.instance.sendToServer(new MessageSetDifficulty(worldInfo.hodgepodge$getDifficulty(), true));
        }

        this.mc.displayGuiScreen(this);
    }

    @Unique
    private String hodgepodge$getDifficultyText(EnumDifficulty difficulty) {
        return I18n.format("options.difficulty") + ": " + I18n.format(difficulty.getDifficultyResourceKey());
    }
}
