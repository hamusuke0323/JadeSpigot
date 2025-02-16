package com.hamusuke.jadespigot.network.packet;

import com.hamusuke.jadespigot.JadeRegistry;
import com.hamusuke.jadespigot.JadeSpigot;
import com.hamusuke.jadespigot.network.NetworkContext;
import com.hamusuke.jadespigot.network.handler.DefaultPacketHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public record ClientHandshakePacket(String protocolVersion) {
    public static final String PACKET_CLIENT_HANDSHAKE = "jade:client_handshake";
    public static final StreamCodec<PacketDataSerializer, ClientHandshakePacket> CODEC = StreamCodec
            .a(ByteBufCodecs.o, ClientHandshakePacket::protocolVersion, ClientHandshakePacket::new);

    public enum ClientHandshakePacketHandler implements DefaultPacketHandler {
        INSTANCE;

        @Override
        public void onPacketReceived(@NotNull String s, @NotNull Player player, @NotNull PacketDataSerializer buf) {
            var packet = CODEC.decode(buf);
            buf.release();
            var ctx = new NetworkContext(player);

            ctx.execute(() -> {
                if (!JadeSpigot.JADE_PROTO_VERSION.equals(packet.protocolVersion())) {
                    JadeSpigot.instance().getLogger().warning(player.getDisplayName() + " has Jade whose protocol version is outdated: " + packet.protocolVersion());
                    return;
                }

                var rsp = new ServerHandshakePacket(Map.of(), List.of(), JadeRegistry.INSTANCE.blockDataProviders.mappedIds(), JadeRegistry.INSTANCE.entityDataProviders.mappedIds());
                var packetBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), JadeSpigot.instance().registryCustom);
                ServerHandshakePacket.CODEC.encode(packetBuf, rsp);
                ctx.send(ServerHandshakePacket.PACKET_SERVER_HANDSHAKE, packetBuf);
                JadeSpigot.instance().getLogger().info(player.getDisplayName() + " seems to have Jade. Reply handshake packet.");
            });
        }
    }
}
