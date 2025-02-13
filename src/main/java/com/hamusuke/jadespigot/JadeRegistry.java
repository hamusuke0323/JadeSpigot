package com.hamusuke.jadespigot;

import com.hamusuke.jadespigot.accessors.BlockAccessor;
import com.hamusuke.jadespigot.accessors.EntityAccessor;
import com.hamusuke.jadespigot.addon.*;
import com.hamusuke.jadespigot.addon.ItemStorageProvider.Extension;
import com.hamusuke.jadespigot.impl.PriorityStore;
import com.hamusuke.jadespigot.impl.lookup.HierarchyLookup;
import com.hamusuke.jadespigot.impl.lookup.PairHierarchyLookup;
import com.hamusuke.jadespigot.impl.lookup.WrappedHierarchyLookup;
import com.hamusuke.jadespigot.providers.ServerDataProvider;
import com.hamusuke.jadespigot.view.ServerExtensionProvider;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.animal.EntityChicken;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.monster.EntityZombieVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityCampfire;
import net.minecraft.world.level.block.entity.TileEntityComparator;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum JadeRegistry {
    INSTANCE;

    public final PairHierarchyLookup<ServerDataProvider<BlockAccessor>> blockDataProviders;
    public final HierarchyLookup<ServerDataProvider<EntityAccessor>> entityDataProviders;
    public final PriorityStore<MinecraftKey, JadeIdProvider> priorities;
    public final WrappedHierarchyLookup<ServerExtensionProvider<ItemStack>> itemStorageProviders;

    JadeRegistry() {
        this.blockDataProviders = new PairHierarchyLookup<>(new HierarchyLookup<>(Block.class), new HierarchyLookup<>(TileEntity.class));
        this.blockDataProviders.idMapped();

        this.entityDataProviders = new HierarchyLookup<>(Entity.class);
        this.entityDataProviders.idMapped();

        this.priorities = new PriorityStore<>(JadeIdProvider::getDefaultPriority, JadeIdProvider::getId);

        this.itemStorageProviders = WrappedHierarchyLookup.forAccessor();
    }

    public void registerAllBlockDataProviders() {
        this.registerBlockDataProvider(ItemStorageProvider.getBlock(), Block.class);

        this.registerBlockDataProvider(BrewingStandProvider.INSTANCE, BlockBrewingStand.class);
        this.registerBlockDataProvider(BeehiveProvider.INSTANCE, BlockBeehive.class);
        this.registerBlockDataProvider(CommandBlockProvider.INSTANCE, BlockCommand.class);
        this.registerBlockDataProvider(HopperLockProvider.INSTANCE, BlockHopper.class);
        this.registerBlockDataProvider(JukeboxProvider.INSTANCE, BlockJukeBox.class);
        this.registerBlockDataProvider(LecternProvider.INSTANCE, BlockLectern.class);
        this.registerBlockDataProvider(RedstoneProvider.INSTANCE, TileEntityComparator.class);
        this.registerBlockDataProvider(RedstoneProvider.INSTANCE, CalibratedSculkSensorBlockEntity.class);
        this.registerBlockDataProvider(FurnaceProvider.INSTANCE, BlockFurnace.class);
        this.registerBlockDataProvider(ChiseledBookshelfProvider.INSTANCE, ChiseledBookShelfBlock.class);
        this.registerBlockDataProvider(MobSpawnerCooldownProvider.INSTANCE, TrialSpawnerBlock.class);
    }

    public void registerAllEntityDataProviders() {
        this.registerEntityDataProvider(ItemStorageProvider.getEntity(), Entity.class);

        this.registerEntityDataProvider(AnimalOwnerProvider.INSTANCE, Entity.class);
        this.registerEntityDataProvider(StatusEffectsProvider.INSTANCE, EntityLiving.class);
        this.registerEntityDataProvider(MobGrowthProvider.INSTANCE, EntityAgeable.class);
        this.registerEntityDataProvider(MobGrowthProvider.INSTANCE, Tadpole.class);
        this.registerEntityDataProvider(MobBreedingProvider.INSTANCE, EntityAnimal.class);
        this.registerEntityDataProvider(MobBreedingProvider.INSTANCE, Allay.class);
        this.registerEntityDataProvider(NextEntityDropProvider.INSTANCE, EntityChicken.class);
        this.registerEntityDataProvider(NextEntityDropProvider.INSTANCE, Armadillo.class);
        this.registerEntityDataProvider(ZombieVillagerProvider.INSTANCE, EntityZombieVillager.class);
        this.registerEntityDataProvider(PetArmorProvider.INSTANCE, EntityInsentient.class);
    }

    public void registerAllItemStorageProviders() {
        this.registerItemStorage(Extension.INSTANCE, Object.class);
        this.registerItemStorage(Extension.INSTANCE, Block.class);

        this.registerItemStorage(CampfireProvider.INSTANCE, TileEntityCampfire.class);
    }

    public void registerBlockDataProvider(ServerDataProvider<BlockAccessor> dataProvider, Class<?> blockOrBlobkEntityClass) {
        this.blockDataProviders.register(blockOrBlobkEntityClass, dataProvider);
    }

    public void registerEntityDataProvider(ServerDataProvider<EntityAccessor> dataProvider, Class<? extends Entity> entityClass) {
        this.entityDataProviders.register(entityClass, dataProvider);
    }

    public List<ServerDataProvider<BlockAccessor>> getBlockNBTProviders(Block block, @Nullable TileEntity blockEntity) {
        if (blockEntity == null) {
            return this.blockDataProviders.first.get(block);
        }

        return this.blockDataProviders.getMerged(block, blockEntity);
    }

    public List<ServerDataProvider<EntityAccessor>> getEntityNBTProviders(Entity entity) {
        return this.entityDataProviders.get(entity);
    }

    public void loadComplete() {
        this.blockDataProviders.loadComplete(this.priorities);
        this.entityDataProviders.loadComplete(this.priorities);
        this.itemStorageProviders.loadComplete(this.priorities);
    }

    public <T> void registerItemStorage(ServerExtensionProvider<ItemStack> provider, Class<? extends T> clazz) {
        this.itemStorageProviders.register(clazz, provider);
    }
}
