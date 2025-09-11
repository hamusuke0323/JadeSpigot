package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.EntityAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.animal.allay.Allay;
import org.jetbrains.annotations.Nullable;

public enum MobBreedingProvider implements StreamServerDataProvider<EntityAccessor, Integer> {
    INSTANCE;

    public static final MinecraftKey MC_MOB_BREEDING = MinecraftKey.b("mob_breeding");

    @Override
    public @Nullable Integer streamData(EntityAccessor accessor) {
        int time = 0;
        var entity = accessor.getEntity();
        if (entity instanceof Allay allay) {
            if (allay.cy > 0 && allay.cy < Integer.MAX_VALUE) {
                time = (int) allay.cy;
            }
        } else {
            time = ((EntityAnimal) entity).Z_();
        }

        return time > 0 ? time : null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Integer> streamCodec() {
        return ByteBufCodecs.h.a();
    }

    @Override
    public MinecraftKey getId() {
        return MC_MOB_BREEDING;
    }
}
