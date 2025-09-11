package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.EntityAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import org.jetbrains.annotations.Nullable;

public enum AnimalOwnerProvider implements StreamServerDataProvider<EntityAccessor, String> {
    INSTANCE;

    public static final MinecraftKey MC_ANIMAL_OWNER = MinecraftKey.b("animal_owner");

    @Nullable
    @Override
    public String streamData(EntityAccessor accessor) {
        return null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, String> streamCodec() {
        return ByteBufCodecs.p.a();
    }

    @Override
    public MinecraftKey getId() {
        return MC_ANIMAL_OWNER;
    }
}
