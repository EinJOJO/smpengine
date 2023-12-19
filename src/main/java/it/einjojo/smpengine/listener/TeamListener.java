package it.einjojo.smpengine.listener;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.event.TeamPlayerJoinEvent;
import it.einjojo.smpengine.event.TeamPlayerLeaveEvent;
import it.einjojo.smpengine.util.Placeholder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TeamListener implements Listener {

    private final SMPEnginePlugin plugin;

    public TeamListener(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void joinTeam(TeamPlayerJoinEvent event) {
        for (Player player : event.getTeam().getOnlineMembers()) {
            Component message = Placeholder.applyPlaceholders(plugin.getMessage("team.join"), Placeholder.player(event.getPlayer().getName()));
            player.sendMessage(message);
        }
        delayedUpdate();
    }

    @EventHandler
    public void leaveTeam(TeamPlayerLeaveEvent event) {
        for (Player player : event.getTeam().getOnlineMembers()) {
            Component message = Placeholder.applyPlaceholders(plugin.getMessage("team.leave"), Placeholder.player(event.getPlayer().getName()));
            player.sendMessage(message);
        }
        delayedUpdate();
    }

    public void delayedUpdate() {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                plugin.getTablistManager().update(player);
            }
        }, 10);
    }

}
