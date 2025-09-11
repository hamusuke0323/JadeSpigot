package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.providers.ServerDataProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.CalibratedSculkSensorBlock;
import net.minecraft.world.level.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.TileEntityComparator;

public enum RedstoneProvider implements ServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final MinecraftKey MC_REDSTONE = MinecraftKey.b("redstone");

    @Override
    public void appendServerData(NBTTagCompound data, BlockAccessor accessor) {
        final var blockEntity = accessor.getBlockEntity();
        if (blockEntity instanceof TileEntityComparator comparator) {
            data.a("Signal", comparator.a());
        } else if (blockEntity instanceof CalibratedSculkSensorBlockEntity) {
            final var direction = accessor.getBlockState().c(CalibratedSculkSensorBlock.b).g();
            final int signal = accessor.getLevel().c(accessor.getPosition().a(direction), direction);
            data.a("Signal", signal);
        }
    }

    @Override
    public MinecraftKey getId() {
        return MC_REDSTONE;
    }
}
