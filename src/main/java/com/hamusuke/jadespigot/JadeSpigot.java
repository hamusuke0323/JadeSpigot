package com.hamusuke.jadespigot;

import com.hamusuke.jadespigot.network.packet.*;
import com.hamusuke.jadespigot.network.packet.ClientHandshakePacket.ClientHandshakePacketHandler;
import com.hamusuke.jadespigot.network.packet.RequestBlockPacket.RequestBlockPacketHandler;
import com.hamusuke.jadespigot.network.packet.RequestEntityPacket.RequestEntityPacketHandler;
import net.minecraft.core.IRegistryCustom;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R3.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

public final class JadeSpigot extends JavaPlugin {
    private static JadeSpigot INSTANCE;
    public static final String JADE_PROTO_VERSION = "7";
    public final IRegistryCustom registryCustom;

    public JadeSpigot() {
        INSTANCE = this;
        this.registryCustom = ((CraftServer) this.getServer()).getServer().bb().a();
        JadeRegistry.INSTANCE.registerAllBlockDataProviders();
        JadeRegistry.INSTANCE.registerAllEntityDataProviders();
        JadeRegistry.INSTANCE.registerAllItemStorageProviders();
        JadeRegistry.INSTANCE.loadComplete();
    }

    public static JadeSpigot instance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        this.getLogger().info("JadeSpigot enabled!");

        Bukkit.getMessenger().registerIncomingPluginChannel(this, ClientHandshakePacket.PACKET_CLIENT_HANDSHAKE, ClientHandshakePacketHandler.INSTANCE);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, RequestBlockPacket.PACKET_REQUEST_BLOCK, RequestBlockPacketHandler.INSTANCE);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, RequestEntityPacket.PACKET_REQUEST_ENTITY, RequestEntityPacketHandler.INSTANCE);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, ServerHandshakePacket.PACKET_SERVER_HANDSHAKE);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, ReceiveDataPacket.PACKET_RECEIVE_DATA);
    }

    @Override
    public void onDisable() {
        this.getLogger().info("JadeSpigot disabled");

        Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
    }
}
