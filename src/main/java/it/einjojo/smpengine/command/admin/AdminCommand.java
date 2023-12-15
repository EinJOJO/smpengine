package it.einjojo.smpengine.command.admin;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.util.CommandUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        if (args.length == 0) {
            if (sender.hasPermission("smpengine.admin")) {
                showHelp(sender);
            } else {
                sender.sendMessage(plugin.getMessage("no-permission"));
            }
            return true;
        }
        Command subCommand = subCommands.get(args[0]);
        if (subCommand == null) {
            return true;
        }
        Command.CommandResult result = subCommand.execute(sender, args);
        return true;
    }

    private void showHelp(CommandSender sender) {
        Component component = plugin.getPrefix().append(
                Component.text("Hilfe").color(NamedTextColor.GRAY)
                        .append(Component.newline()));
        for (Command command : subCommands.values()) {
            component = component
                    .append(Component.text(" ⋆ ").color(NamedTextColor.DARK_GRAY)
                            .append(Component.text(command.getCommand()).color(plugin.getPrimaryColor()))
                            .append(Component.text(" - ").color(NamedTextColor.DARK_GRAY))
                            .append(Component.text(command.getDescription()).color(NamedTextColor.GRAY)))
                    .append(Component.newline());
        }
        sender.sendMessage(component);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return CommandUtil.tabCompleteSubCommands(subCommands.values(), sender, args);
    }

    private void registerSubCommand(Command command) {
        subCommands.put(command.getCommand(), command);
    }
}
