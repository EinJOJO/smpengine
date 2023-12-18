package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.util.CommandUtil;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class DeleteSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public DeleteSubCommand(SMPEnginePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, player -> {
            if (args.length > 0) {
                player.sendMessage(plugin.getMessage("commend.team.removeWrong"));
                return;
            }
            Optional<SMPPlayer> optional = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            optional.ifPresent(smpPlayer -> {
                if(optional.get().getTeam().isPresent()){
                    Optional<Team> team = optional.get().getTeam();
                    if(team.get().isOwner(optional.get()) || player.hasPermission("team.delete.others")){
                        plugin.getTeamManager().deleteTeam(team.get());
                    } else {
                        player.sendMessage(plugin.getMessage("command.team.notOwner"));
                    }
                } else{
                    player.sendMessage(plugin.getMessage("command.team.ExecutorNotInTeam"));
                }

            });

        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Delete a team!";
    }

    @Override
    public String getCommand() {
        return null;
    }
}
