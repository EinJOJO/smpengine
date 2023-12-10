package it.einjojo.smpengine.util;


import it.einjojo.smpengine.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CommandUtil {

    public static Optional<Player> isPlayer(CommandSender sender, Consumer<Player> playerConsumer) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return Optional.empty();
        }
        if (playerConsumer != null) playerConsumer.accept((Player) sender);
        return Optional.of((Player) sender);
    }

    public static List<String> tabCompleteSubCommands(List<Command> subCommands, CommandSender sender, String[] args) {
        if (args.length == 0 || args.length == 1) {
            return subCommands.stream()
                    .filter(command -> command.getPermission() == null || sender.hasPermission(command.getPermission()))
                    .map(Command::getCommand)
                    .filter(command -> command.startsWith(args[0])).toList();
        }
        return null;
    }

    public static String[] removeFirst(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }


}
