package it.einjojo.smpengine.listener;


import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.config.MaintenanceConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectionListener implements Listener {

    private final SMPEnginePlugin plugin;

    public ConnectionListener(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler()
    public void maintenanceCheck(PlayerJoinEvent event) {
        MaintenanceConfig maintenanceConfig = plugin.getMaintenanceConfig();
        if (maintenanceConfig.isEnabled() && !event.getPlayer().hasPermission(maintenanceConfig.getBypassPermission())) {
            Component message = MiniMessage.miniMessage().deserialize(
                    maintenanceConfig.getKickMessage()
            );

        }
    }


}
