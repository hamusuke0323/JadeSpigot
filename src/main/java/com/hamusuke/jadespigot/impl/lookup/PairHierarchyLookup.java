package com.hamusuke.jadespigot.impl.lookup;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.hamusuke.jadespigot.JadeIdProvider;
import com.hamusuke.jadespigot.JadeRegistry;
import com.hamusuke.jadespigot.JadeSpigot;
import com.hamusuke.jadespigot.impl.PriorityStore;
import net.minecraft.core.RegistryBlockID;
import net.minecraft.resources.MinecraftKey;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class PairHierarchyLookup<T extends JadeIdProvider> implements IHierarchyLookup<T> {
    public final IHierarchyLookup<T> first;
    public final IHierarchyLookup<T> second;
    private final Cache<Pair<Class<?>, Class<?>>, List<T>> mergedCache = CacheBuilder.newBuilder().build();
    protected boolean idMapped;
    @Nullable
    protected RegistryBlockID<T> idMapper;
    protected Map<MinecraftKey, T> byKey;

    public PairHierarchyLookup(IHierarchyLookup<T> first, IHierarchyLookup<T> second) {
        this.first = first;
        this.second = second;
    }

    @SuppressWarnings("unchecked")
    public <ANY> List<ANY> getMerged(Object first, Object second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        try {
            return (List<ANY>) this.mergedCache.get(
                    Pair.of(first.getClass(), second.getClass()), () -> {
                        List<T> firstList = this.first.get(first);
                        List<T> secondList = this.second.get(second);
                        if (firstList.isEmpty()) {
                            return secondList;
                        } else if (secondList.isEmpty()) {
                            return firstList;
                        }
                        return ImmutableList.sortedCopyOf(
                                Comparator.comparingInt(JadeRegistry.INSTANCE.priorities::byValue),
                                Iterables.concat(firstList, secondList)
                        );
                    });
        } catch (ExecutionException e) {
            JadeSpigot.instance().getLogger().warning(e.toString());
        }

        return List.of();
    }

    @Override
    public void idMapped() {
        this.idMapped = true;
        this.keyed();
    }

    @Override
    public @Nullable RegistryBlockID<T> idMapper() {
        return this.idMapper;
    }

    @Override
    public void register(Class<?> clazz, T provider) {
        if (this.first.isClassAcceptable(clazz)) {
            this.first.register(clazz, provider);
        } else if (this.second.isClassAcceptable(clazz)) {
            this.second.register(clazz, provider);
        } else {
            throw new IllegalArgumentException("Class " + clazz + " is not acceptable");
        }

        if (this.byKey != null) {
            T oldProvider = this.byKey.put(provider.getId(), provider);
            if (oldProvider != provider && oldProvider != null) {
                JadeSpigot.instance().getLogger().warning(
                        "Found different provider instances with same id %s, this may cause issues: %s and %s".formatted(
                                provider.getId(),
                                oldProvider,
                                provider));
            }
        }
    }

    @Override
    public boolean isClassAcceptable(Class<?> clazz) {
        return this.first.isClassAcceptable(clazz) || this.second.isClassAcceptable(clazz);
    }

    @Override
    public List<T> get(Class<?> clazz) {
        List<T> result = this.first.get(clazz);
        if (result.isEmpty()) {
            result = this.second.get(clazz);
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return this.first.isEmpty() && this.second.isEmpty();
    }

    @Override
    public Stream<Entry<Class<?>, Collection<T>>> entries() {
        return Stream.concat(this.first.entries(), this.second.entries());
    }

    @Override
    public void invalidate() {
        first.invalidate();
        second.invalidate();
        mergedCache.invalidateAll();
    }

    @Override
    public void loadComplete(PriorityStore<MinecraftKey, JadeIdProvider> priorityStore) {
        this.first.loadComplete(priorityStore);
        this.second.loadComplete(priorityStore);
        if (this.idMapped) {
            this.idMapper = createIdMapper();
        }
    }

    @Override
    public void keyed() {
        this.byKey = Maps.newHashMap();
    }
}
