package it.einjojo.smpengine;

import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SMPEngineAPI {

    Optional<SMPPlayer> getPlayerByName(String name);

    CompletableFuture<Optional<SMPPlayer>> getPlayerByNameAsync(String name);

    Optional<SMPPlayer> getPlayer(UUID id);

    CompletableFuture<Optional<SMPPlayer>> getPlayerAsync(UUID id);

    Optional<Team> getTeamByName(String name);

    CompletableFuture<Optional<Team>> getTeamByNameAsync(String name);

    Optional<Team> getTeam(int id);

    CompletableFuture<Optional<Team>> getTeamAsync(int id);


}
