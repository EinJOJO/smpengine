package it.einjojo.smpengine.core.stats;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StatsImpl implements Stats{

    private final Integer sessionID;
    private final UUID uuid;
    private int blocksDestroyed;
    private int blocksPlaced;
    private int mobKills;
    private int playerKills;
    private int deaths;
    private int villagerTrades;

    private SMPEnginePlugin plugin;

    public StatsImpl(Integer sessionID, UUID uuid, int blocksDestroyed, int blocksPlaced, int mobKills, int playerKills, int deaths, int villagerTrades) {
        this.sessionID = sessionID;
        this.uuid = uuid;
        this.blocksDestroyed = blocksDestroyed;
        this.blocksPlaced = blocksPlaced;
        this.mobKills = mobKills;
        this.playerKills = playerKills;
        this.deaths = deaths;
        this.villagerTrades = villagerTrades;
    }

    @Override
    public SMPPlayer getPlayer() {
        return plugin.getPlayerManager().getPlayer(uuid).orElseThrow();
    }

    @Override
    public int getPlayTime() {
        return 0;
    }
}
