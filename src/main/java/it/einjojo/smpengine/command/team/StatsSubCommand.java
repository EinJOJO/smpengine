package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.stats.Stats;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.util.CommandUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class StatsSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public StatsSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override @SuppressWarnings("all")
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            CommandUtil.requirePlayer(sender, (player -> {
                Optional<SMPPlayer> optionalSMPPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                if (optionalSMPPlayer.isEmpty()) {
                    return;
                }
                SMPPlayer nPlayer = optionalSMPPlayer.get();
                Optional<Team> optionalTeam = optionalSMPPlayer.get().getTeam();
                if (optionalTeam.isEmpty()) {
                    player.sendMessage(plugin.getMessage("command.team.notInTeam"));
                    return;
                }
                Team team = optionalTeam.get();

                showStats(sender, team);


            }));
        } else if (args.length == 1) {
            Team team = plugin.getTeamManager().getTeamByName(args[0]).orElse(null);
            showStats(sender, team);
        } else if(args.length > 1){
            sender.sendMessage(plugin.getMessage("command.team.stats.usage"));
        }
    }

    private void showStats(CommandSender sender, Team target) {
        Team team = target;
        if (team == null) {
            sender.sendMessage(plugin.getMessage("command.team.notInTeam"));
            return;
        }
        Stats stats = plugin.getStatsManager().getByTeam(target.getId());
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

    public String formatPlaytime(Instant playTime) {
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
        if (days > 0) {
            builder.append(daysString);
        }
        if (hours > 0) {
            builder.append(hoursString);
        }
        if (minutes > 0) {
            builder.append(minutesString);
        }

        return builder.toString();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return plugin.getTeamManager().getTeamNames();
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Erhalte die Stats eines Teams!";
    }

    @Override
    public String getCommand() {
        return "stats";
    }
}
