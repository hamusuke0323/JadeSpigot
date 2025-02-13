package com.hamusuke.jadespigot.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public interface PacketHandler<B extends ByteBuf> extends PluginMessageListener {
    void onPacketReceived(@NotNull String s, @NotNull Player player, @NotNull B buf);

    B wrap(@NotNull ByteBuf buf);

    @Override
    default void onPluginMessageReceived(@NotNull String s, @NotNull Player player, @NotNull byte[] bytes) {
        var wrapped = this.wrap(Unpooled.wrappedBuffer(bytes));
        this.onPacketReceived(s, player, wrapped);
    }
}
