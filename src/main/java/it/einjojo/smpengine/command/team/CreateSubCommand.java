package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.util.CommandUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

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

            // 1. Gucken, ob die Argumente stimmen
            if (args.length == 1) {
                // 2 a. Überprüfen, ob das Team schon existiert.

                // 2 b .TeamManager.create(args[0], player.getUniqueId()); // TODO: plugin.getTeamManager.create!
            } else {
                // send /team create <name>
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
