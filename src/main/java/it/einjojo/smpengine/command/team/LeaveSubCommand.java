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

    public LeaveSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, player -> {
            SMPPlayer senderSMP = plugin.getPlayerManager().getPlayer(player.getUniqueId()).orElseThrow();
            Optional<Team> optionalTeam = senderSMP.getTeam();
            if (optionalTeam.isEmpty()) {
                player.sendMessage(plugin.getMessage("command.team.notInTeam"));
                return;
            }
            Team team = optionalTeam.get();

            if (team.isOwner(senderSMP)) {
                player.sendMessage(plugin.getMessage("command.team.leave.owner"));
                return;
            }
            if (!team.removeMember(senderSMP)) {
                player.sendMessage("general-error");
            }
            player.sendMessage(plugin.getMessage("command.team.leave.success"));
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
