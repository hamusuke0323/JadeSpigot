package com.hamusuke.jadespigot.network.packet;

import com.hamusuke.jadespigot.JadeRegistry;
import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.impl.BlockAccessorImpl;
import com.hamusuke.jadespigot.impl.BlockAccessorImpl.SyncData;
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

public record RequestBlockPacket(SyncData data, List<ServerDataProvider<BlockAccessor>> dataProviders) {
    public static final String PACKET_REQUEST_BLOCK = "jade:request_block";
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestBlockPacket> CODEC = StreamCodec.a(
            SyncData.STREAM_CODEC,
            RequestBlockPacket::data,
            ByteBufCodecs.<ByteBuf, ServerDataProvider<BlockAccessor>>a()
                    .apply(ByteBufCodecs.a(
                            $ -> Objects.requireNonNull(JadeRegistry.INSTANCE.blockDataProviders.idMapper()).a($),
                            $ -> Objects.requireNonNull(JadeRegistry.INSTANCE.blockDataProviders.idMapper())
                                    .c($))),
            RequestBlockPacket::dataProviders,
            RequestBlockPacket::new);

    public enum RequestBlockPacketHandler implements RegPacketHandler {
        INSTANCE;

        @Override
        public void onPacketReceived(@NotNull String s, @NotNull Player player, @NotNull RegistryFriendlyByteBuf buf) {
            var packet = CODEC.decode(buf);
            buf.release();
            var ctx = new NetworkContext(player);
            BlockAccessorImpl.handleRequest(packet, ctx, tag -> ReceiveDataPacket.send(tag, ctx));
        }
    }
}
