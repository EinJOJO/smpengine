package it.einjojo.smpengine.scoreboard;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.time.Duration;
import java.time.Instant;

public class TablistManager {

    private final SMPEnginePlugin plugin;
    private static final String NO_TEAM = "9999_NO_TEAM";
    private int suffixDisplay = 0;

    public TablistManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    public void update(Player player) {
        player.sendPlayerListHeaderAndFooter(getHeader(), getFooter());
        applyTeam(player);
    }

    public void applyTeam(Player player) {
        plugin.getPlayerManager().getPlayerAsync(player.getUniqueId()).thenAcceptAsync((osmpPlayer) -> {
            if (osmpPlayer.isEmpty()) {
                noTeam(player.getScoreboard(), player, null);
                return;
            }
            SMPPlayer smpPlayer = osmpPlayer.get();
            Scoreboard scoreboard = player.getScoreboard();
            smpPlayer.getTeam().ifPresentOrElse(team -> {
                String key = team.getId() + "_" + team.getName() + "_" + player.getName();
                Team bukkitTeam = scoreboard.getTeam(key);
                if (bukkitTeam == null) {
                    bukkitTeam = scoreboard.registerNewTeam(key);
                }
                TextColor muted = NamedTextColor.GRAY;
                Component prefix = Component.text("[").color(muted)
                        .append(team.getDisplayName())
                        .append(Component.text("] ").color(muted));


                bukkitTeam.prefix(prefix);
                bukkitTeam.addEntry(player.getName());
                bukkitTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
            }, () -> noTeam(scoreboard, player, smpPlayer));
        });

    }



    public void nextSuffix() {
        suffixDisplay++;
        if (suffixDisplay > 3) {
            suffixDisplay = 0;
        }
    }

    private void noTeam(Scoreboard scoreboard, Player bukkitPlayer, SMPPlayer player) {
        Team bukkitTeam = scoreboard.getTeam(NO_TEAM);
        if (bukkitTeam == null) {
            bukkitTeam = scoreboard.registerNewTeam(NO_TEAM);
        }
        bukkitTeam.color(NamedTextColor.GREEN);
        bukkitTeam.addEntry(bukkitPlayer.getName());

    }


    private Component getHeader() {
        Component line1 = plugin.getPrefix().appendSpace().append(Component.text(plugin.getPluginMeta().getVersion()).color(NamedTextColor.BLUE));
        Instant duration = Instant.now().minusMillis(plugin.getStartTime().toEpochMilli());
        String uptime = MessageUtil.formatPlaytime(duration);
        Component line2 = Component.text("Uptime: ").color(NamedTextColor.GRAY).append(Component.text(uptime).color(NamedTextColor.GREEN));

        return line1.append(Component.newline()).append(line2).appendNewline();
    }



    private static final Component Footer = MiniMessage.miniMessage().deserialize("<newline><color:#4A0ff><b>Commands</b></color> <newline><gradient:#8E2DE2:#4A0ff>/team ⋆ /stats ⋆ /difficulty</gradient><newline>");

    private Component getFooter() {
        return Footer;
    }

}
