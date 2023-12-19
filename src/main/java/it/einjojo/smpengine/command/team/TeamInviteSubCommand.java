package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.util.CommandUtil;
import it.einjojo.smpengine.util.Placeholder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamInviteSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public TeamInviteSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, (_player -> {
            SMPPlayer smpPlayer = plugin.getPlayerManager().getPlayer(_player.getUniqueId()).orElseThrow();
            // accept invite
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("accept")) {
                    plugin.getTeamManager().getInvite(_player.getUniqueId()).ifPresentOrElse(
                            (teamId) -> joinTeam(teamId, smpPlayer),
                            () -> {
                                _player.sendMessage(plugin.getMessage("command.team.invite.noInvite"));
                            });
                    return;
                }

            }
            // Send invite
            smpPlayer.getTeam().ifPresentOrElse((team) -> {
                        if (!team.isOwner(smpPlayer)) {
                            sender.sendMessage(plugin.getMessage("command.team.notOwner"));
                        }
                        if (args.length != 1) {
                            sender.sendMessage(plugin.getMessage("command.team.invite.usage"));
                        }
                        Optional<SMPPlayer> oTarget = plugin.getPlayerManager().getPlayer(args[0]);
                        if (oTarget.isEmpty()) {
                            sender.sendMessage(plugin.getMessage("command.target-not-found"));
                            return;
                        }
                        SMPPlayer target = oTarget.get();
                        if (target.getPlayer() == null) {
                            sender.sendMessage(plugin.getMessage("command.team.invite.playerOffline"));
                            return;
                        }
                        if (target.getTeam().isPresent()) {
                            sender.sendMessage(plugin.getMessage("command.team.invite.alreadyInTeam"));
                            return;
                        }
                        // Send invite
                        plugin.getTeamManager().createInvite(target.getUuid(), team);
                        sender.sendMessage(plugin.getMessage("command.team.invite.success"));

                        // Send notification to target
                        Placeholder teamPlaceholder = new Placeholder("team", team.getDisplayName());
                        Component message = plugin.getMessage("command.team.invite.info")
                                .clickEvent(ClickEvent.runCommand("/team invite accept"))
                                .hoverEvent(HoverEvent.showText(Component.text("/team invite accept")));
                        target.getPlayer().sendMessage(Placeholder.applyPlaceholders(message, teamPlaceholder)
                        );
                    },
                    () -> {
                        _player.sendMessage(plugin.getMessage("command.team.notInTeam"));
                    });
        }));
    }


    private void joinTeam(int teamID, SMPPlayer player) {
        plugin.getTeamManager().getTeamById(teamID).ifPresentOrElse((team) -> {
            team.addMember(player);
            player.getPlayer().sendMessage(plugin.getMessage("command.team.invite.accepted"));
        }, () -> {
            player.getPlayer().sendMessage(plugin.getMessage("command.team.invite.expired"));
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        ArrayList<String> result = new ArrayList<>();
        result.add("accept");
        if (!(sender instanceof Player player)) {
            return result;
        }
        if (args.length <= 1) {
            SMPPlayer smpPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId()).orElseThrow();
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
