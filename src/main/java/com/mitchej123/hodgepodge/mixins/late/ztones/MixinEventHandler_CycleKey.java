package com.mitchej123.hodgepodge.mixins.late.ztones;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.riciJak.Ztones.handler.EventHandler;
import com.riciJak.Ztones.handler.PacketHandler;
import com.riciJak.Ztones.item.block.ItemDecoBlocks2;
import com.riciJak.Ztones.network.ToggleMetaData;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mixin(value = EventHandler.class, remap = false)
public class MixinEventHandler_CycleKey {

    @Unique
    private static final KeyBinding hodgepodge$cycleKey = new KeyBinding(
            "key.ztonesCycle.desc",
            Keyboard.KEY_LCONTROL,
            "ZTones");

    @Inject(method = "<init>", at = @At("RETURN"))
    private void hodgepodge$registerCycleKey(CallbackInfo ci) {
        ClientRegistry.registerKeyBinding(hodgepodge$cycleKey);
    }

    /**
     * @author Armisael
     * @reason Only cancel Scrolls instead of all mouse events. Change hardbound controls to customizable.
     **/
    @Overwrite
    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        if (event.dwheel == 0 || !GameSettings.isKeyDown(hodgepodge$cycleKey)) return;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        ItemStack held = player != null ? player.getHeldItem() : null;
        if (held != null && held.getItem() instanceof ItemDecoBlocks2) {
            PacketHandler.sendPacketToServer(new ToggleMetaData(event.dwheel > 0));
            event.setCanceled(true);
        }
    }
}
