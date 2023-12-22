package it.einjojo.smpengine.core.stats;

import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.session.Session;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface Stats {

    UUID getUuid();
    SMPPlayer getPlayer();
    Optional<Session> getSession();

    int getBlocksDestroyed();

    int getBlocksPlaced();

    int getMobKills();

    int getPlayerKills();

    int getDeaths();

    int getVillagerTrades();

    Instant getPlayTime();

}
