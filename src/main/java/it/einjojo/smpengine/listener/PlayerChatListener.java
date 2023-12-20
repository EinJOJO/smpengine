package it.einjojo.smpengine.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.team.ChatSubCommand;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Optional;

public class PlayerChatListener implements Listener {

    private static final Component ARROW = Component.text("»").color(NamedTextColor.DARK_GRAY);
    private static final Component tcPrefix = MiniMessage.miniMessage().deserialize("<gray>[<red>Teamchat</red>]</gray>");
    private final SMPEnginePlugin plugin;

    public PlayerChatListener(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {

        boolean toggled = ChatSubCommand.TEAM_CHAT_STATUS.getOrDefault(event.getPlayer().getUniqueId(), false);
        Component message = event.getPlayer().teamDisplayName()
                .appendSpace()
                .append(ARROW)
                .appendSpace()
                .append(event.originalMessage().color(TextColor.color(0x9D9D9D)));
        event.setCancelled(true);
        if(toggled){
            SMPPlayer smpPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId()).orElseThrow();
            Optional<Team> optionalTeam = smpPlayer.getTeam();
            if(optionalTeam.isEmpty()) {
                ChatSubCommand.TEAM_CHAT_STATUS.remove(event.getPlayer().getUniqueId());
                sendToAllPlayers(message);
                return;
            }
            Team team = optionalTeam.get();
            for(Player player : team.getOnlineMembers()){
                player.sendMessage(tcPrefix.append(message));
            }
        } else
            sendToAllPlayers(message);
    }

    public void sendToAllPlayers(Component component){
        Bukkit.getServer().sendMessage(component);
    }

}
