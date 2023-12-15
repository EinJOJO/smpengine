package it.einjojo.smpengine.core.session;

import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.stats.Stats;

import java.time.Instant;

public interface Session {

    /**
     * @return the stats of the session.
     */
    Stats getSessionStats();

    /**
     * @return the player of the session.
     * @see SMPPlayer
     */
    SMPPlayer getPlayer();

    /**
     * @return the time when the session started.
     */
    Instant getStartTime();

    /**
     * @return the time when the session ended, or null if session is still active.
     */
    Instant getEndTime();

    /**
     * @return the ip of the player when the session started.
     */
    String getIp();

    /**
     * @return true if session is active, false if session is ended.
     */
    boolean isActive();
}
