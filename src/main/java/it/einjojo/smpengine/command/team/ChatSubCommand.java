package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChatSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public ChatSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

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
        return "Aktiviere oder deaktiviere den Teamchat";
    }

    @Override
    public String getCommand() {
        return "chat";
    }
}
