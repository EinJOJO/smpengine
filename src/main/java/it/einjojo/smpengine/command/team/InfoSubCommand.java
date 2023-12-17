package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.util.CommandUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InfoSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public InfoSubCommand(SMPEnginePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.isPlayer(sender, player -> {
            Optional<Team> team = plugin.getPlayerManager().getPlayer(player.getUniqueId()).get().getTeam();
            if(team.isPresent()){
                Team team1 = team.get();

            }
        });
    }

    public void printTeamInfo(Team team, CommandSender sender){
        sender.sendMessage(Component.text(""));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getCommand() {
        return null;
    }
}
