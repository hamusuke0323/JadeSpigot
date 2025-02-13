package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.entity.TileEntityBeehive;

public enum BeehiveProvider implements StreamServerDataProvider<BlockAccessor, Byte> {
    INSTANCE;

    public static final MinecraftKey MC_BEEHIVE = MinecraftKey.b("beehive");

    @Override
    public Byte streamData(BlockAccessor accessor) {
        var beehive = (TileEntityBeehive) accessor.getBlockEntity();
        int bees = beehive.f();
        return (byte) (beehive.d() ? bees : -bees);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Byte> streamCodec() {
        return ByteBufCodecs.c.a();
    }

    @Override
    public MinecraftKey getId() {
        return MC_BEEHIVE;
    }
}
