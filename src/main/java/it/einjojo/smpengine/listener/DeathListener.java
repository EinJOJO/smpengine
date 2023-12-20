package it.einjojo.smpengine.listener;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.util.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final SMPEnginePlugin plugin;

    public DeathListener(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player p = event.getPlayer();
        Placeholder x1 = new Placeholder("x", String.valueOf((int) event.getPlayer().getZ()));
        Placeholder y1 = new Placeholder("y", String.valueOf((int) event.getPlayer().getX()));
        Placeholder z1 = new Placeholder("z", String.valueOf((int) event.getPlayer().getZ()));
        event.deathMessage(plugin.getPrefix().appendSpace().append(event.deathMessage()));
        p.sendMessage(plugin.getMessage("death.player", x1, y1, z1));

    }
}
