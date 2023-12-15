package it.einjojo.smpengine.listener;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.util.Placeholder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final SMPEnginePlugin plugin;

    public DeathListener(SMPEnginePlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        int x = (int) event.getPlayer().getX();
        int y = (int) event.getPlayer().getY();
        int z = (int) event.getPlayer().getZ();
        Placeholder x1 = new Placeholder("x", Integer.toString(x));
        Placeholder y1 = new Placeholder("y", Integer.toString(y));
        Placeholder z1 = new Placeholder("z", Integer.toString(z));
        Component component = plugin.getMessage("death.global");
        Player p = event.getPlayer();
        event.deathMessage(Placeholder.applyPlaceholders(component, Placeholder.player(p.getName())));
        Component component1 = plugin.getMessage("death.player");
        p.sendMessage(Placeholder.applyPlaceholders(component1, x1, y1, z1));

    }
}
