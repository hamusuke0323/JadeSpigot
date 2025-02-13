package com.hamusuke.jadespigot.network.packet;

import com.hamusuke.jadespigot.JadeSpigot;
import com.hamusuke.jadespigot.network.NetworkContext;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record ReceiveDataPacket(NBTTagCompound tag) {
    public static final int MAX_SIZE = 16 * 1024;
    public static final String PACKET_RECEIVE_DATA = "jade:receive_data";
    public static final StreamCodec<PacketDataSerializer, ReceiveDataPacket> CODEC = StreamCodec.a(
            ByteBufCodecs.r,
            ReceiveDataPacket::tag,
            ReceiveDataPacket::new
    );
    private static int spamCount;

    public static void send(NBTTagCompound tag, NetworkContext context) {
        int size = tag.a();
        if (size > MAX_SIZE) {
            if (spamCount++ < 1) {
                JadeSpigot.instance().getLogger().finest("Data size is too large: %s, max: %s, data: %s".formatted(size, MAX_SIZE, tag));
            }

            int c = 0;
            do {
                if (++c > 10) {
                    return;
                }
                removeLargest(tag, 0, 1);
            } while (tag.a() > MAX_SIZE);
        }

        var buf = new PacketDataSerializer(Unpooled.buffer());
        CODEC.encode(buf, new ReceiveDataPacket(tag));
        context.send(PACKET_RECEIVE_DATA, buf);
    }

    private static boolean removeLargest(NBTTagCompound tag, int depth, int maxDepth) {
        int largestSize = 0;
        String largestKey = null;
        NBTBase largestValue = null;
        for (var key : tag.e()) {
            var childTag = Objects.requireNonNull(tag.c(key));
            int size = childTag.a();
            if (size > largestSize) {
                largestSize = size;
                largestKey = key;
                largestValue = childTag;
            }
        }

        if (largestKey == null) {
            return false;
        }

        if (depth < maxDepth && largestValue instanceof NBTTagCompound compound) {
            if (!removeLargest(compound, depth + 1, maxDepth)) {
                tag.r(largestKey);
            }
        } else {
            tag.r(largestKey);
        }

        return true;
    }
}
