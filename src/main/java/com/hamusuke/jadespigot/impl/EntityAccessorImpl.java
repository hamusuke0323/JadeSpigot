package com.hamusuke.jadespigot.impl;

import com.google.common.base.Suppliers;
import com.hamusuke.jadespigot.JadeRegistry;
import com.hamusuke.jadespigot.JadeSpigot;
import com.hamusuke.jadespigot.Utils;
import com.hamusuke.jadespigot.accessors.EntityAccessor;
import com.hamusuke.jadespigot.network.NetworkContext;
import com.hamusuke.jadespigot.network.packet.RequestEntityPacket;
import com.hamusuke.jadespigot.providers.ServerDataProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityAccessorImpl extends AccessorImpl<MovingObjectPositionEntity> implements EntityAccessor {
    private final Supplier<Entity> entity;

    public EntityAccessorImpl(Builder builder) {
        super(builder.level, builder.player, null, builder.hit);
        this.entity = builder.entity;
    }

    public static void handleRequest(RequestEntityPacket message, NetworkContext context, Consumer<NBTTagCompound> responseSender) {
        final var player = context.getPlayer();
        context.execute(() -> {
            final var accessor = message.data().unpack(player);
            if (accessor == null) {
                return;
            }

            final var entity = accessor.getEntity();
            final double maxDistance = MathHelper.k(player.gW() + 21);
            if (entity == null || player.g(entity) > maxDistance) {
                return;
            }

            final var providers = JadeRegistry.INSTANCE.getEntityNBTProviders(entity);
            final var tag = accessor.getServerData();
            for (final var provider : providers) {
                try {
                    provider.appendServerData(tag, accessor);
                } catch (Exception e) {
                    JadeSpigot.instance().getLogger().warning(e.toString());
                }
            }

            tag.a("EntityId", entity.ar());
            responseSender.accept(tag);
        });
    }

    @Override
    public Entity getEntity() {
        return Utils.wrapPartEntityParent(this.getRawEntity());
    }

    @Override
    public Entity getRawEntity() {
        return this.entity.get();
    }

    @NotNull
    @Override
    public Object getTarget() {
        return this.getEntity();
    }

    public static class Builder implements EntityAccessor.Builder {
        public boolean showDetails;
        private World level;
        private Player player;
        private Supplier<MovingObjectPositionEntity> hit;
        private Supplier<Entity> entity;

        @Override
        public Builder level(World level) {
            this.level = level;
            return this;
        }

        @Override
        public Builder player(Player player) {
            this.player = player;
            return this;
        }

        @Override
        public Builder showDetails(boolean showDetails) {
            this.showDetails = showDetails;
            return this;
        }

        @Override
        public Builder hit(Supplier<MovingObjectPositionEntity> hit) {
            this.hit = hit;
            return this;
        }

        @Override
        public Builder entity(Supplier<Entity> entity) {
            this.entity = entity;
            return this;
        }

        @Override
        public EntityAccessor build() {
            return new EntityAccessorImpl(this);
        }
    }

    public record SyncData(boolean showDetails, int id, int partIndex, Vec3D hitVec) {
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncData> STREAM_CODEC = StreamCodec.a(
                ByteBufCodecs.b,
                SyncData::showDetails,
                ByteBufCodecs.h,
                SyncData::id,
                ByteBufCodecs.h,
                SyncData::partIndex,
                ByteBufCodecs.v.a(Vec3D::new, Vec3D::l),
                SyncData::hitVec,
                SyncData::new
        );

        public EntityAccessor unpack(EntityPlayer player) {
            Supplier<Entity> entity = Suppliers.memoize(() -> Utils.getPartEntity(player.y().a(this.id), this.partIndex));
            return new EntityAccessorImpl.Builder()
                    .level(player.y())
                    .player(player.getBukkitEntity().getPlayer())
                    .showDetails(this.showDetails)
                    .entity(entity)
                    .hit(Suppliers.memoize(() -> new MovingObjectPositionEntity(entity.get(), this.hitVec)))
                    .build();
        }
    }
}
