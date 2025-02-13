package com.hamusuke.jadespigot.network.handler;

import com.hamusuke.jadespigot.JadeSpigot;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public interface RegPacketHandler extends PacketHandler<RegistryFriendlyByteBuf> {
    @Override
    default RegistryFriendlyByteBuf wrap(@NotNull ByteBuf buf) {
        return new RegistryFriendlyByteBuf(buf, JadeSpigot.instance().registryCustom);
    }
}
