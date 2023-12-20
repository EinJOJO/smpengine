package it.einjojo.smpengine;

import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;

import java.util.Optional;
import java.util.UUID;

public interface SMPEngineAPI {

    Optional<SMPPlayer> getPlayerByName(String name);
    Optional<SMPPlayer> getPlayer(UUID id);
    Optional<Team> getTeamByName(String name);
    Optional<Team> getTeam(int id);




}
