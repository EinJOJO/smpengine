package it.einjojo.smpengine.core.team;

import it.einjojo.smpengine.SMPEnginePlugin;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class TeamManager {

    private final SMPEnginePlugin plugin;

    public TeamManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    public static void create(String name, UUID owner_uuid) {
    }

    public Optional<Team> getTeam(int teamId) {
        return Optional.empty();
    }

}
