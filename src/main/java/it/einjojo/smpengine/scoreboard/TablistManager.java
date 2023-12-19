package it.einjojo.smpengine.scoreboard;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TablistManager {

    private final SMPEnginePlugin plugin;
    private static final String NO_TEAM = "9999_NO_TEAM";

    public TablistManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    public void updateTablist(Player player) {
        player.sendPlayerListHeaderAndFooter(getHeader(), getFooter());
    }

    public void applyTeam(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        SMPPlayer smpPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId()).orElseThrow();

        // NoTeam erstellen
        Team noTeam = scoreboard.getTeam(NO_TEAM);
        if (noTeam == null) {
            noTeam = scoreboard.registerNewTeam(NO_TEAM);
        }
        String currentTeam;
        if (smpPlayer.getTeam().isPresent()) {
            currentTeam = "100_" + smpPlayer.getTeam().get().getName();
        } else {
            currentTeam = NO_TEAM;
        }

        // Alle anderen Teams registrieren
        plugin.getTeamManager().getTeams().forEach(teamName -> {
            teamName = "100_" + teamName;
            Team bukkitTeam = scoreboard.getTeam(teamName);
            if (bukkitTeam == null) {
                bukkitTeam = scoreboard.registerNewTeam(teamName);
            }

            if (teamName.equals(currentTeam)) {
                bukkitTeam.addEntry(player.getName());
            } else {
                bukkitTeam.removeEntry(player.getName());
            }
        });


    }


    private Component getHeader() {
        return plugin.getPrefix().append(Component.text("Header"));
    }

    private Component getFooter() {
        return plugin.getPrefix().append(Component.text("Footer"));
    }

}
