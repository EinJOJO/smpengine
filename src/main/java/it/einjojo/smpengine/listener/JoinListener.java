package it.einjojo.smpengine.listener;


import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.config.MaintenanceConfig;
import it.einjojo.smpengine.core.player.SMPPlayerImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final SMPEnginePlugin plugin;

    public JoinListener(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // First check
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void maintenanceCheck(PlayerJoinEvent event) {
        MaintenanceConfig maintenanceConfig = plugin.getMaintenanceConfig();
        if (maintenanceConfig.isEnabled() && !event.getPlayer().hasPermission(maintenanceConfig.getBypassPermission())) {
            Component message = MiniMessage.miniMessage().deserialize(
                    maintenanceConfig.getKickMessage()
            );

            event.getPlayer().kick(message);
        }
    }

    @EventHandler
    public void loadPlayer(PlayerJoinEvent event) {
        plugin.getPlayerManager().getPlayerAsync(event.getPlayer().getUniqueId()).thenAccept(smpPlayer -> {
            if (smpPlayer.isEmpty()) {
                var optionalPlayer = plugin.getPlayerManager().createPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
                if (optionalPlayer.isEmpty()) {
                    event.getPlayer().kick(plugin.getMessage("error.player-creation-failed"));
                    return;
                }
                smpPlayer = optionalPlayer;
            }
            var smpPlayerImpl = (SMPPlayerImpl) smpPlayer.get();
            smpPlayerImpl.setOnline(true);
            smpPlayerImpl.setLastJoin(smpPlayerImpl.getLastJoin());
            smpPlayerImpl.setName(event.getPlayer().getName());
            plugin.getPlayerManager().updatePlayerAsync(smpPlayerImpl);
        });
    }


}
