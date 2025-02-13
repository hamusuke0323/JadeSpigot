package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.EntityAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.animal.frog.Tadpole;
import org.jetbrains.annotations.Nullable;

public enum MobGrowthProvider implements StreamServerDataProvider<EntityAccessor, Integer> {
    INSTANCE;

    public static final MinecraftKey MC_MOB_GROWTH = MinecraftKey.b("mob_growth");

    @Override
    public @Nullable Integer streamData(EntityAccessor accessor) {
        int time = -1;
        Entity entity = accessor.getEntity();
        if (entity instanceof EntityAgeable ageable) {
            time = -ageable.Y_();
        } else if (entity instanceof Tadpole tadpole) {
            time = Math.max(0, Tadpole.a - tadpole.ca);
        }
        return time > 0 ? time : null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Integer> streamCodec() {
        return ByteBufCodecs.h.a();
    }

    @Override
    public MinecraftKey getId() {
        return MC_MOB_GROWTH;
    }
}
