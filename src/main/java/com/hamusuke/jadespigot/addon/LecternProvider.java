package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TileEntityLectern;

public enum LecternProvider implements StreamServerDataProvider<BlockAccessor, ItemStack> {
    INSTANCE;

    public static final MinecraftKey MC_LECTERN = MinecraftKey.b("lectern");

    @Override
    public ItemStack streamData(BlockAccessor accessor) {
        return ((TileEntityLectern) accessor.getBlockEntity()).b();
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemStack> streamCodec() {
        return ItemStack.g;
    }

    @Override
    public MinecraftKey getId() {
        return MC_LECTERN;
    }
}
