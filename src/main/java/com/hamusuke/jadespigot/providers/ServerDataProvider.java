package com.hamusuke.jadespigot.providers;

import com.hamusuke.jadespigot.JadeIdProvider;
import com.hamusuke.jadespigot.accessors.Accessor;
import net.minecraft.nbt.NBTTagCompound;

public interface ServerDataProvider<T extends Accessor<?>> extends JadeIdProvider {
    void appendServerData(NBTTagCompound data, T accessor);
}
