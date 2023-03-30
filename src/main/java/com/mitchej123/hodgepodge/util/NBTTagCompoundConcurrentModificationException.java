package com.mitchej123.hodgepodge.util;

import java.util.*;

public class NBTTagCompoundConcurrentModificationException extends ConcurrentModificationException {

    private final Deque<String> keyChain = new ArrayDeque<>();
    private final String source;

    public NBTTagCompoundConcurrentModificationException(ConcurrentModificationException cause, Object source) {
        super(cause);
        String source1 = "~~failed to serialize~~";
        for (int i = 0; i < 10; i++) {
            try {
                source1 = source.toString();
            } catch (ConcurrentModificationException ignored) {}
        }
        this.source = source1;
    }

    public void addKeyPath(String key) {
        keyChain.addFirst(key);
    }

    @Override
    public String getMessage() {
        return String.format("Keys: %s. Source tag: %s", String.join("...", keyChain), source);
    }
}
