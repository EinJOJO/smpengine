package it.einjojo.smpengine.command;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.stats.Stats;
import it.einjojo.smpengine.database.StatsDatabase;
import it.einjojo.smpengine.util.CommandUtil;
import it.einjojo.smpengine.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class StatsCommand implements CommandExecutor, TabCompleter {

    private final SMPEnginePlugin plugin;

    private final StatsDatabase statsDatabase;

    public StatsCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.statsDatabase = new StatsDatabase(plugin.getHikariCP());
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
        if(oPlayer.isEmpty()){
            sender.sendMessage(plugin.getMessage("command.team.targetNotFound"));
            return;
        }
        Stats stats = statsDatabase.getGlobalStats(oPlayer.get().getUuid());
        TextColor primary = plugin.getPrimaryColor();
        TextColor dark_gray = NamedTextColor.DARK_GRAY;
        TextColor muted = NamedTextColor.GRAY;
        Component border = Component.text("-------------------------").color(dark_gray)
                .decorate(TextDecoration.STRIKETHROUGH);
        Component statsText = Component.text()
                .append(border)
                .appendNewline()
                .append(Component.text(target.getName() + "'s Stats"))
                .appendNewline()
                .append(Component.text(TextColor.color(muted) + "Blocks Destroyed: " + TextColor.color(primary) + stats.getBlocksDestroyed()))
                .appendNewline()
                .append(Component.text(TextColor.color(muted) + "Blocks Placed: " + TextColor.color(primary) + stats.getBlocksPlaced()))
                .appendNewline()
                .append(Component.text(TextColor.color(muted) + "Mob Kills: " + TextColor.color(primary) + stats.getMobKills() ))
                .appendNewline()
                .append(Component.text(TextColor.color(muted) + "Player Kills: " + TextColor.color(primary) + stats.getPlayerKills()))
                .appendNewline()
                .append(Component.text(TextColor.color(muted) + "Deaths: " + TextColor.color(primary) + stats.getDeaths()))
                .appendNewline()
                .append(Component.text(TextColor.color(muted) + "Villager Trades: " + TextColor.color(primary) + stats.getVillagerTrades()))
                .appendNewline()
                .append(border)
                .build();
        Objects.requireNonNull(target.getPlayer()).sendMessage(statsText);
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
