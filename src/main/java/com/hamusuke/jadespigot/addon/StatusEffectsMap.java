package com.hamusuke.jadespigot.addon;

import com.google.common.collect.Maps;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.craftbukkit.v1_21_R5.potion.CraftPotionEffectType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

public final class StatusEffectsMap implements Listener {
    private final Map<Integer, Map<PotionEffectType, EffectData>> map = Maps.newConcurrentMap();

    private void add(final LivingEntity livingEntity, final PotionEffectType added) {
        final long time = System.currentTimeMillis();
        this.map.computeIfAbsent(livingEntity.getEntityId(), $ -> Maps.newConcurrentMap())
                .put(added, new EffectData(time, time));
    }

    private void remove(final LivingEntity livingEntity) {
        this.map.remove(livingEntity.getEntityId());
    }

    private void update(final LivingEntity livingEntity, final PotionEffectType updated) {
        if (!this.map.containsKey(livingEntity.getEntityId()) || !this.map.get(livingEntity.getEntityId()).containsKey(updated)) {
            return;
        }

        this.map.get(livingEntity.getEntityId()).get(updated).updateTime = System.currentTimeMillis();
    }

    public List<StatusEffectsProvider.Effect> toEffectList(final EntityLiving livingEntity) {
        final var bukkit = livingEntity.getBukkitEntity();
        if (!this.map.containsKey(bukkit.getEntityId())) {
            return List.of();
        }

        final var map = this.map.get(bukkit.getEntityId());
        return livingEntity.eI()
                .stream()
                .filter(MobEffect::g)
                .filter(e -> map.containsKey(CraftPotionEffectType.minecraftHolderToBukkit(e.c())))
                .map(e -> {
                    final var effect = map.get(CraftPotionEffectType.minecraftHolderToBukkit(e.c()));
                    return new StatusEffectsProvider.Effect(e, effect.updateTime, effect.addTime);
                })
                .toList();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void monitorEffectEvent(final EntityPotionEffectEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof LivingEntity livingEntity)) {
            return;
        }

        if (event.getOldEffect() == null && event.getNewEffect() != null) {
            this.add(livingEntity, event.getNewEffect().getType());
            return;
        }

        if (event.isOverride() && event.getNewEffect() != null) {
            this.update(livingEntity, event.getNewEffect().getType());
        }
    }

    @EventHandler
    public void onDeath(final EntityDeathEvent event) {
        this.remove(event.getEntity());
    }

    public static final class EffectData {
        public long updateTime;
        public long addTime;

        public EffectData(final long updateTime, final long addTime) {
            this.updateTime = updateTime;
            this.addTime = addTime;
        }
    }
}
