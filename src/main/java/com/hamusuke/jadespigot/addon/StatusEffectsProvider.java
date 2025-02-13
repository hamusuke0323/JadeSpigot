package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.EntityAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityLiving;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum StatusEffectsProvider implements StreamServerDataProvider<EntityAccessor, List<MobEffect>> {
    INSTANCE;

    public static final MinecraftKey MC_POTION_EFFECTS = MinecraftKey.b("potion_effects");

    private static final StreamCodec<RegistryFriendlyByteBuf, List<MobEffect>> STREAM_CODEC = ByteBufCodecs.<RegistryFriendlyByteBuf, MobEffect>a()
            .apply(MobEffect.e);

    @Override
    @Nullable
    public List<MobEffect> streamData(EntityAccessor accessor) {
        List<MobEffect> effects = ((EntityLiving) accessor.getEntity()).eA()
                .stream()
                .filter(MobEffect::g)
                .toList();
        return effects.isEmpty() ? null : effects;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, List<MobEffect>> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public MinecraftKey getId() {
        return MC_POTION_EFFECTS;
    }
}
