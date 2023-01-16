package net.minestom.arena.group.manager;

import de.simonsator.partyandfriends.minestom.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.minestom.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.minestom.api.party.PartyManager;
import de.simonsator.partyandfriends.minestom.api.party.PlayerParty;
import net.minestom.arena.group.Group;
import net.minestom.arena.group.PartyGroupImpl;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PartyGroupManager extends GroupManagerAbstract {

    public static final PartyManager partyManager = PartyManager.getInstance();
    public static final PAFPlayerManager playerManager = PAFPlayerManager.getInstance();

    @Override
    public @Nullable Group getGroup(@NotNull Player player) {
        final PAFPlayer pafPlayer = playerManager.getPlayer(player.getUuid());
        final PlayerParty playerParty = partyManager.getParty(pafPlayer);
        if(playerParty == null || !playerParty.isInParty(pafPlayer)) {
            return null;
        }
        return new PartyGroupImpl(playerParty);
    }

    @Override
    public @Nullable Group getMemberGroup(@NotNull Player player) {
        return getGroup(player);
    }

    public static void register(EventNode<Event> events){
        events.addListener(PlayerDisconnectEvent.class, playerDisconnectEvent -> {
            final Player player = playerDisconnectEvent.getPlayer();
            final PAFPlayer pafPlayer = playerManager.getPlayer(player.getUuid());
            final PlayerParty playerParty = partyManager.getParty(pafPlayer);
            if(playerParty == null || !playerParty.isAMember(pafPlayer)) return;
        });
        events.addListener(PlayerSpawnEvent.class, playerSpawnEvent -> {
            if(!playerSpawnEvent.isFirstSpawn())return;
            final Player player = playerSpawnEvent.getPlayer();
            final PAFPlayer pafPlayer = playerManager.getPlayer(player.getUuid());
            final PlayerParty playerParty = partyManager.getParty(pafPlayer);
            if(playerParty == null || !playerParty.isAMember(pafPlayer)) return;

        });
    }


}
