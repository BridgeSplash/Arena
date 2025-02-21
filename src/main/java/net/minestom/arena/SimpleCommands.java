package net.minestom.arena;

import net.minestom.arena.config.ConfigHandler;
import net.minestom.arena.lobby.Lobby;
import net.minestom.arena.utils.CommandUtils;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

import java.util.List;

/**
 * Place for commands with little to no complexity
 */
final class SimpleCommands {
    private SimpleCommands() {}

    public static void register(CommandManager manager) {
        for (Command command : commands())
            manager.register(command);
    }

    private static List<Command> commands() {
        final Command ping = new Command("ping", "latency");
        ping.setDefaultExecutor((sender, context) -> {
            final Player player = (Player) sender;
            Messenger.info(player, "Your ping is " + player.getLatency() + "ms");
        });

        final Command leave = new Command("leave", "l");
        leave.setCondition(CommandUtils::arenaOnly);
        leave.setDefaultExecutor((sender, context) -> {
            final Player player = (Player) sender;
            player.setInstance(Lobby.INSTANCE);
            player.setHealth(player.getMaxHealth());
        });

        final Command reload = new Command("reloadArena");
        reload.setCondition(CommandUtils::consoleOnly);
        reload.setDefaultExecutor(((sender, context) -> ConfigHandler.loadConfig()));

        return List.of(ping, leave, reload);
    }
}
