package it.einjojo.smpengine;

import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;

import java.util.Optional;
import java.util.UUID;

public class SMPEngineAPIProvider implements SMPEngineAPI {

    private final SMPEnginePlugin plugin;

    private static SMPEngineAPI instance;

    public SMPEngineAPIProvider(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        instance = this;
    }


    @Override
    public Optional<SMPPlayer> getPlayerByName(String name) {
        return plugin.getPlayerManager().getPlayer(name);
    }

    @Override
    public Optional<SMPPlayer> getPlayer(UUID id) {
        return plugin.getPlayerManager().getPlayer(id);
    }

    @Override
    public Optional<Team> getTeamByName(String name) {
        return plugin.getTeamManager().getTeamByName(name);
    }

    @Override
    public Optional<Team> getTeam(int id) {
        return plugin.getTeamManager().getTeamById(id);
    }

    public static SMPEngineAPI get() {
        return instance;
    }

}
