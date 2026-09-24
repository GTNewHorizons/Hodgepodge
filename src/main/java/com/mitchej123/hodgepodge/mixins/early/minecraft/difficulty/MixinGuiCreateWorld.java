package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.WorldInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;

@Mixin(GuiCreateWorld.class)
public abstract class MixinGuiCreateWorld extends GuiScreen {

    @Shadow
    private boolean field_146344_y;

    @Shadow
    private boolean field_146337_w;

    @Unique
    private EnumDifficulty hodgepodge$difficulty = EnumDifficulty.NORMAL;

    @Unique
    private GuiButton hodgepodge$difficultyButton;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void addDifficultyButton(CallbackInfo ci) {
        this.hodgepodge$difficultyButton = new GuiButton(9, this.width / 2 - 75, 163, 150, 20, "");
        this.buttonList.add(this.hodgepodge$difficultyButton);
        this.hodgepodge$updateDifficultyButton();
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"))
    private void onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == 9 && button.enabled) {
            this.hodgepodge$difficulty = EnumDifficulty
                    .getDifficultyEnum(this.hodgepodge$difficulty.getDifficultyId() + 1);
        }
        this.hodgepodge$updateDifficultyButton();
    }

    @ModifyArg(
            method = "actionPerformed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;launchIntegratedServer(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/world/WorldSettings;)V"),
            index = 2)
    private WorldSettings setWorldDifficulty(WorldSettings settings) {
        ((IWorldDifficulty) (Object) settings)
                .hodgepodge$setDifficulty(this.field_146337_w ? EnumDifficulty.HARD : this.hodgepodge$difficulty);
        return settings;
    }

    @Inject(method = "func_146318_a", at = @At("TAIL"))
    private void copyDifficulty(WorldInfo info, CallbackInfo ci) {
        EnumDifficulty difficulty = ((IWorldDifficulty) info).hodgepodge$getDifficulty();
        if (difficulty != null) this.hodgepodge$difficulty = difficulty;
    }

    @Unique
    private void hodgepodge$updateDifficultyButton() {
        this.hodgepodge$difficultyButton.visible = !this.field_146344_y;
        this.hodgepodge$difficultyButton.enabled = !this.field_146337_w;
        EnumDifficulty shown = this.field_146337_w ? EnumDifficulty.HARD : this.hodgepodge$difficulty;
        this.hodgepodge$difficultyButton.displayString = I18n.format("options.difficulty") + ": "
                + I18n.format(shown.getDifficultyResourceKey());
    }
}
