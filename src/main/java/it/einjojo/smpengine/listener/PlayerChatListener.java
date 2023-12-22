package it.einjojo.smpengine.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.team.ChatSubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
        event.setCancelled(true);
        boolean teamChat = ChatSubCommand.TEAM_CHAT_STATUS.getOrDefault(event.getPlayer().getUniqueId(), false);
        if (teamChat) {
            sendTeamMessage(event);
        } else {
            Bukkit.getServer().sendMessage(formatMessage(event));
        }
    }

    private void sendTeamMessage(AsyncChatEvent event) {
        plugin.getPlayerManager().getPlayerAsync(event.getPlayer().getUniqueId()).thenAcceptAsync(smpPlayer -> {
            smpPlayer.orElseThrow().getTeam().ifPresent(team -> {
                for (Player player : team.getOnlineMembers()) {
                    player.sendMessage(formatMessage(event));
                }
            });
        });
    }

    private Component formatMessage(AsyncChatEvent event) {
        return event.getPlayer().teamDisplayName()
                .appendSpace()
                .append(ARROW)
                .appendSpace()
                .append(event.originalMessage().color(TextColor.color(0x9D9D9D)));
    }


}
