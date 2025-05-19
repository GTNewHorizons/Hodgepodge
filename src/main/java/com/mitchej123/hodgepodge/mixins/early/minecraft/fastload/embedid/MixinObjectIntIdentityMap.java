package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.embedid;

import com.mitchej123.hodgepodge.mixins.interfaces.EmbedToggle;
import java.util.IdentityHashMap;
import java.util.List;

import net.minecraft.util.ObjectIntIdentityMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.TypeSettable;
import com.mitchej123.hodgepodge.util.EmbeddedObjectIntMap;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@Mixin(ObjectIntIdentityMap.class)
public abstract class MixinObjectIntIdentityMap implements TypeSettable, EmbedToggle {

    @Shadow
    protected IdentityHashMap<Object, Integer> field_148749_a;

    @Shadow
    protected List<Object> field_148748_b;

    @Unique
    private EmbeddedObjectIntMap<Object> hodgepodge$objectMap;
    @Unique
    private ObjectArrayList<Object> hodgepodge$objectList;

    @Override
    public void hodgepodge$setUseEmbed(boolean useEmbed) {
        hodgepodge$objectMap.setEmbed(useEmbed);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void hodgepodge$replaceCollections(CallbackInfo ci) {
        hodgepodge$objectMap = new EmbeddedObjectIntMap<>(512);
        this.field_148749_a = hodgepodge$objectMap;

        hodgepodge$objectList = new ObjectArrayList<>(512);
        this.field_148748_b = hodgepodge$objectList;
    }

    /**
     * @author ah-OOG-ah
     * @reason reduce integer boxing (ported from ASM)
     */
    @Overwrite
    public void func_148746_a(Object key, int val) {
        hodgepodge$put(key, val);
    }

    /**
     * @author ah-OOG-ah
     * @reason reduce integer boxing (ported from ASM)
     */
    @Overwrite
    public int func_148747_b(Object key) {
        return hodgepodge$get(key);
    }

    /**
     * @author ah-OOG-ah
     * @reason reduce integer boxing (ported from ASM)
     */
    @Overwrite
    public Object func_148745_a(int val) {
        return hodgepodge$getByValue(val);
    }

    /**
     * @author ah-OOG-ah
     * @reason reduce integer boxing (ported from ASM)
     */
    @Overwrite
    public boolean func_148744_b(int val) {
        return hodgepodge$contains(val);
    }

    @Unique
    public void hodgepodge$put(Object key, int value) {
        hodgepodge$objectMap.put(key, value);
        hodgepodge$objectList.ensureCapacity(value + 1);
        while (hodgepodge$objectList.size() <= value) {
            hodgepodge$objectList.add(null);
        }
        hodgepodge$objectList.set(value, key);
    }

    @Unique
    public int hodgepodge$get(Object key) {
        return hodgepodge$objectMap.getInt(key);
    }

    @Unique
    public Object hodgepodge$getByValue(int value) {
        return value >= 0 && value < this.hodgepodge$objectList.size() ? this.hodgepodge$objectList.get(value) : null;
    }

    @Unique
    public boolean hodgepodge$contains(int value) {
        return hodgepodge$getByValue(value) != null;
    }

    @Override
    public void hodgepodge$setType(Class<?> type) {
        hodgepodge$objectMap.setType(type);
    }
}
