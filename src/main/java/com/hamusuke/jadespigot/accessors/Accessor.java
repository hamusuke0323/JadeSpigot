package com.hamusuke.jadespigot.accessors;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Accessor<T extends MovingObjectPosition> {
    World getLevel();

    Player getPlayer();

    default EntityPlayer getNMSPlayer() {
        return ((CraftPlayer) this.getPlayer()).getHandle();
    }

    @NotNull
    NBTTagCompound getServerData();

    <D> NBTBase encodeAsNbt(StreamEncoder<RegistryFriendlyByteBuf, D> codec, D value);

    T getHitResult();

    @Nullable
    Object getTarget();
}
