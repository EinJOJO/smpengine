package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.stats.Stats;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.util.CommandUtil;
import it.einjojo.smpengine.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class StatsSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public StatsSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            CommandUtil.requirePlayer(sender, (player -> {
                // get player
                plugin.getPlayerManager().getPlayerAsync(player.getUniqueId()).thenAccept((oSMP) -> {
                    // get team
                    oSMP.orElseThrow().getTeamAsync().thenAccept((oTeam) -> {
                        if (oTeam.isEmpty()) {
                            player.sendMessage(plugin.getMessage("command.team.notInTeam"));
                            return;
                        }
                        showStats(sender, oTeam.get());
                    }).exceptionally((e) -> exception(e, sender));
                }).exceptionally((e) -> exception(e, sender));
            }));
            return;
        } else if (args.length == 1) {
            // get team by name
            plugin.getTeamManager().getTeamByNameAsync(args[0]).thenAcceptAsync((oTeam) -> {
                if (oTeam.isEmpty()) {
                    sender.sendMessage(plugin.getMessage("command.team.notExist"));
                    return;
                }
                showStats(sender, oTeam.get());
            }).exceptionally((e) -> exception(e, sender));
            return;
        }
        sender.sendMessage(plugin.getMessage("command.team.stats.usage"));
    }

    private Void exception(Throwable t, CommandSender sender) {
        sender.sendMessage(plugin.getGeneralErrorMessage());
        t.printStackTrace();
        return null;
    }

    private void showStats(CommandSender sender, Team team) {
        if (team == null) {
            sender.sendMessage(plugin.getMessage("command.team.notInTeam"));
            return;
        }
        Stats stats = plugin.getStatsManager().getByTeam(team.getId());
        TextColor primary = plugin.getPrimaryColor();
        Component statsText = MessageUtil.getStats(stats, primary, team.getName());
        sender.sendMessage(statsText);
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
