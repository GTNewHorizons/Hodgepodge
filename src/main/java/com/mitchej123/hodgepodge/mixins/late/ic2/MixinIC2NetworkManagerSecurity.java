package com.mitchej123.hodgepodge.mixins.late.ic2;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.Common;

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.network.INetworkItemEventListener;
import ic2.core.network.NetworkManager;
import ic2.core.util.ReflectionUtil;

@Mixin(NetworkManager.class)
public abstract class MixinIC2NetworkManagerSecurity {

    @Unique
    private static final int hodgepodge$MAX_ITEM_DATA_VALUES = 1_024;

    @Shadow(remap = false)
    protected abstract void onPacketData(InputStream input, EntityPlayer player);

    @Redirect(
            method = "onPacket",
            at = @At(
                    value = "INVOKE",
                    target = "Lic2/core/network/NetworkManager;onPacketData(Ljava/io/InputStream;Lnet/minecraft/entity/player/EntityPlayer;)V",
                    remap = false),
            remap = false)
    private void hodgepodge$handlePacketSafely(NetworkManager instance, InputStream input, EntityPlayer player) {
        try {
            onPacketData(input, player);
        } catch (RuntimeException exception) {
            hodgepodge$rejectMalformedPacket(player, exception);
        }
    }

    @Redirect(
            method = "onPacketData",
            at = @At(value = "INVOKE", target = "Ljava/io/IOException;printStackTrace()V"),
            remap = false)
    private void hodgepodge$rejectMalformedPacket(IOException exception, @Local(argsOnly = true) EntityPlayer player) {
        hodgepodge$rejectMalformedPacket(player, exception);
    }

    @Redirect(
            method = "onPacketData",
            at = @At(
                    value = "INVOKE",
                    target = "Lic2/api/network/INetworkItemEventListener;onNetworkEvent(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;I)V",
                    remap = false),
            remap = false)
    private void hodgepodge$validateItemEvent(INetworkItemEventListener listener, ItemStack packetStack,
            EntityPlayer player, int event) {
        ItemStack heldStack = player.getHeldItem();
        if (heldStack != null && heldStack.getItem() == listener) {
            listener.onNetworkEvent(heldStack, player, event);
        } else {
            Common.log.warn(
                    Common.securityMarker,
                    "Rejected IC2 item event from {} for an item they are not holding",
                    player.getGameProfile());
        }
    }

    @Redirect(
            method = "onPacketData",
            at = @At(
                    value = "INVOKE",
                    target = "Lic2/api/network/INetworkClientTileEntityEventListener;onNetworkEvent(Lnet/minecraft/entity/player/EntityPlayer;I)V",
                    remap = false),
            remap = false)
    private void hodgepodge$validateTileEvent(INetworkClientTileEntityEventListener listener, EntityPlayer player,
            int event) {
        if (listener instanceof TileEntity && hodgepodge$isOpenContainerFor(player, (TileEntity) listener)) {
            listener.onNetworkEvent(player, event);
        } else {
            Common.log.warn(
                    Common.securityMarker,
                    "Rejected unauthorized IC2 tile event {} from {}",
                    event,
                    player.getGameProfile());
        }
    }

    @ModifyExpressionValue(
            method = "onPacketData",
            at = @At(value = "INVOKE", target = "Ljava/io/DataInputStream;readShort()S"),
            remap = false)
    private short hodgepodge$boundItemDataCount(short count) {
        if (count < 0 || count > hodgepodge$MAX_ITEM_DATA_VALUES) {
            throw new IllegalArgumentException("Invalid IC2 item data count: " + count);
        }
        return count;
    }

    @WrapOperation(
            method = "onPacketData",
            at = @At(
                    value = "INVOKE",
                    target = "Lic2/core/util/ReflectionUtil;setValueRecursive(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Z",
                    ordinal = 1,
                    remap = false),
            remap = false)
    private boolean hodgepodge$validateTileFieldUpdate(Object object, String fieldName, Object value,
            Operation<Boolean> original, @Local(argsOnly = true) EntityPlayer player) {
        if (!(object instanceof TileEntity) || !hodgepodge$isOpenContainerFor(player, (TileEntity) object)
                || !hodgepodge$isCompatibleFieldValue(object, fieldName, value)) {
            Common.log.warn(
                    Common.securityMarker,
                    "Rejected unauthorized or invalid IC2 tile field update '{}' from {}",
                    fieldName,
                    player.getGameProfile());
            return false;
        }
        return original.call(object, fieldName, value);
    }

    @Unique
    private static boolean hodgepodge$isOpenContainerFor(EntityPlayer player, TileEntity tile) {
        // The server-opened container is the permission token. Scan direct fields for addons such as Nuclear Control,
        // whose containers own a tile without extending IC2's ContainerBase.
        Container container = player.openContainer;
        if (container == null || container == player.inventoryContainer || !container.canInteractWith(player)) {
            return false;
        }

        for (Class<?> type = container.getClass(); type != null
                && type != Container.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                try {
                    field.setAccessible(true);
                    if (field.get(container) == tile) return true;
                } catch (IllegalAccessException | SecurityException ignored) {
                    // An inaccessible field cannot prove that this container belongs to the target tile.
                }
            }
        }
        return false;
    }

    @Unique
    private static boolean hodgepodge$isCompatibleFieldValue(Object object, String fieldName, Object value) {
        Field field = ReflectionUtil.getFieldRecursive(object.getClass(), fieldName);
        if (field == null) return false;

        Class<?> type = field.getType();
        if (type.isEnum() && value instanceof Integer) {
            int ordinal = (Integer) value;
            return ordinal >= 0 && ordinal < type.getEnumConstants().length;
        }
        if (!hodgepodge$isAssignable(type, value)) return false;

        // IC2 mutates existing ItemStacks during field sync, so mutable stacks must remain server-authoritative.
        if (ItemStack.class.isAssignableFrom(type)) {
            try {
                field.setAccessible(true);
                return ItemStack.areItemStacksEqual((ItemStack) field.get(object), (ItemStack) value);
            } catch (IllegalAccessException | SecurityException exception) {
                return false;
            }
        }

        if (type.isArray() && value != null) {
            try {
                field.setAccessible(true);
                Object current = field.get(object);
                // Preserve fixed parallel-array shapes, notably IC2 wall and cable retexture data.
                return current == null || Array.getLength(current) == Array.getLength(value);
            } catch (IllegalAccessException | SecurityException exception) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private static boolean hodgepodge$isAssignable(Class<?> type, Object value) {
        if (value == null) return !type.isPrimitive();
        if (!type.isPrimitive()) return type.isInstance(value);
        return (type == boolean.class && value instanceof Boolean) || (type == byte.class && value instanceof Byte)
                || (type == short.class && value instanceof Short)
                || (type == int.class && value instanceof Integer)
                || (type == long.class && value instanceof Long)
                || (type == float.class && value instanceof Float)
                || (type == double.class && value instanceof Double)
                || (type == char.class && value instanceof Character);
    }

    @Unique
    private static void hodgepodge$rejectMalformedPacket(EntityPlayer player, Exception exception) {
        Common.log.warn(
                Common.securityMarker,
                "Rejected malformed IC2 network packet from {}: {}",
                player.getGameProfile(),
                exception.toString());
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).playerNetServerHandler.kickPlayerFromServer("Malformed IC2 network packet");
        }
    }
}
