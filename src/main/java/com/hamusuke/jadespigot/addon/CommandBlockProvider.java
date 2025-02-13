package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.entity.TileEntityCommand;
import org.jetbrains.annotations.Nullable;

public enum CommandBlockProvider implements StreamServerDataProvider<BlockAccessor, String> {
    INSTANCE;

    public static final MinecraftKey MC_COMMAND_BLOCK = MinecraftKey.b("command_block");

    @Override
    @Nullable
    public String streamData(BlockAccessor accessor) {
        if (!accessor.getNMSPlayer().gG()) {
            return null;
        }
        String command = ((TileEntityCommand) accessor.getBlockEntity()).b().m();
        if (command.length() > 40) {
            command = command.substring(0, 37) + "...";
        }

        return command;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, String> streamCodec() {
        return ByteBufCodecs.o.a();
    }

    @Override
    public MinecraftKey getId() {
        return MC_COMMAND_BLOCK;
    }
}
