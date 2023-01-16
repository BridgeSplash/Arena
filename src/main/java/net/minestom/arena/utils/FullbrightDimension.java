package net.minestom.arena.utils;

import net.minestom.server.MinecraftServer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

public class FullbrightDimension {
    public static final DimensionType INSTANCE_LOBBY = DimensionType.builder(NamespaceID.from("bridgesplash:arena_lobby"))
            .ambientLight(2.0f)
            .build();
    public static final DimensionType INSTANCE_ARENA = DimensionType.builder(NamespaceID.from("bridgesplash:arena_play"))
            .ambientLight(2.0f)
            .build();
    static {
        MinecraftServer.getDimensionTypeManager().addDimension(INSTANCE_LOBBY);
        MinecraftServer.getDimensionTypeManager().addDimension(INSTANCE_ARENA);
    }
}
