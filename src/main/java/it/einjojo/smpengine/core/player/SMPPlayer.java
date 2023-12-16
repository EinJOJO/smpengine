package it.einjojo.smpengine.core.player;

import it.einjojo.smpengine.core.team.Team;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SMPPlayer {

    UUID getUuid();
    Player getPlayer();

    String getName();

    boolean isOnline();

    Instant getFirstJoin();

    Instant getLastJoin();

    Optional<Team> getTeam();




}
