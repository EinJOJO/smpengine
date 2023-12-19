package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.util.CommandUtil;
import it.einjojo.smpengine.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class InfoSubCommand implements Command {

    private final SMPEnginePlugin plugin;


    public InfoSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            CommandUtil.requirePlayer(sender, player -> {
                Optional<Team> team = plugin.getPlayerManager().getPlayer(player.getUniqueId()).orElseThrow().getTeam();
                if (team.isEmpty()) {
                    player.sendMessage(plugin.getMessage("command.team.notInTeam"));
                    return;
                }
                Team team1 = team.get();
                printTeamInfo(team1, sender);
            });
        }
        if (args.length == 1) {
            // team info <team> (kann auch von der Konsole ausgeführt werden)
            Optional<Team> team = plugin.getTeamManager().getTeamByName(args[0]);
            if (team.isEmpty()) {
                sender.sendMessage(plugin.getMessage("command.team.notExist"));
                return;
            }
            printTeamInfo(team.get(), sender);
        }

    }

    public void printTeamInfo(Team team, CommandSender sender) {
        CompletableFuture.supplyAsync(() -> {
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
            Instant created_at = team.getCreated_at();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(created_at, ZoneId.systemDefault());
            String formattedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(localDateTime);
            Component line4_2 = Component.text(formattedDate).color(primary);

            return Component.text()
                    .appendNewline()
                    .append(border)
                    .appendNewline()
                    .append(line1_1).append(arrow).append(line1_2)
                    .appendNewline()
                    .append(line2_1).append(arrow).append(line2_2)
                    .appendNewline()
                    .append(line3_1).append(arrow).append(line3_2)
                    .appendNewline()
                    .append(line4_1).append(arrow).append(line4_2)
                    .appendNewline()
                    .append(border)
                    .build();
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return (TextComponent) plugin.getMessage(MessageUtil.KEY.GENERAL_ERROR);
        }).thenAccept(sender::sendMessage);

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return plugin.getTeamManager().getTeamNames();
        }
        return List.of("");
    }

    @Override
    public String getPermission() {
        return null;
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
