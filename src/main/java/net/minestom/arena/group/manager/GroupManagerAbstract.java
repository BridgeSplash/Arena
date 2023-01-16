package net.minestom.arena.group.manager;

import net.minestom.arena.group.Group;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract sealed class GroupManagerAbstract permits PartyGroupManager {

    public abstract @Nullable Group getGroup(@NotNull Player player);

    public abstract @Nullable Group getMemberGroup(@NotNull Player player);


    public static GroupManagerAbstract instance;
}
