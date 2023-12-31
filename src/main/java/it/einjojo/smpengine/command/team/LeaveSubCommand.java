package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.util.CommandUtil;
import it.einjojo.smpengine.util.MessageUtil;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LeaveSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public LeaveSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, player -> {
            plugin.getPlayerManager().getPlayerAsync(player.getUniqueId()).thenAccept(oSmpPlayer -> {
                SMPPlayer smpPlayer = oSmpPlayer.orElseThrow();
                smpPlayer.getTeamAsync().thenAccept((oTeam) -> oTeam.ifPresentOrElse((team) -> {
                    if (team.isOwner(smpPlayer)) {
                        player.sendMessage(plugin.getMessage("command.team.leave.owner"));
                        return;
                    }
                    CompletableFuture.supplyAsync(() -> team.removeMember(smpPlayer))
                            .handle((success, exception) -> {
                                if (exception != null || !success) {
                                    player.sendMessage(plugin.getMessage(MessageUtil.KEY.GENERAL_ERROR));
                                    return null;
                                } else {
                                    player.sendMessage(plugin.getMessage("command.team.leave.success"));
                                }
                                return null;
                            });
                }, () -> {
                    player.sendMessage(plugin.getMessage("command.team.notInTeam"));
                }));
            });
        });
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
        return "Verlasse dein Team!";
    }

    @Override
    public String getCommand() {
        return "leave";
    }
}
