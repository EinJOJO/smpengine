package it.einjojo.smpengine.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface Command {
    public void execute(CommandSender sender, String[] args);

    public List<String> tabComplete(CommandSender sender, String[] args);

    public String getPermission();

    public String getDescription();
    public String getCommand();



}
