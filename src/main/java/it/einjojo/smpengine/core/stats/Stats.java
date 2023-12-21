package it.einjojo.smpengine.core.stats;

import it.einjojo.smpengine.core.player.SMPPlayer;

public interface Stats {

    SMPPlayer getPlayer();

    int getBlocksDestroyed();

    int getBlocksPlaced();

    int getMobKills();

    int getPlayerKills();

    int getDeaths();

    int getVillagerTrades();

    int getPlayTime();

}
