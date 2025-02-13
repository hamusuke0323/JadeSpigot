package com.hamusuke.jadespigot.impl.lookup;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.*;
import com.hamusuke.jadespigot.JadeIdProvider;
import com.hamusuke.jadespigot.JadeRegistry;
import com.hamusuke.jadespigot.JadeSpigot;
import com.hamusuke.jadespigot.impl.PriorityStore;
import net.minecraft.core.RegistryBlockID;
import net.minecraft.resources.MinecraftKey;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class HierarchyLookup<T extends JadeIdProvider> implements IHierarchyLookup<T> {
    private final Class<?> baseClass;
    private final Cache<Class<?>, List<T>> resultCache = CacheBuilder.newBuilder().build();
    private final boolean singleton;
    protected boolean idMapped;
    @Nullable
    protected RegistryBlockID<T> idMapper;
    private ListMultimap<Class<?>, T> objects = ArrayListMultimap.create();
    protected Map<MinecraftKey, T> byKey;

    public HierarchyLookup(Class<?> baseClass) {
        this(baseClass, false);
    }

    public HierarchyLookup(Class<?> baseClass, boolean singleton) {
        this.baseClass = baseClass;
        this.singleton = singleton;
    }

    @Override
    public void idMapped() {
        this.idMapped = true;
        this.keyed();
    }

    @Override
    @Nullable
    public RegistryBlockID<T> idMapper() {
        return this.idMapper;
    }

    @Override
    public void register(Class<?> clazz, T provider) {
        Preconditions.checkArgument(isClassAcceptable(clazz), "Class %s is not acceptable", clazz);
        Objects.requireNonNull(provider.getId());
        JadeRegistry.INSTANCE.priorities.put(provider);
        this.objects.put(clazz, provider);
        if (this.byKey != null) {
            var oldProvider = this.byKey.put(provider.getId(), provider);
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
        return this.baseClass.isAssignableFrom(clazz);
    }

    @Override
    public List<T> get(Class<?> clazz) {
        try {
            return this.resultCache.get(
                    clazz, () -> {
                        List<T> list = Lists.newArrayList();
                        this.getInternal(clazz, list);
                        list = ImmutableList.sortedCopyOf(
                                Comparator.comparingInt(JadeRegistry.INSTANCE.priorities::byValue),
                                list);
                        if (this.singleton && !list.isEmpty()) {
                            return ImmutableList.of(list.getFirst());
                        }
                        return list;
                    });
        } catch (ExecutionException e) {
            JadeSpigot.instance().getLogger().warning(e.toString());
        }
        return List.of();
    }

    @Override
    public void keyed() {
        this.byKey = Maps.newHashMap();
    }

    private void getInternal(Class<?> clazz, List<T> list) {
        if (clazz != this.baseClass && clazz != Object.class) {
            this.getInternal(clazz.getSuperclass(), list);
        }
        list.addAll(this.objects.get(clazz));
    }

    @Override
    public boolean isEmpty() {
        return this.objects.isEmpty();
    }

    @Override
    public Stream<Entry<Class<?>, Collection<T>>> entries() {
        return this.objects.asMap().entrySet().stream();
    }

    @Override
    public void invalidate() {
        this.resultCache.invalidateAll();
    }

    @Override
    public void loadComplete(PriorityStore<MinecraftKey, JadeIdProvider> priorityStore) {
        this.objects.asMap().forEach((clazz, list) -> {
            if (list.size() < 2) {
                return;
            }
            Set<MinecraftKey> set = Sets.newHashSetWithExpectedSize(list.size());
            for (var provider : list) {
                if (set.contains(provider.getId())) {
                    throw new IllegalStateException("Duplicate UID: %s for %s".formatted(
                            provider.getId(), list.stream()
                                    .filter(p -> p.getId().equals(provider.getId()))
                                    .map(p -> p.getClass().getName())
                                    .toList()));
                }
                set.add(provider.getId());
            }
        });

        this.objects = ImmutableListMultimap.<Class<?>, T>builder()
                .orderValuesBy(Comparator.comparingInt(priorityStore::byValue))
                .putAll(this.objects)
                .build();

        if (this.idMapped) {
            this.idMapper = createIdMapper();
        }
    }
}
