package com.mitchej123.hodgepodge.core.util;

/*
 * Copyright (c) 2021, glee8e, Code Chicken.
 *
 * This file is originally part of NotEnoughtItem by Code Chicken.
 * It is adapted to implement the standard Map interface by glease
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import com.google.common.collect.Iterators;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

/**
 * A map class for ItemStack keys with wildcard damage/NBT. Optimised for lookup.
 * <p>
 * This map does NOT support null values or null keys! null will be silently ignored!
 * <p>
 * Originally created by CodeChicken for NotEnoughItems. Adapted to {@code Map<ItemStack, T>} interface by glee8e
 * <p>
 * Edited later to ignore NBT tags to match vanilla furnace recipe map behavior.
 *
 * @author CodeChiken
 * @author glee8e
 */
public final class ItemStackMap<T> extends AbstractMap<ItemStack, T> {
    private final HashMap<Item, DetailMap> itemMap = new HashMap<>();
    private int size;

    static ItemStack wildcard(Item item) {
        return new ItemStack(item, 1, WILDCARD_VALUE);
    }

    @Override
    public T get(Object key) {
        if (!(key instanceof ItemStack)) return null;
        ItemStack stack = ((ItemStack) key);
        if (stack.getItem() == null) return null;
        DetailMap map = itemMap.get(stack.getItem());
        return map == null ? null : map.get(stack);
    }

    @Override
    public T put(ItemStack key, T value) {
        if (key == null || key.getItem() == null || value == null)
            return null;
        return itemMap.computeIfAbsent(key.getItem(), k -> new DetailMap()).put(key, value);
    }

