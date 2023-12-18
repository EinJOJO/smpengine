package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.util.CommandUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class InfoSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public InfoSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, player -> {
            Optional<Team> team = plugin.getPlayerManager().getPlayer(player.getUniqueId()).get().getTeam();
            if (team.isPresent()) {
                Team team1 = team.get();
                printTeamInfo(team1, sender);
            }
        });
    }

    public void printTeamInfo(Team team, CommandSender sender) {
        Component message = Component.text()
                .append(Component.text("§8[§6Team§8] §7Team-Info"))
                .append(Component.text("§8» §7Name: §6" + team.getName()))
                .append(Component.text("§8» §7Owner: §6" + team.getOwner().getName()))
                .append(Component.text("§8» §7Mitglieder: §6" + team.getMembers().size()))
                .build();
        sender.sendMessage(message);

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getPermission() {
        return "team.info";
    }

    @Override
    public String getDescription() {
        return "Erhalte Informationen über dein Team!";
    }

    @Override
    public String getCommand() {
        return "info";
    }
}
