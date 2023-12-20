package it.einjojo.smpengine.core.player;

import it.einjojo.smpengine.core.team.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SMPPlayer {

    /**
     * @return {@link UUID} of player
     */
    UUID getUuid();


    /**
     * @return {@link Player} or null if player is offline.
     */
    @Nullable
    Player getPlayer();

    /**
     * @return name of player
     */
    String getName();

    /**
     * @return true if player is online
     */
    boolean isOnline();

    /**
     * @return {@link Instant} of first join
     */
    Instant getFirstJoin();

    /**
     * @return {@link Instant} of last join
     */
    Instant getLastJoin();

    /**
     * @return {@link Optional<Team>} or {@link Optional#empty()} if player is not in a team.
     */
    Optional<Team> getTeam();

    /**
     * @return true if player is in a team
     */
    boolean isInsideTeam();


    /**
     * @return {@link CompletableFuture} with {@link Optional<Team>} or {@link Optional#empty()} if player is not in a team.
     */
    CompletableFuture<Optional<Team>> getTeamAsync();


}
