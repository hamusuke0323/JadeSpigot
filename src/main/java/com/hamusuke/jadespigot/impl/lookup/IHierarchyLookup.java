package com.hamusuke.jadespigot.impl.lookup;

import com.google.common.collect.Streams;
import com.hamusuke.jadespigot.JadeIdProvider;
import com.hamusuke.jadespigot.impl.PriorityStore;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryBlockID;
import net.minecraft.resources.MinecraftKey;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Stream;

public interface IHierarchyLookup<T extends JadeIdProvider> {
    void idMapped();

    @Nullable
    RegistryBlockID<T> idMapper();

    default List<MinecraftKey> mappedIds() {
        return Streams.stream(Objects.requireNonNull(idMapper()))
                .map(JadeIdProvider::getId)
                .toList();
    }

    void register(Class<?> clazz, T provider);

    boolean isClassAcceptable(Class<?> clazz);

    default List<T> get(Object obj) {
        if (obj == null) {
            return List.of();
        }
        return get(obj.getClass());
    }

    List<T> get(Class<?> clazz);

    void keyed();

    boolean isEmpty();

    Stream<Entry<Class<?>, Collection<T>>> entries();

    void invalidate();

    void loadComplete(PriorityStore<MinecraftKey, JadeIdProvider> priorityStore);

    default RegistryBlockID<T> createIdMapper() {
        List<T> list = entries().flatMap(entry -> entry.getValue().stream()).toList();
        var idMapper = this.idMapper();
        if (idMapper == null) {
            idMapper = new RegistryBlockID<>(list.size());
        }
        for (var provider : list) {
            if (idMapper.a(provider) == Registry.a) {
                idMapper.a(provider);
            }
        }
        return idMapper;
    }
}
