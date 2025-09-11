package com.hamusuke.jadespigot.addon;

import com.google.common.collect.Lists;
import com.hamusuke.jadespigot.accessors.Accessor;
import com.hamusuke.jadespigot.view.ServerExtensionProvider;
import com.hamusuke.jadespigot.view.ViewGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.TileEntityCampfire;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum CampfireProvider implements ServerExtensionProvider<ItemStack> {
    INSTANCE;

    public static final MinecraftKey MC_CAMPFIRE = MinecraftKey.b("campfire");
    private static final MapCodec<Integer> COOKING_TIME_CODEC = Codec.INT.fieldOf("jade:cooking");

    @Override
    public MinecraftKey getId() {
        return MC_CAMPFIRE;
    }

    @Override
    public @Nullable List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        if (accessor.getTarget() instanceof TileEntityCampfire campfire) {
            List<ItemStack> list = Lists.newArrayList();
            for (int i = 0; i < campfire.f.length; i++) {
                ItemStack stack = campfire.c().get(i);
                if (stack.f()) {
                    continue;
                }

                stack = stack.v();
                CustomData customData = stack.a(DataComponents.b, CustomData.a).a(
                        DynamicOpsNBT.a,
                        COOKING_TIME_CODEC,
                        campfire.f[i] - campfire.e[i]).getOrThrow();
                stack.b(DataComponents.b, customData);
                list.add(stack);
            }

            return List.of(new ViewGroup<>(list));
        }

        return null;
    }
}
