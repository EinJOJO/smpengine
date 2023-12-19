package it.einjojo.smpengine.scoreboard;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TablistManager {

    private final SMPEnginePlugin plugin;
    private static final String NO_TEAM = "9999_NO_TEAM";

    public TablistManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    public void update(Player player) {
        player.sendPlayerListHeaderAndFooter(getHeader(), getFooter());
        applyTeam(player);
    }

    public void applyTeam(Player player) {
        SMPPlayer smpPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId()).orElseThrow();
        Scoreboard scoreboard = player.getScoreboard();
        smpPlayer.getTeam().ifPresentOrElse(team -> {
            String key = team.getId() + "_" + team.getName() + "_" + player.getName();
            Team bukkitTeam = scoreboard.getTeam(key);
            if (bukkitTeam == null) {
                bukkitTeam = scoreboard.registerNewTeam(key);
            }
            bukkitTeam.prefix(team.getDisplayName());
            bukkitTeam.color(NamedTextColor.GRAY);
            bukkitTeam.addEntry(player.getName());
        }, () -> {
            Team bukkitTeam = scoreboard.getTeam(NO_TEAM);
            if (bukkitTeam == null) {
                bukkitTeam = scoreboard.registerNewTeam(NO_TEAM);
            }
            bukkitTeam.color(NamedTextColor.GREEN);
            bukkitTeam.addEntry(player.getName());
        });

    }


    private Component getHeader() {
        return plugin.getPrefix().append(Component.text( " Header"));
    }

    private Component getFooter() {
        return plugin.getPrefix().append(Component.text(" Footer"));
    }

}
