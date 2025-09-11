package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.addon.BrewingStandProvider.Data;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.entity.TileEntityBrewingStand;

public enum BrewingStandProvider implements StreamServerDataProvider<BlockAccessor, Data> {
    INSTANCE;

    public static final MinecraftKey MC_BREWING_STAND = MinecraftKey.b("brewing_stand");

    @Override
    public Data streamData(BlockAccessor accessor) {
        var brewingStand = (TileEntityBrewingStand) accessor.getBlockEntity();
        return new Data(brewingStand.u, brewingStand.r);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
        return Data.STREAM_CODEC.a();
    }

    @Override
    public MinecraftKey getId() {
        return MC_BREWING_STAND;
    }

    public record Data(int fuel, int time) {
        public static final StreamCodec<ByteBuf, Data> STREAM_CODEC = StreamCodec.a(
                ByteBufCodecs.h,
                Data::fuel,
                ByteBufCodecs.h,
                Data::time,
                Data::new);
    }
}
