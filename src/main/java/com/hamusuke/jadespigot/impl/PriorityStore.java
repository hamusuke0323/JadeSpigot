package com.hamusuke.jadespigot.impl;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class PriorityStore<K, V> {
    private final Object2IntMap<K> priorities = new Object2IntLinkedOpenHashMap<>();
    private final Function<V, K> keyGetter;
    private final ToIntFunction<V> defaultPriorityGetter;

    public PriorityStore(ToIntFunction<V> defaultPriorityGetter, Function<V, K> keyGetter) {
        this.defaultPriorityGetter = defaultPriorityGetter;
        this.keyGetter = keyGetter;
    }

    public void put(V provider) {
        Objects.requireNonNull(provider);
        this.put(provider, defaultPriorityGetter.applyAsInt(provider));
    }

    public void put(V provider, int priority) {
        Objects.requireNonNull(provider);
        K uid = this.keyGetter.apply(provider);
        Objects.requireNonNull(uid);
        this.priorities.put(uid, priority);
    }

    public int byValue(V value) {
        return this.byKey(this.keyGetter.apply(value));
    }

    public int byKey(K id) {
        return this.priorities.getInt(id);
    }
}
