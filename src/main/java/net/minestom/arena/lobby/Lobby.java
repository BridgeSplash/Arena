package net.minestom.arena.lobby;

import net.minestom.arena.config.ConfigHandler;
import net.minestom.arena.group.Group;
import net.minestom.arena.utils.FullbrightDimension;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.tag.Tag;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.nio.file.Path;

public final class Lobby {
    public static final Instance INSTANCE;

    static  {
        final Instance instance = new InstanceContainer(ConfigHandler.CONFIG.server().getLobbyInstanceUUID(),
                FullbrightDimension.INSTANCE_LOBBY, new AnvilLoader(Path.of("lobby")));
        MinecraftServer.getInstanceManager().registerInstance(instance);
        instance.setTag(Tag.Boolean("no_unload_instance"), true);

        Map.create(instance, new Pos(2, 18, 9));
        instance.setTimeRate(0);

        instance.eventNode().addListener(AddEntityToInstanceEvent.class, event -> {
            if (!(event.getEntity() instanceof Player player)) return;

            if (player.getInstance() != null) player.scheduler().scheduleNextTick(() -> onArenaFinish(player));
            else onFirstSpawn(player);
        }).addListener(ItemDropEvent.class, event -> event.setCancelled(true));

        INSTANCE = instance;
    }

    private static void onFirstSpawn(Player player) {
        player.sendPackets(Map.packets());

        final Group group = Group.findGroup(player);
        group.setDisplay(new LobbySidebarDisplay(group));
    }

    private static void onArenaFinish(Player player) {
        player.refreshCommands();
        player.getInventory().clear();
        player.teleport(new Pos(0.5, 16, 0.5));
        player.tagHandler().updateContent(NBTCompound.EMPTY);
    }
}
