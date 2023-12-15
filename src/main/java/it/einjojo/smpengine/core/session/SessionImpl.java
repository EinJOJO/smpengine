package it.einjojo.smpengine.core.session;

import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.stats.Stats;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
public class SessionImpl implements Session {

    private Instant startTime;
    private Instant endTime;
    private String ip;
    private UUID uuid;


    @Override
    public Stats getSessionStats() {
        return null;
    }

    @Override
    public SMPPlayer getPlayer() {
        return null;
    }

}
