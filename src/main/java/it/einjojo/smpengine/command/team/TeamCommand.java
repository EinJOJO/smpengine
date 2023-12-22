package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.util.CommandUtil;
import it.einjojo.smpengine.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamCommand implements CommandExecutor, TabCompleter {

    private final SMPEnginePlugin plugin;

    private final Map<String, Command> subCommands = new HashMap<>();

    public TeamCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        registerSubCommand(new CreateSubCommand(plugin));
        registerSubCommand(new InfoSubCommand(plugin));
        registerSubCommand(new DeleteSubCommand(plugin));
        registerSubCommand(new KickSubCommand(plugin));
        registerSubCommand(new LeaveSubCommand(plugin));
        registerSubCommand(new InviteSubCommand(plugin));
        registerSubCommand(new MemberSubCommand(plugin));
        registerSubCommand(new ChatSubCommand(plugin));
        registerSubCommand(new StatsSubCommand(plugin));

        plugin.getCommand("team").setExecutor(this);
        plugin.getCommand("team").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Player p = (Player) sender;
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        CommandUtil.executeSubCommand(subCommands, sender, args, plugin);
        return true;
    }

    private void showHelp(CommandSender sender) {
        // <prefix> Admin Commands
        Component component = plugin.getPrefix().append(MessageUtil.format(" <gray>Team Commands <newline>"));
        for (Command command : subCommands.values()) {
            // ⋆ <command> - <description>  // (hover: /smp <command> ausführen) (click: /smp <command> )
            component = component
                    .append(Component.text(" ⋆ ").color(NamedTextColor.DARK_GRAY)
                            .append(MessageUtil.format("<pkr>" + command.getCommand(), plugin.getPrimaryColor())
                                    .hoverEvent(HoverEvent.showText(MessageUtil.format("<red>/team <pkr>" + command.getCommand() + " <white>ausführen.", plugin.getPrimaryColor())))
                                    .clickEvent(ClickEvent.suggestCommand("/team " + command.getCommand() + " "))
                            ).append(Component.text(" - ").color(NamedTextColor.DARK_GRAY))
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
