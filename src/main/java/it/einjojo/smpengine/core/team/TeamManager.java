package it.einjojo.smpengine.core.team;

import it.einjojo.smpengine.SMPEnginePlugin;

import java.util.Optional;

public class TeamManager {

    private final SMPEnginePlugin plugin;

    public TeamManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    public Optional<Team> getTeam(int teamId) {
        return Optional.empty();
    }

}
