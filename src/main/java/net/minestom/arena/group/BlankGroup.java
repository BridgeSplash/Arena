package net.minestom.arena.group;

import net.minestom.arena.group.displays.GroupDisplay;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class BlankGroup implements Group{


    private final UUID leader;
    private GroupDisplay display;

    public BlankGroup(@NotNull Player player) {
        leader = player.getUuid();
    }

    @Override
    public @NotNull UUID leader() {
        return leader;
    }

    @Override
    public @NotNull List<@NotNull UUID> members() {
        return List.of(leader);
    }

    @Override
    public @NotNull GroupDisplay display() {
        return display;
    }

    @Override
    public void setDisplay(@NotNull GroupDisplay display) {
        if (this.display != null) this.display.clean();
        this.display = display;
        display.update();
    }

    @Override
    public void addPlayer(@NotNull Player player) {
        display.update();
    }

    @Override
    public void removePlayer(@NotNull Player player) {
        display.update();
    }
}
