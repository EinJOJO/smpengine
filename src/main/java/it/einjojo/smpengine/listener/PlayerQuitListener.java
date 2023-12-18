package it.einjojo.smpengine.listener;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayerImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerQuitListener implements Listener {

    private final SMPEnginePlugin plugin;

    public PlayerQuitListener(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuitDataHandler(PlayerQuitEvent event) {
        CompletableFuture.supplyAsync(() -> plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId()))
                .thenAccept((optionalPlayer) -> {
                    if (optionalPlayer.isEmpty()) {
                        throw new IllegalStateException("SMPPlayer " + event.getPlayer().getName() + " (" + event.getPlayer().getUniqueId() + ") is empty!");
                    }
                    SMPPlayerImpl smpPlayer = (SMPPlayerImpl) optionalPlayer.get();
                    smpPlayer.setOnline(false);
                    CompletableFuture.runAsync(() -> {
                        plugin.getSessionManager().endSession(smpPlayer);
                        plugin.getPlayerManager().updatePlayer(smpPlayer);
                    });
                });
    }
}
