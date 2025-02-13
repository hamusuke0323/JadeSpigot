package com.hamusuke.jadespigot.network;

import com.hamusuke.jadespigot.JadeSpigot;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public record NetworkContext(Player player) {
    public void execute(Runnable runnable) {
        this.getPlayer().g.execute(runnable);
    }

    public void send(String name, ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.release();
        this.player.sendPluginMessage(JadeSpigot.instance(), name, bytes);
    }

    public EntityPlayer getPlayer() {
        return ((CraftPlayer) this.player).getHandle();
    }
}
