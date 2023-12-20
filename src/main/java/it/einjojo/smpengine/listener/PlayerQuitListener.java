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
        plugin.getPlayerManager().getPlayerAsync(event.getPlayer().getUniqueId()).thenAcceptAsync((oSMPPlayer) -> oSMPPlayer.ifPresentOrElse((smpPlayer) -> {
            ((SMPPlayerImpl) smpPlayer).setOnline(false);
            plugin.getSessionManager().endSession(smpPlayer);
            plugin.getPlayerManager().updatePlayer(smpPlayer);
            plugin.getPlayerManager().updatePlayer(smpPlayer);
        }, () -> plugin.getLogger().warning("Player " + event.getPlayer().getName() + " is not in database. Failed to update online mode on quit.")));

    }
}
