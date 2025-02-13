package com.hamusuke.jadespigot.network.packet;

import com.google.common.collect.Maps;
import com.hamusuke.jadespigot.network.codecs.JadeCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;

public record ServerHandshakePacket(Map<MinecraftKey, Object> serverConfig, List<Block> shearableBlocks,
                                    List<MinecraftKey> blockProviderIds, List<MinecraftKey> entityProviderIds) {
    public static final String PACKET_SERVER_HANDSHAKE = "jade:server_handshake";

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerHandshakePacket> CODEC = StreamCodec.a(
            ByteBufCodecs.a(Maps::newHashMapWithExpectedSize, MinecraftKey.b, JadeCodecs.PRIMITIVE_STREAM_CODEC),
            ServerHandshakePacket::serverConfig,
            ByteBufCodecs.a(Registries.f).a(ByteBufCodecs.a()),
            ServerHandshakePacket::shearableBlocks,
            ByteBufCodecs.<ByteBuf, MinecraftKey>a().apply(MinecraftKey.b),
            ServerHandshakePacket::blockProviderIds,
            ByteBufCodecs.<ByteBuf, MinecraftKey>a().apply(MinecraftKey.b),
            ServerHandshakePacket::entityProviderIds,
            ServerHandshakePacket::new
    );
}
