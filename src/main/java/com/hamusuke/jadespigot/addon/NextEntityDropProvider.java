package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.accessors.EntityAccessor;
import com.hamusuke.jadespigot.providers.ServerDataProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.animal.EntityChicken;
import net.minecraft.world.entity.animal.armadillo.Armadillo;

public enum NextEntityDropProvider implements ServerDataProvider<EntityAccessor> {
    INSTANCE;

    public static final MinecraftKey MC_NEXT_ENTITY_DROP = MinecraftKey.b("next_entity_drop");

    @Override
    public void appendServerData(NBTTagCompound tag, EntityAccessor accessor) {
        int max = 24000 * 2;
        if (accessor.getEntity() instanceof EntityChicken chicken) {
            if (!chicken.e_() && chicken.ce < max) {
                tag.a("NextEggIn", chicken.ce);
            }
        } else if (accessor.getEntity() instanceof Armadillo armadillo) {
            if (armadillo.e_()) {
                return;
            }

            var compound = new NBTTagCompound();
            armadillo.b(compound);
            var scuteTime = compound.h("scute_time");
            if (scuteTime < max) {
                tag.a("NextScuteIn", scuteTime);
            }
        }
    }

    @Override
    public MinecraftKey getId() {
        return MC_NEXT_ENTITY_DROP;
    }
}
