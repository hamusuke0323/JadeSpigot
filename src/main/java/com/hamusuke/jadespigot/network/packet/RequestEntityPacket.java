package com.hamusuke.jadespigot.network.packet;

import com.hamusuke.jadespigot.JadeRegistry;
import com.hamusuke.jadespigot.accessors.EntityAccessor;
import com.hamusuke.jadespigot.impl.EntityAccessorImpl;
import com.hamusuke.jadespigot.impl.EntityAccessorImpl.SyncData;
import com.hamusuke.jadespigot.network.NetworkContext;
import com.hamusuke.jadespigot.network.handler.RegPacketHandler;
import com.hamusuke.jadespigot.providers.ServerDataProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public record RequestEntityPacket(SyncData data, List<ServerDataProvider<EntityAccessor>> dataProviders) {
    public static final String PACKET_REQUEST_ENTITY = "jade:request_entity";
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestEntityPacket> CODEC = StreamCodec.a(
            SyncData.STREAM_CODEC,
            RequestEntityPacket::data,
            ByteBufCodecs.<ByteBuf, ServerDataProvider<EntityAccessor>>a()
                    .apply(ByteBufCodecs.a(
                            $ -> Objects.requireNonNull(JadeRegistry.INSTANCE.entityDataProviders.idMapper()).a($),
                            $ -> Objects.requireNonNull(JadeRegistry.INSTANCE.entityDataProviders.idMapper())
                                    .c($))),
            RequestEntityPacket::dataProviders,
            RequestEntityPacket::new);

    public enum RequestEntityPacketHandler implements RegPacketHandler {
        INSTANCE;

        @Override
        public void onPacketReceived(@NotNull String s, @NotNull Player player, @NotNull RegistryFriendlyByteBuf buf) {
            var packet = CODEC.decode(buf);
            buf.release();
            var ctx = new NetworkContext(player);
            EntityAccessorImpl.handleRequest(packet, ctx, tag -> ReceiveDataPacket.send(tag, ctx));
        }
    }
}
