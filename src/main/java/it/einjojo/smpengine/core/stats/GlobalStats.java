package it.einjojo.smpengine.core.stats;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
public class GlobalStats extends StatsImpl{

    private Instant playtimeMillis;
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
