package com.hamusuke.jadespigot.accessors;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

public interface EntityAccessor extends Accessor<MovingObjectPositionEntity> {
    Entity getEntity();

    Entity getRawEntity();

    interface Builder {
        Builder level(World level);

        Builder player(Player player);

        Builder showDetails(boolean showDetails);

        Builder hit(Supplier<MovingObjectPositionEntity> hit);

        default Builder entity(Entity entity) {
            return entity(() -> entity);
        }

        Builder entity(Supplier<Entity> entity);

        EntityAccessor build();
    }
}
