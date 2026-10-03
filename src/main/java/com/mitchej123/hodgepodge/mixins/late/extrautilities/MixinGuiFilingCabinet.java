package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.rwtema.extrautils.gui.ContainerFilingCabinet;
import com.rwtema.extrautils.gui.GuiFilingCabinet;

import cpw.mods.fml.common.Loader;

@Mixin(GuiFilingCabinet.class)
public abstract class MixinGuiFilingCabinet extends GuiContainer {

    @Unique
    private static final ResourceLocation hodgepodge$backgroundTexture = new ResourceLocation(
            "textures/gui/container/creative_inventory/tab_items.png");

    @Unique
    private static final ResourceLocation hodgepodge$inventoryTexture = new ResourceLocation(
            "textures/gui/container/generic_54.png");

    @Unique
    private static final ResourceLocation hodgepodge$scrollTexture = new ResourceLocation(
            "textures/gui/container/creative_inventory/tabs.png");

    @Shadow(remap = false)
    private boolean isScrolling;

    @Shadow(remap = false)
    public abstract void sortItems();

    @Shadow(remap = false)
    private int numItems;

    @Unique
    private int hodgepodge$scroll;

    @Unique
    private int hodgepodge$rows;

    @Unique
    private int hodgepodge$maxScroll;

    @Unique
    private List<Slot> hodgepodge$sortedSlots;

    protected MixinGuiFilingCabinet(Container container) {
        super(container);
    }

    @Override
    public void initGui() {
        int padding = Loader.isModLoaded("NotEnoughItems") ? 42 : 0;
        hodgepodge$rows = Math.min(
                (inventorySlots.inventorySlots.size() - 36) / 9,
                Math.max(3, (height - 115 - padding) / 18));
        xSize = 195;
        ySize = 115 + hodgepodge$rows * 18;
        super.initGui();
        int unusedSpace = height - ySize;
        guiTop = (int) Math.floor(unusedSpace / (unusedSpace < 0 ? 3.8F : 2.0F));
        isScrolling = false;
        sortItems();
    }

    @Inject(method = "sortItems", at = @At("HEAD"), cancellable = true, remap = false)
    private void hodgepodge$reuseSortedSlots(CallbackInfo ci) {
        if (hodgepodge$sortedSlots != null && !ContainerFilingCabinet.updated) {
            hodgepodge$layoutRows(hodgepodge$sortedSlots);
            ci.cancel();
        }
    }

