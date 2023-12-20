package it.einjojo.smpengine.command.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.util.CommandUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.Iterator;
import java.util.List;

public class MemberSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public MemberSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, player -> plugin.getPlayerManager().getPlayerAsync(player.getUniqueId())
                .thenAcceptAsync(oSmpPlayer -> oSmpPlayer.orElseThrow().getTeam()
                        .ifPresentOrElse(
                                (team) -> player.sendMessage(buildMessage(team.getMembers(), team, player.getName())),
                                () -> player.sendMessage(plugin.getMessage("command.team.notInTeam"))
                        )
                ).exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                })
        );
    }

    public Component buildMessage(List<SMPPlayer> members, Team team, String receiver) {
        TextColor primary = plugin.getPrimaryColor();
        TextColor dark_gray = NamedTextColor.DARK_GRAY;
        TextColor muted = NamedTextColor.GRAY;
        Component border = Component.text("-------------------------").color(dark_gray)
                .decorate(TextDecoration.STRIKETHROUGH);
        Component owner = Component.text("Ersteller: ").color(muted);
        TextComponent.Builder builder = Component.text()
                .appendNewline()
                .append(border)
                .appendNewline()
                .append(owner)
                .append(Component.text(team.getOwner().getName()).color(primary)
                        .appendNewline()
                        .appendNewline()
                        .append(Component.text("Mitglieder: ").color(muted)));

        Iterator<SMPPlayer> iterator = members.iterator();
        while (iterator.hasNext()) {
            SMPPlayer member = iterator.next();
            String name = member.getName();
            if (name.equals(receiver)) {
                name = "Du";
            }

            Component onlineStatus = member.isOnline() ?
                    Component.text("✓").color(NamedTextColor.GREEN) :
                    Component.text("✗").color(NamedTextColor.RED);

            Component wrapped = Component.text("[").color(dark_gray)
                    .append(onlineStatus)
                    .append(Component.text("] ").color(dark_gray));

            builder.append(Component.text(name).color(primary)).appendSpace().append(wrapped);
            if (iterator.hasNext()) {
                builder.append(Component.text(", ").color(muted));
            }
        }
        return builder
                .appendNewline()
                .append(border)
                .build();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Erhalte eine Liste aller Teammitglieder";
    }

    @Override
    public String getCommand() {
        return "members";
    }
}
