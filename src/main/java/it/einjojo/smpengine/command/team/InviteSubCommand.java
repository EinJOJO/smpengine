package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.player.SMPPlayerManager;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.core.team.TeamManager;
import it.einjojo.smpengine.util.CommandUtil;
import it.einjojo.smpengine.util.MessageUtil;
import it.einjojo.smpengine.util.Placeholder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InviteSubCommand implements Command {

    private final SMPEnginePlugin plugin;
    private final TeamManager teamManager;
    private final SMPPlayerManager playerManager;

    public InviteSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.teamManager = plugin.getTeamManager();
        this.playerManager = plugin.getPlayerManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, (_player -> {
            SMPPlayer smpPlayer = playerManager.getPlayer(_player.getUniqueId()).orElseThrow();
            // accept invite
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("accept")) {
                    teamManager.getInvite(smpPlayer.getUuid()).ifPresentOrElse(
                            (teamId) -> joinTeam(teamId, smpPlayer),
                            () -> _player.sendMessage(plugin.getMessage("command.team.invite.noInvite")));
                    return;
                }

            }
            // Send invite
            smpPlayer.getTeamAsync().handle((teamOptional, throwable) -> {
                if (throwable != null) {
                    _player.sendMessage(plugin.getMessage(MessageUtil.KEY.GENERAL_ERROR));
                    return null;
                }
                sendInvite(teamOptional.orElse(null), smpPlayer, args);
                return null;
            });
        }));
    }

    private void sendInvite(Team team, SMPPlayer player, String[] args) {
        Player bukkitPlayer = player.getPlayer();
        if (bukkitPlayer == null) return;
        if (team == null) {
            bukkitPlayer.sendMessage(plugin.getMessage("command.team.notInTeam"));
            return;
        }
        if (!team.isOwner(player)) {
            bukkitPlayer.sendMessage(plugin.getMessage("command.team.notOwner"));
            return;
        }
        if (args.length != 1) {
            bukkitPlayer.sendMessage(plugin.getMessage("command.team.invite.usage"));
            return;
        }
        Optional<SMPPlayer> oTarget = playerManager.getPlayer(args[0]);
        if (oTarget.isEmpty()) {
            bukkitPlayer.sendMessage(plugin.getMessage("command.target-not-found"));
            return;
        }
        SMPPlayer target = oTarget.get();
        if (target.getPlayer() == null) {
            bukkitPlayer.sendMessage(plugin.getMessage("command.team.invite.playerOffline"));
            return;
        }
        if (target.getTeam().isPresent()) {
            bukkitPlayer.sendMessage(plugin.getMessage("command.team.invite.alreadyInTeam"));
            return;
        }
        // Send invite
        teamManager.createInvite(target.getUuid(), team);
        bukkitPlayer.sendMessage(plugin.getMessage("command.team.invite.success"));

        // Send notification to target
        Placeholder teamPlaceholder = new Placeholder("team", team.getDisplayName());
        Component message = plugin.getMessage("command.team.invite.info")
                .clickEvent(ClickEvent.runCommand("/team invite accept"))
                .hoverEvent(HoverEvent.showText(Component.text("/team invite accept")));
        target.getPlayer().sendMessage(Placeholder.applyPlaceholders(message, teamPlaceholder));
    }


    private void joinTeam(int teamID, SMPPlayer player) {
        if (player.getPlayer() == null) return;
        teamManager.getTeamById(teamID).ifPresentOrElse((team) -> {
            Placeholder teamPlaceholder = new Placeholder("team", team.getDisplayName());
            if (player.isInsideTeam()) {
                player.getPlayer().sendMessage(Placeholder.applyPlaceholders(plugin.getMessage("command.team.invite.acceptAlreadyInTeam"), teamPlaceholder));
                return;
            }
            team.addMember(player);
            teamManager.removeInvite(player.getUuid());
            player.getPlayer().sendMessage(Placeholder.applyPlaceholders(plugin.getMessage("command.team.invite.accepted"), teamPlaceholder));
        }, () -> player.getPlayer().sendMessage(plugin.getMessage("command.team.invite.expired")));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        ArrayList<String> result = new ArrayList<>();
        result.add("accept");
        if (!(sender instanceof Player player)) {
            return result;
        }
        plugin.getLogger().info(String.valueOf(args.length));
        if (args.length <= 1) {
            SMPPlayer smpPlayer = playerManager.getPlayer(player.getUniqueId()).orElseThrow();
            if (!smpPlayer.isInsideTeam()) {
                return result;
            }
            return CommandUtil.getOnlinePlayerNames().stream().filter(name -> name.startsWith(args[0])).toList();
        }
        return List.of("");
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Create or accept a team invite";
    }

    @Override
    public String getCommand() {
        return "invite";
    }
}
