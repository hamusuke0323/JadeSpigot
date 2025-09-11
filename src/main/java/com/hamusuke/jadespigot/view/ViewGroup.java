package com.hamusuke.jadespigot.view;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ViewGroup<T> {
    public static <B extends ByteBuf, T> StreamCodec<B, ViewGroup<T>> codec(StreamCodec<B, T> viewCodec) {
        return StreamCodec.a(
                ByteBufCodecs.<B, T>a().apply(viewCodec),
                $ -> $.views,
                ByteBufCodecs.a(ByteBufCodecs.p),
                $ -> Optional.ofNullable($.id),
                ByteBufCodecs.a(ByteBufCodecs.s),
                $ -> Optional.ofNullable($.extraData),
                ViewGroup::new);
    }

    public static <B extends ByteBuf, T> StreamCodec<B, Map.Entry<MinecraftKey, List<ViewGroup<T>>>> listCodec(StreamCodec<B, T> viewCodec) {
        return StreamCodec.a(
                MinecraftKey.b,
                Map.Entry::getKey,
                ByteBufCodecs.<B, ViewGroup<T>>a().apply(codec(viewCodec)),
                Map.Entry::getValue,
                Map::entry);
    }

    public List<T> views;
    @Nullable
    public String id;
    @Nullable
    protected NBTTagCompound extraData;

    public ViewGroup(List<T> views) {
        this(views, Optional.empty(), Optional.empty());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public ViewGroup(List<T> views, Optional<String> id, Optional<NBTTagCompound> extraData) {
        this.views = views;
        this.id = id.orElse(null);
        this.extraData = extraData.orElse(null);
    }

    public NBTTagCompound getExtraData() {
        if (this.extraData == null) {
            this.extraData = new NBTTagCompound();
        }

        return this.extraData;
    }
}
