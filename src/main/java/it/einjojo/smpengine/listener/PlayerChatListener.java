package it.einjojo.smpengine.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import it.einjojo.smpengine.SMPEnginePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerChatListener implements Listener {

    private static final Component ARROW = Component.text("»").color(NamedTextColor.DARK_GRAY);

    public PlayerChatListener(SMPEnginePlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Component message = event.getPlayer().teamDisplayName()
                .appendSpace()
                .append(ARROW)
                .appendSpace()
                .append(event.originalMessage());
        event.setCancelled(true);
        Bukkit.getServer().sendMessage(message);
    }

}
