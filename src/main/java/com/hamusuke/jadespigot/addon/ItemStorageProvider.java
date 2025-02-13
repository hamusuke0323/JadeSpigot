package com.hamusuke.jadespigot.addon;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hamusuke.jadespigot.JadeRegistry;
import com.hamusuke.jadespigot.JadeSpigot;
import com.hamusuke.jadespigot.Utils;
import com.hamusuke.jadespigot.accessors.Accessor;
import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.accessors.EntityAccessor;
import com.hamusuke.jadespigot.providers.ServerDataProvider;
import com.hamusuke.jadespigot.view.ServerExtensionProvider;
import com.hamusuke.jadespigot.view.ViewGroup;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.ChestLock;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TileEntityContainer;
import net.minecraft.world.level.block.entity.TileEntityEnderChest;
import net.minecraft.world.level.block.entity.TileEntityFurnace;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftInventory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class ItemStorageProvider<T extends Accessor<?>> implements ServerDataProvider<T> {
    public static final Cache<Object, ItemCollector<?>> targetCache = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(
            60,
            TimeUnit.SECONDS).build();
    private static final StreamCodec<RegistryFriendlyByteBuf, Entry<MinecraftKey, List<ViewGroup<ItemStack>>>> STREAM_CODEC = ViewGroup.listCodec(
            ItemStack.g);

    public static final MinecraftKey UNIVERSAL_ITEM_STORAGE = MinecraftKey.b("item_storage");

    public static ForBlock getBlock() {
        return ForBlock.INSTANCE;
    }

    public static ForEntity getEntity() {
        return ForEntity.INSTANCE;
    }

    public static class ForBlock extends ItemStorageProvider<BlockAccessor> {
        private static final ForBlock INSTANCE = new ForBlock();
    }

    public static class ForEntity extends ItemStorageProvider<EntityAccessor> {
        private static final ForEntity INSTANCE = new ForEntity();
    }

    public static void putData(Accessor<?> accessor) {
        var tag = accessor.getServerData();
        var target = accessor.getTarget();
        var player = accessor.getPlayer();
        var entry = Utils.getServerExtensionData(accessor, JadeRegistry.INSTANCE.itemStorageProviders);

        if (entry != null) {
            var groups = entry.getValue();
            for (var group : groups) {
                if (group.views.size() > ItemCollector.MAX_SIZE) {
                    group.views = group.views.subList(0, ItemCollector.MAX_SIZE);
                }
            }

            tag.a(UNIVERSAL_ITEM_STORAGE.toString(), accessor.encodeAsNbt(STREAM_CODEC, entry));
            return;
        }

        if (target instanceof RandomizableContainer containerEntity && containerEntity.aw_() != null) {
            tag.a("Loot", true);
        } else if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR && target instanceof TileEntityContainer te) {
            if (te.d != ChestLock.a) {
                tag.a("Locked", true);
            }
        }
    }

    @Override
    public void appendServerData(NBTTagCompound tag, T accessor) {
        if (accessor.getTarget() instanceof TileEntityFurnace) {
            return;
        }

        putData(accessor);
    }

    @Override
    public MinecraftKey getId() {
        return UNIVERSAL_ITEM_STORAGE;
    }

    public enum Extension implements ServerExtensionProvider<ItemStack> {
        INSTANCE;

        public static final MinecraftKey UNIVERSAL_ITEM_STORAGE_DEFAULT = MinecraftKey.b("item_storage.default");

        @Override
        public MinecraftKey getId() {
            return UNIVERSAL_ITEM_STORAGE_DEFAULT;
        }

        @Nullable
        @Override
        public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
            Object target = accessor.getTarget();

            switch (target) {
                case null -> {
                    return Utils.createItemCollector(accessor).update(accessor);
                }
                case RandomizableContainer te when te.aw_() != null -> {
                    return null;
                }
                case ContainerEntity containerEntity when containerEntity.v() != null -> {
                    return null;
                }
                default -> {
                }
            }

            var player = accessor.getPlayer();
            if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR && target instanceof TileEntityContainer te) {
                if (te.d != ChestLock.a) {
                    return null;
                }
            }

            if (target instanceof TileEntityEnderChest) {
                var inventory = ((CraftInventory) player.getEnderChest()).getInventory();
                return new ItemCollector<>(new ItemIterator.ContainerItemIterator($ -> inventory, 0)).update(
                        accessor
                );
            }

            ItemCollector<?> itemCollector;
            try {
                itemCollector = targetCache.get(target, () -> Utils.createItemCollector(accessor));
            } catch (ExecutionException e) {
                JadeSpigot.instance().getLogger().warning(e.toString());
                return null;
            }

            return itemCollector.update(accessor);
        }
    }
}
