package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.addon.FurnaceProvider.Data;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TileEntityFurnace;

import java.util.List;

public enum FurnaceProvider implements StreamServerDataProvider<BlockAccessor, Data> {
    INSTANCE;

    public static final MinecraftKey MC_FURNACE = MinecraftKey.b("furnace");

    @Override
    public Data streamData(BlockAccessor accessor) {
        var furnace = (TileEntityFurnace) accessor.getBlockEntity();
        return new Data(
                furnace.v,
                furnace.w,
                List.of(furnace.a(0), furnace.a(1), furnace.a(2)));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
        return Data.STREAM_CODEC;
    }

    @Override
    public MinecraftKey getId() {
        return MC_FURNACE;
    }

    public record Data(int progress, int total, List<ItemStack> inventory) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.a(
                ByteBufCodecs.h,
                Data::progress,
                ByteBufCodecs.h,
                Data::total,
                ItemStack.i,
                Data::inventory,
                Data::new);
    }
}
