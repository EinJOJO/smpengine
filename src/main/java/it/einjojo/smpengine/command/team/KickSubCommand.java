package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.core.team.TeamManager;
import it.einjojo.smpengine.util.CommandUtil;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class KickSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public KickSubCommand(SMPEnginePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, player -> {
            if (args.length != 1) {
                player.sendMessage(plugin.getMessage("commend.team.removeWrong"));
                return;
            }
            Optional<SMPPlayer> optionalSMPPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            Optional<SMPPlayer> kicked = plugin.getPlayerManager().getPlayer(args[0]);
            optionalSMPPlayer.ifPresent(smpPlayer -> {
                if (optionalSMPPlayer.get().getTeam().isPresent()) {
                    SMPPlayer executor = optionalSMPPlayer.get();
                    Optional<Team> optionalTeam = executor.getTeam();
                    Team team = optionalTeam.get();
                    if (!team.isOwner(executor)) {
                        return;
                    }
                    if (kicked.isEmpty()) {
                        return;
                    }
                    SMPPlayer executedOn = kicked.get();
                    if (!team.isMember(executedOn)) {
                        player.sendMessage(plugin.getMessage("command.team.notInTeam"));
                        return;
                    }
                    team.removeMember(executedOn);
                }
                if(player.hasPermission(plugin.getMaintenanceConfig().getBypassPermission())){

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
        return null;
    }

    @Override
    public String getCommand() {
        return null;
    }
}
