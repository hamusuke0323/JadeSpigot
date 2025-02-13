package com.hamusuke.jadespigot.network.handler;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketDataSerializer;
import org.jetbrains.annotations.NotNull;

public interface DefaultPacketHandler extends PacketHandler<PacketDataSerializer> {
    @Override
    default PacketDataSerializer wrap(@NotNull ByteBuf buf) {
        return new PacketDataSerializer(buf);
    }
}
