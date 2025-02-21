package net.minestom.arena.game;

import net.minestom.arena.feature.Feature;
import net.minestom.arena.lobby.LobbySidebarDisplay;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public interface SingleInstanceArena extends Arena {
    @NotNull Instance instance();

    @NotNull Pos spawnPosition(@NotNull Player player);

    @NotNull List<Feature> features();

    @Override
    default @NotNull CompletableFuture<Void> init() {
        Instance instance = instance();
        // Register this arena
        MinecraftServer.getInstanceManager().registerInstance(instance);

        instance.eventNode().addListener(RemoveEntityFromInstanceEvent.class, event -> {
            // We don't care about entities, only players.
            if (!(event.getEntity() instanceof Player)) return;
            // Ensure there is only this player in the instance
            if (instance.getPlayers().size() > 1) return;
            // All players have left. We can remove this instance once the player is removed.
            MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
                MinecraftServer.getInstanceManager().unregisterInstance(instance);
                stop();
            });

            group().setDisplay(new LobbySidebarDisplay(group()));
        });

        for (Feature feature : features()) {
            feature.hook(instance.eventNode());
        }

        CompletableFuture<?>[] futures =
                group().members().stream()
                        .map(uuid -> MinecraftServer.getConnectionManager().getPlayer(uuid))
                        .filter(Objects::nonNull)
                        .map(player -> player.setInstance(instance, spawnPosition(player)))
                        .toArray(CompletableFuture<?>[]::new);

        return CompletableFuture.allOf(futures).thenRun(this::start);
    }
}
