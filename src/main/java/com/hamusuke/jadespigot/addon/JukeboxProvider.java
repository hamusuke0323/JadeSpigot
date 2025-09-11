package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TileEntityJukeBox;
import org.jetbrains.annotations.Nullable;

public enum JukeboxProvider implements StreamServerDataProvider<BlockAccessor, ItemStack> {
    INSTANCE;

    public static final MinecraftKey MC_JUKEBOX = MinecraftKey.b("jukebox");

    @Nullable
    @Override
    public ItemStack streamData(BlockAccessor accessor) {
        return ((TileEntityJukeBox) accessor.getBlockEntity()).f();
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemStack> streamCodec() {
        return ItemStack.h;
    }

    @Override
    public MinecraftKey getId() {
        return MC_JUKEBOX;
    }
}
