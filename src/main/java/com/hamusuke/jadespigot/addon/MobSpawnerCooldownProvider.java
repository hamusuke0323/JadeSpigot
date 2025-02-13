package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import org.jetbrains.annotations.Nullable;

public enum MobSpawnerCooldownProvider implements StreamServerDataProvider<BlockAccessor, Integer> {
    INSTANCE;

    public static final MinecraftKey MC_MOB_SPAWNER_COOLDOWN = MinecraftKey.b("mob_spawner.cooldown");

    @Override
    public @Nullable Integer streamData(BlockAccessor accessor) {
        var spawner = (TrialSpawnerBlockEntity) accessor.getBlockEntity();
        var spawnerData = spawner.c().f();
        var level = (WorldServer) accessor.getLevel();
        if (spawner.c().a(level) && !spawnerData.a(level)) {
            return (int) (spawner.c().d().a() - level.ad());
        }

        return null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Integer> streamCodec() {
        return ByteBufCodecs.h.a();
    }

    @Override
    public MinecraftKey getId() {
        return MC_MOB_SPAWNER_COOLDOWN;
    }
}
