package com.hamusuke.jadespigot.impl;

import com.google.common.base.Suppliers;
import com.hamusuke.jadespigot.JadeRegistry;
import com.hamusuke.jadespigot.JadeSpigot;
import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.network.NetworkContext;
import com.hamusuke.jadespigot.network.packet.RequestBlockPacket;
import com.hamusuke.jadespigot.providers.ServerDataProvider;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockAccessorImpl extends AccessorImpl<MovingObjectPositionBlock> implements BlockAccessor {
    private final IBlockData blockState;
    @Nullable
    private final Supplier<TileEntity> blockEntity;

    private BlockAccessorImpl(Builder builder) {
        super(builder.level, builder.player, null, Suppliers.ofInstance(builder.hit));
        this.blockState = builder.blockState;
        this.blockEntity = builder.blockEntity;
    }

    public static void handleRequest(RequestBlockPacket message, NetworkContext context, Consumer<NBTTagCompound> responseSender) {
        var player = context.getPlayer();
        context.execute(() -> {
            var accessor = message.data().unpack(player);
            if (accessor == null) {
                return;
            }

            var pos = accessor.getPosition();
            var world = player.y();
            double maxDistance = MathHelper.k(player.gL() + 21);
            if (pos.j(player.dv()) > maxDistance || !world.p(pos)) {
                return;
            }

            List<ServerDataProvider<BlockAccessor>> providers = JadeRegistry.INSTANCE.getBlockNBTProviders(accessor.getBlock(), accessor.getBlockEntity());
            var tag = accessor.getServerData();
            for (var provider : providers) {
                try {
                    provider.appendServerData(tag, accessor);
                } catch (Exception e) {
                    JadeSpigot.instance().getLogger().warning(e.toString());
                }
            }

            tag.a("x", pos.u());
            tag.a("y", pos.v());
            tag.a("z", pos.w());
            tag.a("BlockId", BuiltInRegistries.e.b(accessor.getBlock()).toString());
            responseSender.accept(tag);
        });
    }

    @Override
    public Block getBlock() {
        return this.getBlockState().b();
    }

    @Override
    public IBlockData getBlockState() {
        return this.blockState;
    }

    @Override
    public TileEntity getBlockEntity() {
        return this.blockEntity == null ? null : this.blockEntity.get();
    }

    @Override
    public BlockPosition getPosition() {
        return this.getHitResult().b();
    }

    @Nullable
    @Override
    public Object getTarget() {
        return this.getBlockEntity();
    }

    public static class Builder implements BlockAccessor.Builder {
        private World level;
        private Player player;
        private MovingObjectPositionBlock hit;
        private IBlockData blockState = Blocks.a.m();
        private Supplier<TileEntity> blockEntity;

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
            return this;
        }

        @Override
        public Builder hit(MovingObjectPositionBlock hit) {
            this.hit = hit;
            return this;
        }

        @Override
        public Builder blockState(IBlockData blockState) {
            this.blockState = blockState;
            return this;
        }

        @Override
        public Builder blockEntity(Supplier<TileEntity> blockEntity) {
            this.blockEntity = blockEntity;
            return this;
        }

        @Override
        public Builder fakeBlock(ItemStack stack) {
            return this;
        }

        @Override
        public BlockAccessor build() {
            return new BlockAccessorImpl(this);
        }
    }

    public record SyncData(boolean showDetails, MovingObjectPositionBlock hit, IBlockData blockState,
                           ItemStack fakeBlock) {
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncData> STREAM_CODEC = StreamCodec.a(
                ByteBufCodecs.b,
                SyncData::showDetails,
                StreamCodec.a((StreamEncoder<RegistryFriendlyByteBuf, MovingObjectPositionBlock>) (o, movingObjectPositionBlock) -> o.a(movingObjectPositionBlock), RegistryFriendlyByteBuf::v),
                SyncData::hit,
                ByteBufCodecs.a(Block.q),
                SyncData::blockState,
                ItemStack.g,
                SyncData::fakeBlock,
                SyncData::new
        );

        public BlockAccessor unpack(EntityPlayer player) {
            Supplier<TileEntity> blockEntity = null;
            if (this.blockState.x()) {
                blockEntity = Suppliers.memoize(() -> player.dV().c_(hit.b()));
            }

            return new Builder()
                    .level(player.dV())
                    .player(player.getBukkitEntity().getPlayer())
                    .showDetails(showDetails)
                    .hit(hit)
                    .blockState(blockState)
                    .blockEntity(blockEntity)
                    .fakeBlock(fakeBlock)
                    .build();
        }
    }
}
