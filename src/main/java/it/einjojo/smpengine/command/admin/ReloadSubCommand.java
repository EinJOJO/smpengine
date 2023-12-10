package it.einjojo.smpengine.command.admin;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadSubCommand implements Command {

    private SMPEnginePlugin plugin;

    public ReloadSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {
        return null;
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
    public String getCommand() {
        return "reload";
    }
}
