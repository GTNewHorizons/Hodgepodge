package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
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
    @Unique
    private GuiDifficultyLockButton hodgepodge$lockButton = null;

    // remove the GameSettings.Options.DIFFICULTY from the main menu options
    @WrapOperation(
            method = "initGui",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/GuiOptions;field_146440_f:[Lnet/minecraft/client/settings/GameSettings$Options;",
                    opcode = Opcodes.GETSTATIC))
    private GameSettings.Options[] wrapOptionsArray(Operation<GameSettings.Options[]> original) {
        return new GameSettings.Options[] { GameSettings.Options.FOV };
    }

    // add the DIFFICULTY button when the world is loaded
    @Inject(method = "initGui", at = @At("RETURN"))
    private void onInitGui(CallbackInfo ci) {
        if (this.mc.theWorld != null) {
            final IWorldDifficulty pwd = (IWorldDifficulty) mc.theWorld.getWorldInfo();
            EnumDifficulty enumdifficulty = pwd.getDifficulty();
            final int x = this.width / 2 + 5;
            final int y = this.height / 6 - 12;

            this.hodgepodge$difficultyButton = new GuiButton(
                    108,
                    x,
                    y,
                    150,
                    20,
                    this.hodgepodge$getDifficultyText(enumdifficulty));
            this.buttonList.add(this.hodgepodge$difficultyButton);

            if (this.mc.isSingleplayer() && !this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
                this.hodgepodge$difficultyButton.width = this.hodgepodge$difficultyButton.getButtonWidth() - 20;
                this.hodgepodge$lockButton = new GuiDifficultyLockButton(
                        109,
                        this.hodgepodge$difficultyButton.xPosition + this.hodgepodge$difficultyButton.getButtonWidth(),
                        this.hodgepodge$difficultyButton.yPosition);
                this.buttonList.add(this.hodgepodge$lockButton);

                this.hodgepodge$lockButton.setLocked(pwd.isDifficultyLocked());
                this.hodgepodge$lockButton.enabled = !this.hodgepodge$lockButton.isLocked();
                this.hodgepodge$difficultyButton.enabled = !this.hodgepodge$lockButton.isLocked();
            } else {
                this.hodgepodge$difficultyButton.enabled = false;
            }
        }
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"))
    private void onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (mc.theWorld == null) return;

        final IWorldDifficulty pwd = (IWorldDifficulty) mc.theWorld.getWorldInfo();

        if (button.id == 108) {
            final EnumDifficulty next = EnumDifficulty.getDifficultyEnum(pwd.getDifficulty().getDifficultyId() + 1);
            pwd.setDifficulty(next);
            mc.gameSettings.difficulty = next;
            this.hodgepodge$difficultyButton.displayString = this.hodgepodge$getDifficultyText(next);
            NetworkHandler.instance.sendToServer(new MessageSetDifficulty(next, false));
        }

        if (button.id == 109) {
            mc.displayGuiScreen(
                    new GuiYesNoMultiline(
                            hodgepodge$asYesNoCallback(),
                            new ChatComponentTranslation("difficulty.lock.title").getFormattedText(),
                            new ChatComponentTranslation(
                                    "difficulty.lock.question",
                                    new ChatComponentTranslation(pwd.getDifficulty().getDifficultyResourceKey()))
                                            .getFormattedText(),
                            109));
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {

        if (id == 109 && result && this.mc.theWorld != null) {
            final IWorldDifficulty pwd = (IWorldDifficulty) mc.theWorld.getWorldInfo();
            pwd.setDifficultyLocked(true);
            NetworkHandler.instance.sendToServer(new MessageSetDifficulty(pwd.getDifficulty(), true));
        }

        this.mc.displayGuiScreen(hodgepodge$asGuiScreen());
    }

    @Unique
    public String hodgepodge$getDifficultyText(EnumDifficulty difficulty) {

        IChatComponent difficultyText = new ChatComponentText("");
        difficultyText.appendSibling(new ChatComponentTranslation("options.difficulty"));
        difficultyText.appendText(": ");
        difficultyText.appendSibling(new ChatComponentTranslation(difficulty.getDifficultyResourceKey()));
        return difficultyText.getFormattedText();
    }

    @Unique
    private GuiYesNoCallback hodgepodge$asYesNoCallback() {
        return (GuiYesNoCallback) (Object) this;
    }

    @Unique
    private GuiScreen hodgepodge$asGuiScreen() {
        return (GuiScreen) (Object) this;
    }
}
