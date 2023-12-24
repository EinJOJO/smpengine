package it.einjojo.smpengine.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import it.einjojo.smpengine.SMPEnginePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MOTDListener implements Listener {


    private static final String defaultMOTD = " <bold>[ <gradient:#FDC830:#F37335>einjojo.it</gradient> ]</bold>     <bold><yellow> 1.20.4</yellow></bold>     <gold>Winter 2024 Projekt</gold><newline>                    <green>SMPE-Version: </green><dark_green><version></dark_green>  ";
    private static final Component maintenanceMOTD = MiniMessage.miniMessage().deserialize(" <bold>[ <gradient:#FDC830:#F37335>einjojo.it</gradient> ]</bold>     <bold><yellow> 1.20.4</yellow></bold>     <gold>Winter 2024 Projekt</gold><newline>                    <red>Wartungsarbeiten</red>");
    private final String version;
    private final SMPEnginePlugin plugin;

    public MOTDListener(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.version = plugin.getPluginMeta().getVersion();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMOTD(PaperServerListPingEvent event) {
        if (plugin.getMaintenanceConfig().isEnabled()) {
            event.motd(maintenanceMOTD);
            event.setMaxPlayers(0);
            event.setVersion("§cMaintenance");
        } else {
            Component motd = MiniMessage.miniMessage().deserialize(defaultMOTD, Placeholder.unparsed("version", version));
            event.motd(motd);
        }
    }

}
