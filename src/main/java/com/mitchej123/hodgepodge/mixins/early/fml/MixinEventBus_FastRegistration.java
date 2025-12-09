package com.mitchej123.hodgepodge.mixins.early.fml;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.reflect.TypeToken;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.eventhandler.IEventListener;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mixin(value = EventBus.class, remap = false, priority = 100)
public abstract class MixinEventBus_FastRegistration {

    @Shadow
    private ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners;

    @Shadow
    private Map<Object, ModContainer> listenerOwners;

    @Shadow
    protected abstract void register(Class<?> eventType, Object target, Method method, ModContainer owner);

    /**
     * @author Alexdoru
     * @reason speed
     */
    @Overwrite
    public void register(Object target) {
        if (listeners.containsKey(target)) {
            return;
        }

        ModContainer activeModContainer = Loader.instance().activeModContainer();
        if (activeModContainer == null) {
            FMLLog.log(
                    Level.ERROR,
                    new Throwable(),
                    "Unable to determine registrant mod for %s. This is a critical error and should be impossible",
                    target);
            activeModContainer = Loader.instance().getMinecraftModContainer();
        }
        listenerOwners.put(target, activeModContainer);
        Class<?>[] supers = TypeToken.of(target.getClass()).getTypes().rawTypes().toArray(new Class<?>[0]);
        for (Method method : target.getClass().getMethods()) {
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            for (Class<?> cls : supers) {
                if (cls == Object.class) {
                    continue;
                }

                try {
                    Method real = cls.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    if (real.isAnnotationPresent(SubscribeEvent.class)) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes.length != 1) {
                            throw new IllegalArgumentException(
                                    "Method " + method
                                            + " has @SubscribeEvent annotation, but requires "
                                            + parameterTypes.length
                                            + " arguments.  Event handler methods must require a single argument.");
                        }

                        Class<?> eventType = parameterTypes[0];

                        if (!Event.class.isAssignableFrom(eventType)) {
                            throw new IllegalArgumentException(
                                    "Method " + method
                                            + " has @SubscribeEvent annotation, but takes a argument that is not an Event "
                                            + eventType);
                        }

                        register(eventType, target, method, activeModContainer);
                        break;
                    }
                } catch (NoSuchMethodException ignored) {}
            }
        }
    }

}
