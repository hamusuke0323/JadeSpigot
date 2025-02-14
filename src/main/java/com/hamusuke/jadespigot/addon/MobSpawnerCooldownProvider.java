package com.hamusuke.jadespigot.addon;

import com.google.common.collect.Maps;
import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import com.mojang.serialization.JavaOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public enum MobSpawnerCooldownProvider implements StreamServerDataProvider<BlockAccessor, Integer> {
    INSTANCE;

    public static final MinecraftKey MC_MOB_SPAWNER_COOLDOWN = MinecraftKey.b("mob_spawner.cooldown");

    @Override
    public @Nullable Integer streamData(BlockAccessor accessor) {
        var spawnerBlock = (TrialSpawnerBlockEntity) accessor.getBlockEntity();
        var spawner = spawnerBlock.c();
        var spawnerData = spawner.f();
        var level = (WorldServer) accessor.getLevel();

        var result = TrialSpawnerData.b.encoder().encode(spawnerData, JavaOps.INSTANCE, Maps.newHashMap()).result();
        if (result.isEmpty() || !(result.get() instanceof Map<?, ?> map)) {
            return null;
        }

        var cooldownEndsAt = map.get("cooldown_ends_at");

        if (cooldownEndsAt instanceof Long l && spawner.a(level) && !spawnerData.a(level)) {
            return (int) (l - level.ad());
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
