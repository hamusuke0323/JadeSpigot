package com.hamusuke.jadespigot.accessors;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

public interface BlockAccessor extends Accessor<MovingObjectPositionBlock> {
    Block getBlock();

    IBlockData getBlockState();

    TileEntity getBlockEntity();

    BlockPosition getPosition();

    ItemStack getFakeBlock();

    interface Builder {
        Builder level(World level);

        Builder player(Player player);

        Builder showDetails(boolean showDetails);

        Builder hit(MovingObjectPositionBlock hit);

        Builder blockState(IBlockData state);

        Builder blockEntity(Supplier<TileEntity> blockEntity);

        Builder fakeBlock(ItemStack stack);

        BlockAccessor build();
    }
}
