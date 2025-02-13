package com.hamusuke.jadespigot;

import net.minecraft.resources.MinecraftKey;

public interface JadeIdProvider {
    MinecraftKey getId();

    default int getDefaultPriority() {
        return 0;
    }
}
