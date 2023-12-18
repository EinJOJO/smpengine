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
    /**
     * /team create [name]
     */
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, (player -> {
            if (args.length != 1) {
                player.sendMessage(plugin.getMessage("commend.team.createWrong"));
                return;
            }
            if (plugin.getTeamManager().getTeamByName(args[0]).isEmpty()) {
                player.sendMessage(plugin.getMessage("command.team.alreadyExisting"));
                return;
            }
            Optional<SMPPlayer> optional = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            optional.ifPresent(smpPlayer -> {
                if (smpPlayer.isInsideTeam()) {
                    player.sendMessage(plugin.getMessage("command.team.alreadyMember"));
                    return;
                }

                for (int i = 0; i < 10 ; i++) {
                    Team team = plugin.getTeamManager().createTeam(args[0]+i, smpPlayer);
                }

            });
        }));

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getPermission() {
        return "";
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
