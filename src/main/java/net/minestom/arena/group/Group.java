package net.minestom.arena.group;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.minestom.arena.group.displays.GroupDisplay;
import net.minestom.arena.group.manager.GroupManagerAbstract;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public sealed interface Group extends ForwardingAudience permits BlankGroup, PartyGroupImpl {
    static Group findGroup(@NotNull Player player) {
        Group group = GroupManagerAbstract.instance.getGroup(player);
        if(group == null) return new BlankGroup(player);
        return group;
    }

    @NotNull UUID leader();

    @NotNull List<@NotNull UUID> members();

    @NotNull GroupDisplay display();

    void setDisplay(@NotNull GroupDisplay display);

    void addPlayer(@NotNull Player player);
    void removePlayer(@NotNull Player player);


    @Override
    default @NotNull Iterable<? extends Audience> audiences() {
        return members().stream().map(uuid ->
                MinecraftServer.getConnectionManager().getPlayer(uuid)
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
