package it.einjojo.smpengine.listener;

import io.papermc.paper.event.player.PlayerTradeEvent;
import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.session.SessionManager;
import it.einjojo.smpengine.core.stats.StatsImpl;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class StatsListener implements Listener {

    private final SMPEnginePlugin plugin;
    private final SessionManager sessionManager;

    public StatsListener(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.sessionManager = plugin.getSessionManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private StatsImpl getStats(UUID uuid) {
        return (StatsImpl) sessionManager.getSession(uuid).orElseThrow().getSessionStats();
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        var stats = getStats(event.getPlayer().getUniqueId());
        stats.setBlocksDestroyed(stats.getBlocksDestroyed() + 1);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        var stats = getStats(event.getPlayer().getUniqueId());
        stats.setBlocksPlaced(stats.getBlocksPlaced() + 1);
    }

    @EventHandler
    public void mobKills(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        if (event.getEntity().getType().equals(EntityType.PLAYER)) return;
        var stats = getStats(event.getEntity().getKiller().getUniqueId());
        stats.setMobKills(stats.getMobKills() + 1);
    }

    @EventHandler
    public void playerKillsDeath(PlayerDeathEvent event) {
        var dead = getStats(event.getEntity().getUniqueId());
        dead.setDeaths(dead.getDeaths() + 1);
        if (event.getEntity().getKiller() == null) return;
        var killer = getStats(event.getEntity().getKiller().getUniqueId());
        killer.setPlayerKills(killer.getPlayerKills() + 1);
    }


    @EventHandler
    public void onTrade(PlayerTradeEvent event) {
        var stats = getStats(event.getPlayer().getUniqueId());
        stats.setVillagerTrades(stats.getVillagerTrades() + 1);
    }

}
