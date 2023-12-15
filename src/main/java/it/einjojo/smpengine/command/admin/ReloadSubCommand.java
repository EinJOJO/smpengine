package it.einjojo.smpengine.command.admin;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadSubCommand implements Command {
    private final SMPEnginePlugin plugin;

    public ReloadSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.reloadConfigs();
        sender.sendMessage(plugin.getMessage("command.reload.reloaded"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String getPermission() {
        return "smpengine.admin.reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the Plugin Configs";
    }

    @Override
    public String getCommand() {
        return "reload";
    }
}
