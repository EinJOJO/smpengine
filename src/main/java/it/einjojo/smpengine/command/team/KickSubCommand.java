package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.util.CommandUtil;
import it.einjojo.smpengine.util.MessageUtil;
import it.einjojo.smpengine.util.Placeholder;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class KickSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public KickSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, player -> {
            if (args.length > 2 || args.length == 0) {
                player.sendMessage(plugin.getMessage("command.team.kick.usage"));
                return;
            }
            SMPPlayer senderSMPPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId()).orElseThrow();
            Optional<SMPPlayer> _oTarget = plugin.getPlayerManager().getPlayerByName(args[0]);
            if (_oTarget.isEmpty()) {
                player.sendMessage(plugin.getMessage(MessageUtil.KEY.COMMAND_TARGET_NOT_FOUND));
                return;
            }
            SMPPlayer target = _oTarget.get();
            if (args.length == 1) {
                if (senderSMPPlayer.getTeam().isEmpty()) {
                    player.sendMessage(plugin.getMessage("command.team.notInTeam"));
                    return;
                }
                Team team = senderSMPPlayer.getTeam().get();
                if (!team.isOwner(senderSMPPlayer)) {
                    player.sendMessage(plugin.getMessage("command.team.notOwner"));
                    return;
                }
                kick(player, target, team);

            } else {
                if (!player.hasPermission("team.kick.others")) {
                    player.sendMessage(plugin.getMessage(MessageUtil.KEY.NO_PERMISSION));
                    return;
                }
                Optional<Team> _team = plugin.getTeamManager().getTeamByName(args[1]);
                if (_team.isEmpty()) {
                    player.sendMessage(plugin.getMessage("command.team.notExist"));
                    return;
                }
                Team targetTeam = _team.get();
                kick(player, target, targetTeam);
            }
        });
    }

    private void kick(Player executor, SMPPlayer target, Team team) {
        if (team.isOwner(target)) {
            executor.sendMessage(plugin.getMessage("command.team.kick.owner"));
            return;
        }
        if (team.isMember(target)) {
            if (!team.removeMember(target)) {
                executor.sendMessage(plugin.getMessage(MessageUtil.KEY.GENERAL_ERROR));
            }
            sendKickNotification(target, team);
        } else {
            Component message = Placeholder.applyPlaceholders(plugin.getMessage("command.team.targetNotInTeam"), new Placeholder("player", target.getName()));
            executor.sendMessage(message);
        }
    }

    private void sendKickNotification(SMPPlayer kicked, Team team) {
        if (kicked.getPlayer() != null) {
            kicked.getPlayer().sendMessage(plugin.getMessage("command.team.kick.target-info"));
            return;
        }
        for (Player globalPlayer : team.getOnlineMembers()) {
            globalPlayer.sendMessage(Placeholder.applyPlaceholders(plugin.getMessage("command.team.kick.global-info"), new Placeholder("player", kicked.getName())));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender _sender, String[] args) {
        if (_sender instanceof Player _player) {
            if (args.length <= 1) {
                return CommandUtil.getOnlinePlayerNames().stream().filter(name -> name.startsWith(args[0])).toList();
            }
        }
        return List.of("");
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Entferne einen Spieler aus deinem Team!";
    }

    @Override
    public String getCommand() {
        return "kick";
    }
}
