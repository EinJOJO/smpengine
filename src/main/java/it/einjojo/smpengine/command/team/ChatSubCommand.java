package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.util.CommandUtil;
import it.einjojo.smpengine.util.Placeholder;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public static final Map<UUID, Boolean> TEAM_CHAT_STATUS = new HashMap<>();

    public ChatSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, player -> plugin.getPlayerManager().getPlayerAsync(player.getUniqueId())
                .thenAcceptAsync(oSmpPlayer -> oSmpPlayer.orElseThrow().getTeam()
                        .ifPresentOrElse(
                                (team) -> toggleTeamChat(oSmpPlayer.orElseThrow(), team),
                                () -> player.sendMessage(plugin.getMessage("command.team.notInTeam"))
                        )
                ).exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                })
        );
    }

    public void toggleTeamChat(SMPPlayer player, Team team){
        boolean status = TEAM_CHAT_STATUS.getOrDefault(player.getUuid(), false);
        TEAM_CHAT_STATUS.put(player.getUuid(), !status);
        Player p = player.getPlayer();
        if(p == null) return;
        player.getPlayer().sendMessage(plugin.getMessage("command.team.teamchat", new Placeholder("state",  status ? "deaktiviert" : "aktiviert")));
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Aktiviere oder deaktiviere den Teamchat";
    }

    @Override
    public String getCommand() {
        return "chat";
    }
}
