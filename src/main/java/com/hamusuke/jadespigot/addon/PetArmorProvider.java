package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.EntityAccessor;
import com.hamusuke.jadespigot.providers.StreamServerDataProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum PetArmorProvider implements StreamServerDataProvider<EntityAccessor, ItemStack> {
    INSTANCE;

    public static final MinecraftKey MC_PET_ARMOR = MinecraftKey.b("pet_armor");

    @Override
    public @Nullable ItemStack streamData(EntityAccessor accessor) {
        final var armor = ((EntityInsentient) accessor.getEntity()).gl();
        return armor.f() ? null : armor;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemStack> streamCodec() {
        return ItemStack.h;
    }

    @Override
    public MinecraftKey getId() {
        return MC_PET_ARMOR;
    }
}
