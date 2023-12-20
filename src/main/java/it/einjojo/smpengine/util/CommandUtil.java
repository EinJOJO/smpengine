package it.einjojo.smpengine.util;


import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

@UtilityClass
public class CommandUtil {

    public static List<String> getOnlinePlayerNames() {
        List<String> names = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            names.add(player.getName());
        }
        return names;
    }

    public static void requirePlayer(CommandSender sender, @Nullable Consumer<Player> playerConsumer) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return;
        }
        if (playerConsumer != null) playerConsumer.accept((Player) sender);
    }


    public static List<String> tabCompleteSubCommands(Collection<Command> subCommands, CommandSender sender, String[] args) {
        if (args.length == 0 || args.length == 1) {
            return subCommands.stream()
                    .filter(command -> command.getPermission() == null || sender.hasPermission(command.getPermission()))
                    .map(Command::getCommand)
                    .filter(command -> command.startsWith(args[0])).toList();
        }
        Command command = subCommands.stream().filter(subCommand -> subCommand.getCommand().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if (command != null) {
            return command.tabComplete(sender, removeFirst(args));
        }
        return List.of("");
    }

    public static void executeSubCommand(Map<String, Command> subCommands, CommandSender sender, String[] args, SMPEnginePlugin plugin) {
        Command subCommand = subCommands.get(args[0]);
        if (subCommand == null) {
            sender.sendMessage(plugin.getMessage("command.unknown"));
            return;
        }
        if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return;
        }
        subCommand.execute(sender, CommandUtil.removeFirst(args));
    }

    public static String[] removeFirst(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }


}
