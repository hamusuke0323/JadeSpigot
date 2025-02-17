package com.hamusuke.jadespigot.network.packet;

import com.hamusuke.jadespigot.JadeSpigot;
import org.bukkit.entity.Player;

public class ServerHandshakePacket {
    public static final String PACKET_SERVER_HANDSHAKE = "jade:server_handshake";
    public static final byte[] RESPONSE = new byte[]{0, 0, 0, 0};

    public static void send(Player player) {
        player.sendPluginMessage(JadeSpigot.instance(), PACKET_SERVER_HANDSHAKE, RESPONSE);
    }
}
