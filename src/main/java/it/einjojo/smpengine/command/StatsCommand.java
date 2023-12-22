package it.einjojo.smpengine.command;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.stats.Stats;
import it.einjojo.smpengine.util.CommandUtil;
import it.einjojo.smpengine.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class StatsCommand implements CommandExecutor, TabCompleter {

    private final SMPEnginePlugin plugin;

    public StatsCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("stats").setExecutor(this);
        plugin.getCommand("stats").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) { // /stats
            CommandUtil.requirePlayer(sender, (player) -> {
                plugin.getPlayerManager().getPlayerAsync(player.getUniqueId()).thenAccept((oPlayer) -> {
                    showStats(sender, oPlayer.orElseThrow());
                }).exceptionally((e) -> {
                    sender.sendMessage(plugin.getMessage(MessageUtil.KEY.GENERAL_ERROR));
                    e.printStackTrace();
                    return null;
                });
            });
        } else if (args.length == 1) { // /stats [name]
            String targetName = args[0];
            plugin.getPlayerManager().getPlayerByNameAsync(targetName).thenAccept((oTarget) -> {
                if (oTarget.isEmpty()) {
                    sender.sendMessage(plugin.getMessage(MessageUtil.KEY.COMMAND_TARGET_NOT_FOUND));
                    return;
                }
                showStats(sender, oTarget.get());
            });
        } else {
            sendUsage(sender);
        }
        return true;
    }

    private void sendUsage(CommandSender sender) {
        // TODO: 12/22/2023 implement
    }

    private void showStats(CommandSender sender, SMPPlayer target) {
        Optional<SMPPlayer> oPlayer = plugin.getPlayerManager().getPlayer(target.getUuid());
        if (oPlayer.isEmpty()) {
            sender.sendMessage(plugin.getMessage("command.team.targetNotFound"));
            return;
        }
        Stats stats = plugin.getStatsManager().getByPlayer(oPlayer.get().getUuid());
        TextColor primary = plugin.getPrimaryColor();
        Component statsText = MessageUtil.getStats(stats, primary, target.getName());
        sender.sendMessage(statsText);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command
            command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) { // stats [name]
            return CommandUtil.getOnlinePlayerNames();
        }
        return List.of("");
    }


}
