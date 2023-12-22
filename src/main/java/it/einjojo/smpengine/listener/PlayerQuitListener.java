package it.einjojo.smpengine.listener;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayerImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final SMPEnginePlugin plugin;

    public PlayerQuitListener(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuitDataHandler(PlayerQuitEvent event) {
        if (plugin.isShuttingDown()) return;
        plugin.getPlayerManager().getPlayerAsync(event.getPlayer().getUniqueId())
                .thenAccept(optionalPlayer -> {
                    if (optionalPlayer.isEmpty()) {
                        plugin.getLogger().warning("Player data not found on quit");
                        return;
                    }

                    SMPPlayerImpl smpPlayer = (SMPPlayerImpl) optionalPlayer.get();
                    smpPlayer.setOnline(false);

                    smpPlayer.getSessionAsync().thenAcceptAsync(optionalSession -> {
                        if (optionalSession.isEmpty()) {
                            plugin.getLogger().warning("Session data not found for player on quit");
                            return;
                        }

                        plugin.getStatsManager().updateStats(optionalSession.get().getSessionStats());
                    }).exceptionally(throwable -> {
                        plugin.getLogger().warning("Failed to update player stats on quit: " + throwable.getMessage());
                        return null;
                    });

                    plugin.getSessionManager().endSession(smpPlayer);
                    plugin.getPlayerManager().updatePlayer(smpPlayer);
                }).exceptionally(throwable -> {
                    plugin.getLogger().warning("Failed to update player data on quit: " + throwable.getMessage());
                    return null;
                });
    }
}
