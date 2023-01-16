package net.minestom.arena.config;

import java.util.List;
import java.util.UUID;

public record Config(Server server, Permissions permissions) {
    public record Server(@Default("[\"Line1\",\"Line2\"]") List<String> motd, @Default("60fd730b-779c-4498-bcfd-7b17087ad2bd") String lobbyInstanceUUID){

        public UUID getLobbyInstanceUUID() {
            return UUID.fromString(lobbyInstanceUUID);
        }

    }

    public record Permissions(@Default("[]") List<String> operators) {}

}
