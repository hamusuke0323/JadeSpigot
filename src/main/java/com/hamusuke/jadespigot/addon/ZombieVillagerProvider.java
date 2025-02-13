package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.EntityAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.monster.EntityZombieVillager;
import org.jetbrains.annotations.Nullable;

public enum ZombieVillagerProvider implements StreamServerDataProvider<EntityAccessor, Integer> {
    INSTANCE;

    public static final MinecraftKey MC_ZOMBIE_VILLAGER = MinecraftKey.b("zombie_villager");

    @Override
    public @Nullable Integer streamData(EntityAccessor accessor) {
        int time = ((EntityZombieVillager) accessor.getEntity()).cf;
        return time > 0 ? time : null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Integer> streamCodec() {
        return ByteBufCodecs.h.a();
    }

    @Override
    public MinecraftKey getId() {
        return MC_ZOMBIE_VILLAGER;
    }
}
