package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.util.CommandUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;

import org.bukkit.command.CommandSender;

import java.time.format.DateTimeFormatter;
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
        TextColor primary = plugin.getPrimaryColor();
        TextColor dark_gray = NamedTextColor.DARK_GRAY;
        TextColor muted = NamedTextColor.GRAY;
        Component border = Component.text("-------------------------").color(dark_gray)
                .decorate(TextDecoration.STRIKETHROUGH);
        Component arrow = Component.text(" » ").color(dark_gray);

        Component line1_1 = Component.text("Name").color(muted);
        Component line1_2 = Component.text(team.getName()).color(primary);

        Component line2_1 = Component.text("Leader").color(muted);
        Component line2_2 = Component.text(team.getOwner().getName()).color(primary);

        Component line3_1 = Component.text("Mitglieder").color(muted);
        Component line3_2 = Component.text(team.getMembers().size()).color(primary);

        Component line4_1 = Component.text("Erstellt am").color(muted);
        String date = DateTimeFormatter.ofPattern("").format(null)

        Component line4_2 = Component.text().color(primary);

        Component message = Component.join(
                Component.newline(),
                border,
                Component.join(Component.newline(), line1_1, arrow, line1_2),
                Component.join(Component.newline(), line2_1, arrow, line2_2),
                Component.join(Component.newline(), line3_1, arrow, line3_2),
                Component.join(Component.newline(), line4_1, arrow, line4_2),

                Component.newline(),
                border);

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
