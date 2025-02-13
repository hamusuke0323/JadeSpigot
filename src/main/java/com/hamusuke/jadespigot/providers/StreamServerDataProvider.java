package com.hamusuke.jadespigot.providers;

import com.hamusuke.jadespigot.accessors.Accessor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public interface StreamServerDataProvider<T extends Accessor<?>, D> extends ServerDataProvider<T> {
    @Override
    default void appendServerData(NBTTagCompound data, T accessor) {
        var v = this.streamData(accessor);
        if (v != null) {
            data.a(this.getId().toString(), accessor.encodeAsNbt(this.streamCodec(), v));
        }
    }

    @Nullable
    D streamData(T accessor);

    StreamCodec<RegistryFriendlyByteBuf, D> streamCodec();
}
