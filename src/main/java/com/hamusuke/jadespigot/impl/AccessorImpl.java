package com.hamusuke.jadespigot.impl;

import com.hamusuke.jadespigot.accessors.Accessor;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class AccessorImpl<T extends MovingObjectPosition> implements Accessor<T> {
    private final World level;
    private final Player player;
    private final NBTTagCompound serverData;
    private final Supplier<T> hit;
    protected boolean verify;
    private RegistryFriendlyByteBuf buffer;

    public AccessorImpl(World level, Player player, NBTTagCompound serverData, Supplier<T> hit) {
        this.level = level;
        this.player = player;
        this.hit = hit;
        this.serverData = serverData == null ? new NBTTagCompound() : serverData.l();
    }

    @Override
    public World getLevel() {
        return this.level;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public final @NotNull NBTTagCompound getServerData() {
        return this.serverData;
    }

    private RegistryFriendlyByteBuf buffer() {
        if (this.buffer == null) {
            this.buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), this.level.K_());
        }

        this.buffer.clear();
        return this.buffer;
    }

    @Override
    public <D> NBTBase encodeAsNbt(StreamEncoder<RegistryFriendlyByteBuf, D> streamCodec, D value) {
        var buffer = this.buffer();
        streamCodec.encode(buffer, value);
        var tag = new NBTTagByteArray(ArrayUtils.subarray(buffer.array(), 0, buffer.readableBytes()));
        buffer.clear();
        return tag;
    }

    @Override
    public T getHitResult() {
        return this.hit.get();
    }

    public void requireVerification() {
        this.verify = true;
    }
}
