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
        // synchronously update player data because when server shuts down, it will not save player data
        SMPPlayerImpl smpPlayer = (SMPPlayerImpl) plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId()).orElseThrow();
        smpPlayer.setOnline(false);
        plugin.getSessionManager().endSession(smpPlayer);
        plugin.getPlayerManager().updatePlayer(smpPlayer);
    }
}
