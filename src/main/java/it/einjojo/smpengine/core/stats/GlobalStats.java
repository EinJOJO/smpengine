package it.einjojo.smpengine.core.stats;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class GlobalStats extends StatsImpl{

    private final Instant playtimeMillis;
    private final int logins;

    public GlobalStats(UUID uuid, int blocksDestroyed, int blocksPlaced, int mobKills, int playerKills, int deaths, int villagerTrades, Instant playtimeMillis, int logins){
        super(null, uuid, blocksDestroyed, blocksPlaced, mobKills, playerKills, deaths, villagerTrades);
        this.playtimeMillis = playtimeMillis;
        this.logins = logins;
    }

    @Override
    public Instant getPlayTime() {
        return playtimeMillis;
    }

}
