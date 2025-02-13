package com.hamusuke.jadespigot.view;

import com.hamusuke.jadespigot.JadeIdProvider;
import com.hamusuke.jadespigot.accessors.Accessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ServerExtensionProvider<T> extends JadeIdProvider {
    @Nullable List<ViewGroup<T>> getGroups(Accessor<?> accessor);
}