    @Redirect(
            method = "sortItems",
            at = @At(value = "INVOKE", target = "Ljava/util/Collections;sort(Ljava/util/List;Ljava/util/Comparator;)V"),
            remap = false)
    private void hodgepodge$sortCachedTooltips(List<Slot> slots, Comparator<Slot> comparator) {
        Map<Slot, String> keys = new IdentityHashMap<>();
        EntityClientPlayerMP player = mc.thePlayer;
        boolean sneaking = player.movementInput.sneak;
        try {
            player.movementInput.sneak = false;
            for (Slot slot : slots) {
                ItemStack stack = slot.getStack();
                if (stack != null) {
                    keys.put(slot, String.join("\n", stack.getTooltip(player, true)) + "\n");
                }
            }
        } finally {
            player.movementInput.sneak = sneaking;
        }
        slots.sort(Comparator.comparing(keys::get, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    @Inject(
            method = "sortItems",
            at = @At(value = "INVOKE", target = "Lcom/rwtema/extrautils/gui/GuiFilingCabinet;getStartSlot()I"),
            cancellable = true,
            remap = false)
    private void hodgepodge$cacheSortedSlots(CallbackInfo ci, @Local List<Slot> slots) {
        if (hodgepodge$rows == 0) return;
        slots.addAll(inventorySlots.inventorySlots.subList(slots.size(), inventorySlots.inventorySlots.size() - 36));
        hodgepodge$sortedSlots = slots;
        hodgepodge$layoutRows(slots);
        ContainerFilingCabinet.updated = false;
        ci.cancel();
    }

    @Unique
    private void hodgepodge$layoutRows(List<Slot> slots) {
        hodgepodge$maxScroll = Math.max(0, (numItems + 8) / 9 - hodgepodge$rows);
        hodgepodge$scroll = Math.max(0, Math.min(hodgepodge$scroll, hodgepodge$maxScroll));
        int first = hodgepodge$scroll * 9;
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (i >= first && i < first + hodgepodge$rows * 9) {
                slot.xDisplayPosition = 9 + (i - first) % 9 * 18;
                slot.yDisplayPosition = 18 + (i - first) / 9 * 18;
            } else {
                slot.xDisplayPosition = Integer.MIN_VALUE;
                slot.yDisplayPosition = Integer.MIN_VALUE;
            }
        }
        int playerStart = inventorySlots.inventorySlots.size() - 36;
        for (int i = 0; i < 36; i++) {
            inventorySlots.getSlot(playerStart + i).yDisplayPosition = ySize - 83 + (i < 27 ? i / 9 * 18 : 58);
        }
    }

    @Inject(method = "drawGuiContainerBackgroundLayer", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$drawBackground(float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(hodgepodge$backgroundTexture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 195, 18);
        for (int row = 0; row < hodgepodge$rows; row++) {
            drawTexturedModalRect(guiLeft, guiTop + 18 + row * 18, 0, 18, 195, 18);
        }
        mc.getTextureManager().bindTexture(hodgepodge$inventoryTexture);
        int inventoryTop = guiTop + 17 + hodgepodge$rows * 18;
        drawTexturedModalRect(guiLeft, inventoryTop, 0, 125, 169, 97);
        func_152125_a(guiLeft + 169, inventoryTop, 169, 125, 1, 97, 19, 97, 256, 256);
        drawTexturedModalRect(guiLeft + 188, inventoryTop, 169, 125, 7, 97);
        int range = hodgepodge$maxScroll;
        int thumbY = range == 0 ? 0 : hodgepodge$scroll * (hodgepodge$rows * 18 - 17) / range;
        mc.getTextureManager().bindTexture(hodgepodge$scrollTexture);
        drawTexturedModalRect(guiLeft + 175, guiTop + 18 + thumbY, range == 0 ? 244 : 232, 0, 12, 15);
        ci.cancel();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRendererObj.drawString("Filing Cabinet", 8, 6, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 93, 0x404040);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$mouseClicked(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (button == 0 && hodgepodge$maxScroll > 0
                && mouseX >= guiLeft + 175
                && mouseX <= guiLeft + 187
                && mouseY >= guiTop + 18
                && mouseY <= guiTop + 16 + hodgepodge$rows * 18) {
            isScrolling = true;
            hodgepodge$dragScroll(mouseY);
        } else {
            super.mouseClicked(mouseX, mouseY, button);
        }
        ci.cancel();
    }

    @Inject(method = "mouseClickMove", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$dragScrollbar(int mouseX, int mouseY, int button, long elapsed, CallbackInfo ci) {
        if (isScrolling) {
            hodgepodge$dragScroll(mouseY);
        } else {
            super.mouseClickMove(mouseX, mouseY, button, elapsed);
        }
        ci.cancel();
    }

    @Unique
    private void hodgepodge$dragScroll(int mouseY) {
        int y = mouseY - guiTop - 18;
        int height = hodgepodge$rows * 18 - 2;
        if (y >= 0 && y <= height) {
            hodgepodge$scroll = (y * 2 * hodgepodge$maxScroll / height + 1) / 2;
            sortItems();
        }
    }

    @Inject(method = "mouseMovedOrUp", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$releaseControl(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (button == 0 && isScrolling) {
            isScrolling = false;
            ci.cancel();
        }
    }

    @Inject(method = "handleMouseInput", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$handleMouseInput(CallbackInfo ci) {
        int wheel = Mouse.getEventDWheel();
        if (wheel == 0) {
            super.handleMouseInput();
        } else {
            hodgepodge$scroll -= Integer.signum(wheel) * Math.max(1, hodgepodge$rows / 6);
            sortItems();
        }
        ci.cancel();
    }

}
