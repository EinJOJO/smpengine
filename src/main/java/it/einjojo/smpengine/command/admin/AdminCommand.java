package it.einjojo.smpengine.command.admin;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminCommand implements TabCompleter, CommandExecutor {
    private final SMPEnginePlugin plugin;
    private final Map<String, Command> subCommands = new HashMap<>();

    public AdminCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("smpengine").setExecutor(this);
        this.plugin.getCommand("smpengine").setTabCompleter(this);
        registerSubCommand(new ReloadSubCommand(plugin));
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Command subCommand = subCommands.get(args[0]);
        if (subCommand == null) {
            return true;
        }
        Command.CommandResult result = subCommand.execute(sender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }

    private void registerSubCommand(Command command) {
        subCommands.put(command.getCommand(), command);
    }
}
