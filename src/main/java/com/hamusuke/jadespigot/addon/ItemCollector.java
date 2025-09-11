package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.Accessor;
import com.hamusuke.jadespigot.view.ViewGroup;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class ItemCollector<T> {
    public static final int MAX_SIZE = 54;
    public static final ItemCollector<?> EMPTY = new ItemCollector<>(null);
    private static final Predicate<ItemStack> SHOWN = stack -> {
        if (stack.f()) {
            return false;
        }

        if (stack.a(DataComponents.q, TooltipDisplay.c).a()) {
            return false;
        }

        if (stack.d(DataComponents.p)) {
            final var customData = stack.a(DataComponents.b, CustomData.a);
            final var tag = customData.e();
            for (final var key : tag.e()) {
                if (key.toLowerCase(Locale.ENGLISH).endsWith("clear") && tag.b(key, true)) {
                    return false;
                }
            }
        }

        return true;
    };
    private final Object2IntLinkedOpenHashMap<ItemDefinition> items = new Object2IntLinkedOpenHashMap<>();
    private final ItemIterator<T> iterator;
    public long version;
    public long lastTimeFinished;
    public boolean lastTimeIsEmpty;
    public List<ViewGroup<ItemStack>> mergedResult;

    public ItemCollector(ItemIterator<T> iterator) {
        this.iterator = iterator;
    }

    public List<ViewGroup<ItemStack>> update(Accessor<?> accessor) {
        if (iterator == null) {
            return null;
        }
        T container = iterator.find(accessor);
        if (container == null) {
            return null;
        }
        long currentVersion = iterator.getVersion(container);
        long gameTime = System.currentTimeMillis();
        if (mergedResult != null && iterator.isFinished()) {
            if (version == currentVersion) {
                return mergedResult; // content not changed
            }
            if (lastTimeFinished + 250 > gameTime) {
                return mergedResult; // avoid update too frequently
            }
            iterator.reset();
        }
        AtomicInteger count = new AtomicInteger();
        iterator.populate(container, MAX_SIZE * 2).forEach(stack -> {
            count.incrementAndGet();
            if (SHOWN.test(stack)) {
                ItemDefinition def = new ItemDefinition(stack);
                items.addTo(def, stack.M());
            }
        });
        iterator.afterPopulate(count.get());
        if (mergedResult != null && !iterator.isFinished()) {
            updateCollectingProgress(mergedResult.getFirst());
            return mergedResult;
        }
        List<ItemStack> partialResult = items.object2IntEntrySet().stream().limit(MAX_SIZE).map(entry -> {
            ItemDefinition def = entry.getKey();
            return def.toStack(entry.getIntValue());
        }).toList();
        List<ViewGroup<ItemStack>> groups = List.of(updateCollectingProgress(new ViewGroup<>(partialResult)));
        if (iterator.isFinished()) {
            mergedResult = groups;
            lastTimeIsEmpty = mergedResult.getFirst().views.isEmpty();
            version = currentVersion;
            lastTimeFinished = gameTime;
            items.clear();
        }
        return groups;
    }

    protected ViewGroup<ItemStack> updateCollectingProgress(ViewGroup<ItemStack> group) {
        if (lastTimeIsEmpty && group.views.isEmpty()) {
            return group;
        }
        float progress = iterator.getCollectingProgress();
        var data = group.getExtraData();
        if (Float.isNaN(progress) || progress >= 1) {
            data.r("Collecting");
        } else {
            data.a("Collecting", progress);
        }
        return group;
    }

    public record ItemDefinition(Item item, DataComponentPatch components) {
        ItemDefinition(ItemStack stack) {
            this(stack.h(), stack.d());
        }

        public ItemStack toStack(int count) {
            ItemStack itemStack = new ItemStack(item, count);
            itemStack.b(components);
            return itemStack;
        }
    }
}
