package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizon.gtnhlib.chat.customcomponents.ChatComponentItemName;

/**
 * Fixes func_151000_E() (ItemStack#chatComponent) to return a ChatComponentItemName, which is serialized with the full
 * ItemStack and translated client-side in the correct locale. This also avoids the NBT toString roundtrip that caused
 * client crashes with complex NBT items.
 */
@Mixin(ItemStack.class)
public class MixinItemStack_FixChatLocalization {

    @Inject(method = "func_151000_E", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$fixChatLocalization(CallbackInfoReturnable<IChatComponent> cir) {
        cir.setReturnValue(new ChatComponentItemName((ItemStack) (Object) this));
    }
}
