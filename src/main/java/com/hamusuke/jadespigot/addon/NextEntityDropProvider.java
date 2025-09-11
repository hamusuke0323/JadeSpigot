package com.hamusuke.jadespigot.addon;

import com.hamusuke.jadespigot.Utils;
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
            if (!chicken.g_() && chicken.cq < max) {
                tag.a("NextEggIn", chicken.cq);
            }
        } else if (accessor.getEntity() instanceof Armadillo armadillo) {
            if (!armadillo.g_()) {
                return;
            }

            final int scuteTime = Utils.retrieveFieldValue("cv", armadillo, -1);
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
