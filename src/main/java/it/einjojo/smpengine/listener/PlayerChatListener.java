package it.einjojo.smpengine.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class PlayerChatListener implements Listener {

    private final SMPEnginePlugin plugin;

    public PlayerChatListener(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event){
        if(!event.isCancelled()){
            event.setCancelled(true);
            Optional<SMPPlayer> player = plugin.getPlayerManager().getPlayer(String.valueOf(event.getPlayer()));
            if(player.get().getTeam().isEmpty()){
                Bukkit.broadcast(Component.text(""));
                return;
            }

        }
    }

}
