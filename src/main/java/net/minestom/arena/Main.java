package net.minestom.arena;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.arena.game.ArenaCommand;
import net.minestom.arena.game.mob.MobTestCommand;
import net.minestom.arena.group.manager.GroupManagerAbstract;
import net.minestom.arena.group.manager.PartyGroupManager;
import net.minestom.arena.lobby.Lobby;
import net.minestom.arena.utils.ResourceUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.MathUtils;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import static net.minestom.arena.config.ConfigHandler.CONFIG;

final class Main extends Extension {

    @Override
    public void initialize() {
        try {
            ResourceUtils.extractResource("lobby");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        GroupManagerAbstract.instance = new PartyGroupManager();

        // Commands
        {
            CommandManager manager = MinecraftServer.getCommandManager();
            manager.setUnknownCommandCallback((sender, c) -> Messenger.warn(sender, "Command not found."));
            manager.register(new ArenaCommand());
            manager.register(new MobTestCommand());
            SimpleCommands.register(manager);
        }

        // Events
        {
            GlobalEventHandler handler = MinecraftServer.getGlobalEventHandler();

            // Server list
            ServerList.hook(handler);
            PartyGroupManager.register(handler);

            // Login
            handler.addListener(PlayerLoginEvent.class, event -> {
                final Player player = event.getPlayer();
                event.setSpawningInstance(Lobby.INSTANCE);
                player.setRespawnPoint(new Pos(0.5, 16, 0.5));

                if (CONFIG.permissions().operators().contains(player.getUsername())) {
                    player.setPermissionLevel(4);
                }

                Audiences.all().sendMessage(Component.text(
                        player.getUsername() + " has joined",
                        NamedTextColor.GREEN
                ));
            });

            handler.addListener(PlayerSpawnEvent.class, event -> {
                if (!event.isFirstSpawn()) return;
                final Player player = event.getPlayer();
                Messenger.info(player, "Welcome to BridgeSplash Arena, use /arena to play!");
                player.setGameMode(GameMode.ADVENTURE);
                player.playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.MASTER, 1f, 1f));
                player.setEnableRespawnScreen(false);
            });

            // Logout
            handler.addListener(PlayerDisconnectEvent.class, event -> Audiences.all().sendMessage(Component.text(
                    event.getPlayer().getUsername() + " has left",
                    NamedTextColor.RED
            )));

            // Chat
            handler.addListener(PlayerChatEvent.class, chatEvent -> {
                chatEvent.setChatFormat((event) -> Component.text(event.getEntity().getUsername())
                        .append(Component.text(" | ", NamedTextColor.DARK_GRAY)
                                .append(Component.text(event.getMessage(), NamedTextColor.WHITE))));
            });

            // Monitoring
            AtomicReference<TickMonitor> lastTick = new AtomicReference<>();
            handler.addListener(ServerTickMonitorEvent.class, event -> {
                final TickMonitor monitor = event.getTickMonitor();
                lastTick.set(monitor);
            });
            MinecraftServer.getExceptionManager().setExceptionHandler(e -> {
                getLogger().error("Global exception handler", e);
            });

            // Header/footer
            MinecraftServer.getSchedulerManager().scheduleTask(() -> {
                Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
                if (players.isEmpty()) return;

                final Runtime runtime = Runtime.getRuntime();
                final TickMonitor tickMonitor = lastTick.get();
                final long ramUsage = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;

                final Component header = Component.newline()
                        .append(Component.text("BridgeSplash Arena", Messenger.PINK_COLOR))
                        .append(Component.newline()).append(Component.text("Players: " + players.size()))
                        .append(Component.newline()).append(Component.newline())
                        .append(Component.text("RAM USAGE: " + ramUsage + " MB", NamedTextColor.GRAY).append(Component.newline())
                                .append(Component.text("TICK TIME: " + MathUtils.round(tickMonitor.getTickTime(), 2) + "ms", NamedTextColor.GRAY))).append(Component.newline());
                final Component footer = Component.newline().append(Component.text("Project: minestom.net").append(Component.newline())
                                .append(Component.text("    Source: github.com/Minestom/Minestom    ", Messenger.ORANGE_COLOR))
                                .append(Component.newline())
                                .append(Component.text("Arena: github.com/Minestom/Arena", Messenger.ORANGE_COLOR))
                                .append(Component.newline())
                                .append(Component.text("Forked From: github.com/Minestom/Arena", Messenger.ORANGE_COLOR)))
                        .append(Component.newline());

                Audiences.players().sendPlayerListHeaderAndFooter(header, footer);

            }, TaskSchedule.tick(10), TaskSchedule.tick(10));

            getLogger().info("Server startup done! Using configuration " + CONFIG);
        }
    }

    @Override
    public void terminate() {
        getLogger().info("Terminating...");
    }
}
