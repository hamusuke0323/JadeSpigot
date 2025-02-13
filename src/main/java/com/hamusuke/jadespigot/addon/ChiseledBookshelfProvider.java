package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;

public enum ChiseledBookshelfProvider implements StreamServerDataProvider<BlockAccessor, ItemStack> {
    INSTANCE;

    public static final MinecraftKey MC_CHISELED_BOOKSHELF = MinecraftKey.b("chiseled_bookshelf");

    @Override
    public ItemStack streamData(BlockAccessor accessor) {
        int slot = ((ChiseledBookShelfBlock) accessor.getBlock()).a(accessor.getHitResult(), accessor.getBlockState()).orElse(-1);
        if (slot == -1) {
            return null;
        }

        return ((ChiseledBookShelfBlockEntity) accessor.getBlockEntity()).a(slot);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemStack> streamCodec() {
        return ItemStack.g;
    }

    @Override
    public MinecraftKey getId() {
        return MC_CHISELED_BOOKSHELF;
    }
}
