package it.einjojo.smpengine.core.session;

import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.stats.Stats;

import java.time.Instant;

public interface Session {
    Stats getSessionStats();
    SMPPlayer getPlayer();
    Instant getStartTime();
    Instant getEndTime();
    String getIp();
}