    @Override
    public T remove(Object key) {
        if (!(key instanceof ItemStack)) return null;
        ItemStack stack = ((ItemStack) key);
        if (stack.getItem() == null) return null;
        DetailMap map = itemMap.get(stack.getItem());
        return map == null ? null : map.remove(stack);
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof ItemStack)) return false;
        ItemStack stack = ((ItemStack) key);
        if (stack.getItem() == null) return false;
        DetailMap map = itemMap.get(stack.getItem());
        return map != null && map.get(stack) != null;
    }

    @Override
    public void clear() {
        itemMap.clear();
        size = 0;
    }

    @Override
    @Nonnull
    public Set<Map.Entry<ItemStack, T>> entrySet() {
        return new SetView();
    }

    private static class DetailIter<T> implements Iterator<Map.Entry<ItemStack, T>> {
        private final Item owner;
        private final ItemStackMap<T>.DetailMap backing;
        @Nullable
        private final Iterator<Map.Entry<Integer, T>> damageIter;
        private DetailIterState state = DetailIterState.NOT_STARTED;
        private boolean removed = false;
        private DetailIter(Map.Entry<Item, ItemStackMap<T>.DetailMap> input) {
            this.owner = input.getKey();
            this.backing = input.getValue();
            damageIter = backing.damageMap != null ? backing.damageMap.entrySet().iterator() : null;
        }

        private DetailIterState nextState() {
            switch (state) {
                case NOT_STARTED:
                    if (backing.hasWildcard) return DetailIterState.WILDCARD;
                case WILDCARD:
                case DAMAGE:
                    if (damageIter != null && damageIter.hasNext()) return DetailIterState.DAMAGE;
                case DONE:
                    return DetailIterState.DONE;
                default:
                    throw new IllegalStateException("Unexpected value: " + state);
            }
        }

        @Override
        public void remove() {
            if (removed)
                throw new IllegalStateException("remove() called twice");
            state.remove(this);
            removed = true;
        }

        @Override
        public boolean hasNext() {
            return nextState() != DetailIterState.DONE;
        }

        @Override
        public Map.Entry<ItemStack, T> next() {
            DetailIterState nextState = nextState();
            if (nextState == DetailIterState.DONE)
                throw new NoSuchElementException();
            state = nextState;
            removed = false;
            return nextState.get(this);
        }

        private enum DetailIterState {
            NOT_STARTED {
                @Override
                <T> Map.Entry<ItemStack, T> get(DetailIter<T> iter) {
                    throw new AssertionError("Should not call get on NOT_STARTED");
                }

                @Override
                <T> void remove(DetailIter<T> iter) {
                    throw new IllegalStateException("next() never called");
                }
            },
            WILDCARD {
                @Override
                <T> Map.Entry<ItemStack, T> get(DetailIter<T> iter) {
                    return new Map.Entry<ItemStack, T>() {
                        private final ItemStack key = wildcard(iter.owner);
                        @Override
                        public ItemStack getKey() {
                            return key;
                        }

                        @Override
                        public T getValue() {
                            return iter.backing.wildcard;
                        }

                        @Override
                        public T setValue(T value) {
                            return iter.backing.wildcard = value;
                        }
                    };
                }

                @Override
                <T> void remove(DetailIter<T> iter) {
                    assert iter.backing.hasWildcard;
                    iter.backing.wildcard = null;
                    iter.backing.hasWildcard = false;
                }
            },
            DAMAGE {
                @Override
                <T> Map.Entry<ItemStack, T> get(DetailIter<T> iter) {
                    assert iter.damageIter != null;
                    Map.Entry<Integer, T> entry = iter.damageIter.next();
                    return new ItemStackEntry<>(new ItemStack(iter.owner, 1, entry.getKey()), entry);
                }

                @Override
                <T> void remove(DetailIter<T> iter) {
                    assert iter.damageIter != null;
                    iter.damageIter.remove();
                }
            },
            DONE {
                @Override
                <T> Map.Entry<ItemStack, T> get(DetailIter<T> iter) {
                    throw new AssertionError("Should not call get on DONE");
                }

                @Override
                <T> void remove(DetailIter<T> iter) {
                    throw new AssertionError("Should not call remove on DONE");
                }
            };

            abstract <T> Map.Entry<ItemStack, T> get(DetailIter<T> iter);

            abstract <T> void remove(DetailIter<T> iter);

            private static class ItemStackEntry<T> implements Map.Entry<ItemStack, T> {
                private final ItemStack key;
                private final Map.Entry<?, T> entry;

                public ItemStackEntry(ItemStack key, Map.Entry<?, T> entry) {
                    this.key = key;
                    this.entry = entry;
                }

                @Override
                public ItemStack getKey() {
                    return key;
                }

                @Override
                public T getValue() {
                    return entry.getValue();
                }

                @Override
                public T setValue(T value) {
                    return entry.setValue(value);
                }
            }
        }
    }

    private class DetailMap {
        private boolean hasWildcard;
        private T wildcard;
        private HashMap<Integer, T> damageMap;
        private int size;

        public T get(ItemStack key) {
            if (wildcard != null)
                return wildcard;

            if (damageMap != null) {
                final T ret = damageMap.get(key.getItemDamage());
                if (ret != null) return ret;
            }

            return null;
        }

        public T put(ItemStack key, T value) {
            try {
                if (key.getItemDamage() == WILDCARD_VALUE) {
                    T ret = wildcard;
                    wildcard = value;
                    hasWildcard = true;
                    return ret;
                } else {
                    if (damageMap == null) damageMap = new HashMap<>();
                    return damageMap.put(key.getItemDamage(), value);
                }
            } finally {
                updateSize();
            }
        }

        public T remove(ItemStack key) {
            try {
                if (key.getItemDamage() == WILDCARD_VALUE) {
                    T ret = wildcard;
                    wildcard = null;
                    hasWildcard = false;
                    return ret;
                } else {
                    return damageMap != null ? damageMap.remove(key.getItemDamage()) : null;
                }
            } finally {
                updateSize();
            }
        }

        private void updateSize() {
            int newSize = (hasWildcard ? 1 : 0) +
                (damageMap != null ? damageMap.size() : 0);

            if (newSize != size) {
                ItemStackMap.this.size += newSize - size;
                size = newSize;
            }
        }
    }

    private class SetView extends AbstractSet<Map.Entry<ItemStack, T>> {
        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) return false;
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            return java.util.Objects.equals(get(entry.getKey()), entry.getValue());
        }

        @Override
        public boolean add(Map.Entry<ItemStack, T> itemStackTEntry) {
            return itemStackTEntry.getKey() != null && itemStackTEntry.getValue() != null &&
                put(itemStackTEntry.getKey(), itemStackTEntry.getValue()) == null;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) return false;
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            return ItemStackMap.this.remove(entry.getKey(), entry.getValue());
        }

        @Nonnull
        @Override
        public Iterator<Map.Entry<ItemStack, T>> iterator() {
            return Iterators.concat(Iterators.transform(itemMap.entrySet().iterator(), DetailIter::new));
        }

        @Override
        public int size() {
            return size;
        }
    }
}