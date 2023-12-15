package it.einjojo.smpengine.listener;


import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.config.MaintenanceConfig;
import it.einjojo.smpengine.core.player.SMPPlayerImpl;
import it.einjojo.smpengine.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.CompletableFuture;

public class JoinListener implements Listener {

    private final SMPEnginePlugin plugin;

    public JoinListener(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // First check
    @EventHandler()
    public void maintenanceCheck(PlayerJoinEvent event) {
        MaintenanceConfig maintenanceConfig = plugin.getMaintenanceConfig();
        if (maintenanceConfig.isEnabled() && !event.getPlayer().hasPermission(maintenanceConfig.getBypassPermission())) {
            syncKick(event, MessageUtil.format(maintenanceConfig.getKickMessage(), plugin.getPrimaryColor(), plugin.getPrefix()));
        }
    }

    @EventHandler
    public void loadPlayer(PlayerJoinEvent event) {
        CompletableFuture.supplyAsync(() -> plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId())).thenAccept(smpPlayer -> {
            if (smpPlayer.isEmpty()) {
                var optionalPlayer = plugin.getPlayerManager().createPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
                if (optionalPlayer.isEmpty()) {
                    plugin.getLogger().warning("Failed to create player " + event.getPlayer().getName() + " (" + event.getPlayer().getUniqueId() + ")");
                    syncKick(event, plugin.getMessage("error.player-creation-failed"));
                    return;
                }
                smpPlayer = optionalPlayer;
            }
            var smpPlayerImpl = (SMPPlayerImpl) smpPlayer.get();
            smpPlayerImpl.setOnline(true);
            smpPlayerImpl.setLastJoin(smpPlayerImpl.getLastJoin());
            smpPlayerImpl.setName(event.getPlayer().getName());
            CompletableFuture.runAsync(() -> plugin.getPlayerManager().updatePlayer(smpPlayerImpl));
        });
    }

    private void syncKick(PlayerJoinEvent event, Component kickMessage) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            event.getPlayer().kick(kickMessage);
        });
    }


}
