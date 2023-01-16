package net.minestom.arena.group;

import de.simonsator.partyandfriends.minestom.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.minestom.api.party.PlayerParty;
import net.minestom.arena.group.displays.GroupDisplay;
import net.minestom.arena.group.manager.PartyGroupManager;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class PartyGroupImpl implements Group {

    private final UUID leader;
    private GroupDisplay display;


    public PartyGroupImpl(@NotNull PlayerParty party) {
        this.leader = party.getLeader().getUniqueId();
    }

    @Override
    public @NotNull UUID leader() {
        PlayerParty party = party();
        if(party == null) return this.leader;
        return party.getLeader().getUniqueId();
    }

    @Override
    public @NotNull List<@NotNull UUID> members() {
        return party().getAllPlayers().stream().map(PAFPlayer::getUniqueId).toList();
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

    private PlayerParty party() {
        final PAFPlayer pafPlayer = PartyGroupManager.playerManager.getPlayer(this.leader);
        return PartyGroupManager.partyManager.getParty(pafPlayer);
    }
}
