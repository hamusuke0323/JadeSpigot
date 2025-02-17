package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.Utils;
import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;
import org.jetbrains.annotations.Nullable;

public enum MobSpawnerCooldownProvider implements StreamServerDataProvider<BlockAccessor, Integer> {
    INSTANCE;

    public static final MinecraftKey MC_MOB_SPAWNER_COOLDOWN = MinecraftKey.b("mob_spawner.cooldown");

    @Override
    public @Nullable Integer streamData(BlockAccessor accessor) {
        var spawnerBlock = (TrialSpawnerBlockEntity) accessor.getBlockEntity();
        var spawner = spawnerBlock.c();
        var spawnerData = spawner.f();
        var level = (WorldServer) accessor.getLevel();

        var cooldownEndsAt = Utils.retrieveFieldFrom(spawnerData, "cooldown_ends_at", TrialSpawnerData.b.codec(), Long.class);
        if (cooldownEndsAt != null && spawner.a(level) && !spawnerData.a(level)) {
            return (int) (cooldownEndsAt - level.ad());
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
