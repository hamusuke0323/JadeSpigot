package com.hamusuke.jadespigot.impl.lookup;

import com.google.common.collect.Lists;
import com.hamusuke.jadespigot.JadeIdProvider;
import com.hamusuke.jadespigot.accessors.Accessor;
import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.impl.PriorityStore;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

public class WrappedHierarchyLookup<T extends JadeIdProvider> extends HierarchyLookup<T> {
    public final List<Pair<IHierarchyLookup<T>, Function<Accessor<?>, Object>>> overrides = Lists.newArrayList();
    private boolean empty = true;

    public WrappedHierarchyLookup() {
        super(Object.class);
    }

    public static <T extends JadeIdProvider> WrappedHierarchyLookup<T> forAccessor() {
        WrappedHierarchyLookup<T> lookup = new WrappedHierarchyLookup<>();
        lookup.overrides.add(Pair.of(
                new HierarchyLookup<>(Block.class), accessor -> {
                    if (accessor instanceof BlockAccessor blockAccessor) {
                        return blockAccessor.getBlock();
                    }

                    return null;
                }));

        return lookup;
    }

    public List<T> wrappedGet(Accessor<?> accessor) {
        List<T> list = Lists.newArrayList();
        for (var override : overrides) {
            var o = override.getRight().apply(accessor);
            if (o != null) {
                list.addAll(override.getLeft().get(o));
            }
        }
        list.addAll(get(accessor.getTarget()));
        return list;
    }

    @Override
    public void register(Class<?> clazz, T provider) {
        for (var override : this.overrides) {
            if (override.getLeft().isClassAcceptable(clazz)) {
                override.getLeft().register(clazz, provider);
                this.empty = false;
                return;
            }
        }

        super.register(clazz, provider);
        this.empty = false;
    }

    @Override
    public boolean isClassAcceptable(Class<?> clazz) {
        for (var override : this.overrides) {
            if (override.getLeft().isClassAcceptable(clazz)) {
                return true;
            }
        }

        return super.isClassAcceptable(clazz);
    }

    @Override
    public void invalidate() {
        for (var override : this.overrides) {
            override.getLeft().invalidate();
        }

        super.invalidate();
    }

    @Override
    public void loadComplete(PriorityStore<MinecraftKey, JadeIdProvider> priorityStore) {
        for (var override : this.overrides) {
            override.getLeft().loadComplete(priorityStore);
        }

        super.loadComplete(priorityStore);
    }

    @Override
    public boolean isEmpty() {
        return this.empty;
    }

    @Override
    public Stream<Entry<Class<?>, Collection<T>>> entries() {
        Stream<Map.Entry<Class<?>, Collection<T>>> stream = super.entries();
        for (var override : this.overrides) {
            stream = Stream.concat(stream, override.getLeft().entries());
        }

        return stream;
    }
}
