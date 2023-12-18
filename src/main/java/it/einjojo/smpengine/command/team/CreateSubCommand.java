package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.util.CommandUtil;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class CreateSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public CreateSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;

    }

    @Override
    /*
     * /team create [name]
     */
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, (player -> {
            if (args.length != 1) {
                player.sendMessage(plugin.getMessage("command.team.create.usage"));
                return;
            }
            if (plugin.getTeamManager().getTeamByName(args[0]).isPresent()) {
                player.sendMessage(plugin.getMessage("command.team.create.alreadyExists"));
                return;
            }
            Optional<SMPPlayer> oSender = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            oSender.ifPresent(smpPlayer -> {
                if (smpPlayer.isInsideTeam()) {
                    player.sendMessage(plugin.getMessage("command.team.create.alreadyInTeam"));
                    return;
                }
                Team team = plugin.getTeamManager().createTeam(args[0], smpPlayer);
                if (team == null) {
                    player.sendMessage(plugin.getMessage("general-error"));
                    return;
                }
                player.sendMessage(plugin.getMessage("command.team.create.success"));
            });
        }));

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
        return "Erstellt ein Team!";
    }

    @Override
    public String getCommand() {
        return "create";
    }
}
