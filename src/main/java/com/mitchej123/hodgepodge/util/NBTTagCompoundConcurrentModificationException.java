package com.mitchej123.hodgepodge.util;

import java.util.*;

import net.minecraft.nbt.NBTTagCompound;

import codechicken.nei.util.NBTJson;

public class NBTTagCompoundConcurrentModificationException extends ConcurrentModificationException {

    private final Deque<String> keyChain = new ArrayDeque<>();
    private final String source;
    private Object fullTag;

    public NBTTagCompoundConcurrentModificationException(ConcurrentModificationException cause, Object source) {
        super(cause);
        this.source = toString(source);
        fullTag = source;
    }

    public void addKeyPath(String key) {
        keyChain.addFirst(key);
    }

    public void setFullTag(Object fullTag) {
        this.fullTag = fullTag;
    }

    @Override
    public String getMessage() {
        return String.format(
                "Keys: %s. Source tag: %s. Full tag: %s",
                String.join("...", keyChain),
                source,
                toString(fullTag));
    }

    private static String toString(Object source) {
        if (source == null) return "null";
        for (int i = 0; i < 10; i++) {
            try {
                return source instanceof NBTTagCompound ? NBTJson.toJson((NBTTagCompound) source) : source.toString();

            } catch (ConcurrentModificationException ignored) {}
        }
        return "~~failed to serialize~~";
    }
}
