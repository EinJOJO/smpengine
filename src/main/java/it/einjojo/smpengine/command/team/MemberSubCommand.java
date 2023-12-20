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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class MemberSubCommand implements Command {

    private final SMPEnginePlugin plugin;

    public MemberSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandUtil.requirePlayer(sender, player -> {
            Optional<Team> optionalTeam = plugin.getPlayerManager().getPlayer(player.getUniqueId()).orElseThrow().getTeam();
            if (optionalTeam.isEmpty()) {
                player.sendMessage(plugin.getMessage("command.team.notInTeam"));
                return;
            }
            Team team = optionalTeam.get();
            player.sendMessage(buildMessage(team.getMembers(), team));
        });
    }

    public Component buildMessage(List<SMPPlayer> members, Team team) {
        List<SMPPlayer> modifiableMembers = new ArrayList<>(members);
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



        Iterator<SMPPlayer> iterator = modifiableMembers.iterator();
        while (iterator.hasNext()) {
            SMPPlayer member = iterator.next();
            if (member.getName().equals(team.getOwner().getName())) {
                iterator.remove();
            }
        }
        iterator = modifiableMembers.iterator();
        while(iterator.hasNext()){
            SMPPlayer member = iterator.next();
            Component playerName = Component.text(member.getName()).color(primary);
            Component comma = Component.text(", ").color(muted);
            builder.append(playerName);
            if(iterator.hasNext()){
                builder.append(comma);
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
