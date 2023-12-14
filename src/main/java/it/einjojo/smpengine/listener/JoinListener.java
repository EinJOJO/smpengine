package it.einjojo.smpengine.listener;


import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.config.MaintenanceConfig;
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

    public void loadPlayer(PlayerJoinEvent event) {

    }


}
