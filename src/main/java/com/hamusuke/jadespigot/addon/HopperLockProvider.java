package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.state.properties.BlockProperties;

public enum HopperLockProvider implements StreamServerDataProvider<BlockAccessor, Boolean> {
    INSTANCE;

    public static final MinecraftKey MC_HOPPER_LOCK = MinecraftKey.b("hopper_lock");

    @Override
    public Boolean streamData(BlockAccessor accessor) {
        return !accessor.getBlockState().c(BlockProperties.i);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Boolean> streamCodec() {
        return ByteBufCodecs.b.a();
    }

    @Override
    public MinecraftKey getId() {
        return MC_HOPPER_LOCK;
    }
}
