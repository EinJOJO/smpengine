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

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
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
        if(oPlayer.isEmpty()){
            sender.sendMessage(plugin.getMessage("command.team.targetNotFound"));
            return;
        }
        Stats stats = plugin.getStatsManager().getByPlayer(oPlayer.get().getUuid());
        TextColor primary = plugin.getPrimaryColor();
        TextColor dark_gray = NamedTextColor.DARK_GRAY;
        TextColor muted = NamedTextColor.GRAY;
        Component border = Component.text("-------------------------").color(dark_gray)
                .decorate(TextDecoration.STRIKETHROUGH);
        Component statsText = Component.text()
                .append(border)
                .appendNewline()
                .append(Component.text(target.getName() + "'s Stats").color(primary))
                .appendNewline()
                .appendNewline()
                .append(Component.text("Blocks Destroyed: ").color(muted)).append(Component.text(stats.getBlocksDestroyed()).color(primary))
                .appendNewline()
                .append(Component.text("Blocks Placed: ").color(muted)).append(Component.text(stats.getBlocksPlaced()).color(primary))
                .appendNewline()
                .append(Component.text("Mob Kills: ").color(muted).append(Component.text(stats.getMobKills()).color(primary)))
                .appendNewline()
                .append(Component.text("Player Kills: ").color(muted)).append(Component.text(stats.getPlayerKills()).color(primary))
                .appendNewline()
                .append(Component.text("Deaths: ").color(muted)).append(Component.text(stats.getDeaths()).color(primary))
                .appendNewline()
                .append(Component.text("Villager Trades: ").color(muted)).append(Component.text(stats.getVillagerTrades()).color(primary))
                .appendNewline()
                .append(Component.text("Spielzeit: ").color(muted)).append(Component.text(formatPlaytime(stats.getPlayTime())).color(primary))
                .appendNewline()
                .append(border)
                .build();
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

    public String formatPlaytime(Instant playTime){
        Duration duration = Duration.between(Instant.EPOCH, playTime);

        // Extrahieren von Tagen, Stunden und Minuten
        long days = duration.toDays();
        duration = duration.minusDays(days);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();

        // Formatieren des Strings
        String daysString = String.format("%d Tage, ", days);
        String hoursString = String.format("%d Stunden, ", hours);
        String minutesString = String.format("%d Minuten", minutes);
        String finalString = "";
        StringBuilder builder = new StringBuilder(finalString);
        if(days > 0){
            builder.append(daysString);
        }
        if(hours > 0){
            builder.append(hoursString);
        }
        if(minutes > 0){
            builder.append(minutesString);
        }

        return builder.toString();
    }

}
