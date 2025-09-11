package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.JadeSpigot;
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

public enum StatusEffectsProvider implements StreamServerDataProvider<EntityAccessor, List<StatusEffectsProvider.Effect>> {
    INSTANCE;

    public static final MinecraftKey MC_POTION_EFFECTS = MinecraftKey.b("potion_effects");

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Effect>> STREAM_CODEC = ByteBufCodecs.<RegistryFriendlyByteBuf, Effect>a()
            .apply(Effect.STREAM_CODEC);

    @Override
    @Nullable
    public List<Effect> streamData(EntityAccessor accessor) {
        final var effects = JadeSpigot.instance().statusEffectsMap.toEffectList((EntityLiving) accessor.getEntity());
        return effects.isEmpty() ? null : effects;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, List<Effect>> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public MinecraftKey getId() {
        return MC_POTION_EFFECTS;
    }

    public record Effect(MobEffect effect, long updateTime, long addTime) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Effect> STREAM_CODEC = StreamCodec.a(
                MobEffect.e,
                Effect::effect,
                ByteBufCodecs.j,
                Effect::updateTime,
                ByteBufCodecs.j,
                Effect::addTime,
                Effect::new);
    }
}
