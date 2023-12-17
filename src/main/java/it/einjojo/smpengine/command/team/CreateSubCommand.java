package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.team.TeamManager;
import it.einjojo.smpengine.util.CommandUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CreateSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public CreateSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.isPlayer(sender, (player -> {
            if (args.length == 1) {
                TeamManager.create(args[0], player.getUniqueId());
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
