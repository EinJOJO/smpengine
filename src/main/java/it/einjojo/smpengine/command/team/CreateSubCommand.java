package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.TeamManager;
import it.einjojo.smpengine.util.CommandUtil;
import net.kyori.adventure.text.Component;
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
            if (args.length == 1) {
                if (plugin.getTeamManager().getTeamByName(args[0]).isEmpty()) {
                    Optional<SMPPlayer> player1 = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                    player1.ifPresent(smpPlayer -> {
                        if (smpPlayer.isInsideTeam()) {
                            player.sendMessage(plugin.getMessage("command.team.alreadyMember"));
                        } else {
                            plugin.getTeamManager().createTeam(args[0], smpPlayer);
                        }
                    });
                } else{
                    player.sendMessage(plugin.getMessage("command.team.alreadyExisting"));
                }
            } else {
                player.sendMessage(plugin.getMessage("commend.team.createWrong"));
            }
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
