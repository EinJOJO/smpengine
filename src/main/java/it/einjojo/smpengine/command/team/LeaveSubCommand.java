package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.util.CommandUtil;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class LeaveSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public LeaveSubCommand(SMPEnginePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, player -> {
            Optional<SMPPlayer> optionalSMPPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if(optionalSMPPlayer.isEmpty()){
                return;
            }
            Optional<Team> optionalTeam = optionalSMPPlayer.get().getTeam();
            if(optionalTeam.isEmpty()){
                return;
            }
            Team team = optionalTeam.get();
            team.removeMember(optionalSMPPlayer.get());
            player.sendMessage(plugin.getMessage(""));
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
