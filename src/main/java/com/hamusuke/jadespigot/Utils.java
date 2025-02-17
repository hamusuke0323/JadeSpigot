package com.hamusuke.jadespigot;

import com.google.common.collect.Maps;
import com.hamusuke.jadespigot.accessors.Accessor;
import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.addon.ItemCollector;
import com.hamusuke.jadespigot.addon.ItemIterator;
import com.hamusuke.jadespigot.impl.lookup.WrappedHierarchyLookup;
import com.hamusuke.jadespigot.view.ServerExtensionProvider;
import com.hamusuke.jadespigot.view.ViewGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.IInventory;
import net.minecraft.world.IInventoryHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.EntityHorseAbstract;
import net.minecraft.world.entity.boss.EntityComplexPart;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.level.block.BlockChest;
import net.minecraft.world.level.block.entity.TileEntityChest;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Utils {
    @Nullable
    public static <A, T> T retrieveFieldFrom(A input, String fieldName, Codec<A> codec, Class<T> retType) {
        var result = codec.encode(input, JavaOps.INSTANCE, Maps.newHashMap()).result();
        if (result.isEmpty() || !(result.get() instanceof Map<?, ?> map)) {
            return null;
        }

        var o = map.get(fieldName);
        if (o == null || !o.getClass().isAssignableFrom(retType)) {
            return null;
        }

        return retType.cast(o);
    }

    public static <T> Map.Entry<MinecraftKey, List<ViewGroup<T>>> getServerExtensionData(
            Accessor<?> accessor,
            WrappedHierarchyLookup<ServerExtensionProvider<T>> lookup) {
        for (var provider : lookup.wrappedGet(accessor)) {
            List<ViewGroup<T>> groups;
            try {
                groups = provider.getGroups(accessor);
            } catch (Exception e) {
                JadeSpigot.instance().getLogger().warning(e.toString());
                continue;
            }
            if (groups != null) {
                return Map.entry(provider.getId(), groups);
            }
        }

        return null;
    }

    public static ItemCollector<?> createItemCollector(Accessor<?> accessor) {
        if (accessor.getTarget() instanceof EntityHorseAbstract) {
            return new ItemCollector<>(new ItemIterator.ContainerItemIterator(
                    o -> {
                        if (o instanceof EntityHorseAbstract horse) {
                            return horse.ct;
                        }

                        return null;
                    }, 2));
        }

        final var container = findContainer(accessor);
        if (container != null) {
            if (container instanceof TileEntityChest) {
                return new ItemCollector<>(new ItemIterator.ContainerItemIterator(
                        a -> {
                            if (a.getTarget() instanceof TileEntityChest be) {
                                if (be.m().b() instanceof BlockChest chestBlock) {
                                    var compound = BlockChest.a(
                                            chestBlock,
                                            be.m(),
                                            Objects.requireNonNull(be.i()),
                                            be.aA_(),
                                            false);
                                    if (compound != null) {
                                        return compound;
                                    }
                                }

                                return be;
                            }

                            return null;
                        }, 0));
            }

            return new ItemCollector<>(new ItemIterator.ContainerItemIterator(0));
        }

        return ItemCollector.EMPTY;
    }

    @Nullable
    public static IInventory findContainer(Accessor<?> accessor) {
        var target = accessor.getTarget();
        if (target == null && accessor instanceof BlockAccessor blockAccessor &&
                blockAccessor.getBlock() instanceof IInventoryHolder holder) {
            return holder.a(blockAccessor.getBlockState(), accessor.getLevel(), blockAccessor.getPosition());
        } else if (target instanceof IInventory container) {
            return container;
        }

        return null;
    }

    public static Entity wrapPartEntityParent(Entity target) {
        return target instanceof EntityComplexPart part ? part.a : target;
    }

    public static Entity getPartEntity(Entity parent, int index) {
        if (parent == null) {
            return null;
        }

        if (index < 0) {
            return parent;
        }

        if (parent instanceof EntityEnderDragon dragon) {
            EntityComplexPart[] parts = dragon.x();
            if (index < parts.length) {
                return parts[index];
            }
        }

        return parent;
    }
}
