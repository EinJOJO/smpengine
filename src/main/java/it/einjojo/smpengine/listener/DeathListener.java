package it.einjojo.smpengine.listener;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.util.Placeholder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private SMPEnginePlugin plugin;

    public DeathListener(SMPEnginePlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Component component = plugin.getMessage("death.message.global");
        Player p = event.getPlayer();
        event.deathMessage(Placeholder.applyPlaceholders(component, Placeholder.player(p.getName())));

    }
}
