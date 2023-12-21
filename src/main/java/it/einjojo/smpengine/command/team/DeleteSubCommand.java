package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.util.CommandUtil;
import it.einjojo.smpengine.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DeleteSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public DeleteSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, player -> {
            SMPPlayer smpSender = plugin.getPlayerManager().getPlayer(player.getUniqueId()).orElseThrow();
            if (args.length == 0) {
                deleteOwnTeam(smpSender);
            } else if (args.length == 1) {
                plugin.getTeamManager().getTeamByNameAsync(args[0]).thenAccept(oTeam -> {
                    if (oTeam.isEmpty()) {
                        player.sendMessage(plugin.getMessage("command.team.notExist"));
                        return;
                    }
                    Team team = oTeam.get();
                    deleteOtherTeam(smpSender, team);
                });
            } else {
                player.sendMessage(plugin.getMessage("commend.team.delete.usage"));
            }

        });
    }

    private void deleteOwnTeam(SMPPlayer smpSender) {
        Player player = smpSender.getPlayer();
        if (player == null) {
            return;
        }
        smpSender.getTeamAsync().thenAccept((oSenderTeam) -> {
            if (oSenderTeam.isEmpty()) {
                player.sendMessage(plugin.getMessage("command.team.notInTeam"));
                return;
            }
            Team team = oSenderTeam.get();
            if (!team.isOwner(smpSender)) {
                player.sendMessage(plugin.getMessage("command.team.notOwner"));
                return;
            }
            deleteTeam(player, team);
        });
    }

    private void deleteOtherTeam(SMPPlayer player, Team team) {
        Player player1 = player.getPlayer();
        if (player1 == null) {
            return;
        }
        if (player1.hasPermission("team.delete.other")) {
            deleteTeam(player1, team);
        } else {
            player1.sendMessage(plugin.getMessage(MessageUtil.KEY.NO_PERMISSION));
        }
    }

    private void deleteTeam(Player player, Team team) {
        CompletableFuture
                .supplyAsync(() -> plugin.getTeamManager().deleteTeam(team))
                .handle((deleted, exception) -> {
                    if (exception != null) {
                        exception.printStackTrace();
                        player.sendMessage(plugin.getMessage(MessageUtil.KEY.GENERAL_ERROR));
                        return false;
                    }
                    if (deleted) {
                        player.sendMessage(plugin.getMessage("command.team.delete.success"));
                    } else {
                        player.sendMessage(plugin.getMessage("command.team.delete.error"));
                    }
                    return null;
                });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            if (!sender.hasPermission("team.delete.other")) return List.of();
            return plugin.getTeamManager().getTeamNames().stream()
                    .filter(name -> name.startsWith(args[0]))
                    .toList();
        }
        return List.of();
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Delete a team!";
    }

    @Override
    public String getCommand() {
        return "delete";
    }
}
