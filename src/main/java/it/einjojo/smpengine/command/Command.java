package it.einjojo.smpengine.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface Command {
    public CommandResult execute(CommandSender sender, String[] args);

    public List<String> tabComplete(CommandSender sender, String[] args);

    public String getPermission();

    public String getCommand();

    public enum CommandResult {
        SUCCESS,
        INVALID_USAGE,
        NO_PERMISSION,
        PLAYER_ONLY,
        CONSOLE_ONLY,
        UNKNOWN_SUBCOMMAND,
        ERROR
    }

}
