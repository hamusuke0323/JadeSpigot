package com.hamusuke.jadespigot;

import com.hamusuke.jadespigot.network.packet.*;
import com.hamusuke.jadespigot.network.packet.ClientHandshakePacket.ClientHandshakePacketHandler;
import com.hamusuke.jadespigot.network.packet.RequestBlockPacket.RequestBlockPacketHandler;
import com.hamusuke.jadespigot.network.packet.RequestEntityPacket.RequestEntityPacketHandler;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatHoverable.EnumHoverAction;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_21_R3.CraftServer;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.UnaryOperator;

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

        Objects.requireNonNull(this.getCommand("jadehandshake")).setExecutor(this);

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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof CraftPlayer player)) {
            sender.sendMessage("Player only!");
            return false;
        }

        ServerHandshakePacket.send(player);
        var nms = player.getHandle();
        nms.a(IChatBaseComponent.b("")
                .b(IChatBaseComponent
                        .b("[JadeSpigot]")
                        .a((UnaryOperator<ChatModifier>) c -> c
                                .a(EnumChatFormat.h)))
                .b(IChatBaseComponent
                        .b(": Resent "))
                .b(IChatBaseComponent
                        .b("handshake packet")
                        .a((UnaryOperator<ChatModifier>) chatModifier -> chatModifier
                                .a(new ChatHoverable(EnumHoverAction.a, IChatBaseComponent
                                        .b("byte: " + Arrays.toString(ServerHandshakePacket.RESPONSE))))))
                .b(IChatBaseComponent
                        .b(" to "))
                .b(nms.p_())
        );

        return true;
    }
}
