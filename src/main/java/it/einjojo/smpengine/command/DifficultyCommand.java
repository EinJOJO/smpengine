package it.einjojo.smpengine.command;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.util.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DifficultyCommand implements TabCompleter, CommandExecutor {

    private final SMPEnginePlugin plugin;
    private final HashMap<Player, Instant> cooldown;

    public DifficultyCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.cooldown = new HashMap<>();
        plugin.getCommand("difficulty").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player p)) {
            return true;
        }

        if (hasCooldown(p)) {
            sendCooldownMessage(p);
            return true;
        }

        if (args.length != 2) {
            sendUsageMessage(p);
            return true;
        }


        Difficulty difficulty;
        switch (args[0]) {
            case "easy":
            case "ez":
            case "1":
            case "noob":
            case "e":
                difficulty = Difficulty.EASY;
                break;
            case "normal":
            case "2":
            case "n":
                difficulty = Difficulty.NORMAL;
                break;
            case "hard":
            case "3":
            case "h":
            case "asian":
                difficulty = Difficulty.HARD;
                break;
            default:
                sendUsageMessage(p);
                return true;
        }

        long time;
        try {
            time = Long.parseLong(args[1]); //in minutes
        } catch (NumberFormatException e) {
            sendUsageMessage(p);
            return true;
        }

        if (time > 60) {
            sendUsageMessage(p);
        }

        setDifficulty(p, difficulty, time);


        return true;
    }

    private void sendUsageMessage(Player player) {
        player.sendMessage(plugin.getMessage("command.difficulty.usage"));
        player.sendMessage(plugin.getMessage("command.difficulty.maxTime"));
    }

    private void sendCooldownMessage(Player player) {
        player.sendMessage(plugin.getMessage("command.difficulty.cooldown"));
        Duration remainingTime = Duration.between(Instant.now(), cooldown.get(player));

        long time = remainingTime.toMinutesPart();

        player.sendMessage(Placeholder.applyPlaceholders(plugin.getMessage("command.difficulty.remainingTimeCooldown"), new Placeholder("time", String.valueOf(time))));
    }

    private void setDifficulty(Player player, Difficulty difficulty, long time) {
        World world = player.getWorld();
        world.setDifficulty(difficulty);
        setCooldown(player, time);

        Bukkit.broadcast(Placeholder.applyPlaceholders(plugin.getMessage("command.difficulty.change"), new Placeholder("player", player.getName()), new Placeholder("difficulty", difficulty.name())));
        Bukkit.broadcast(Placeholder.applyPlaceholders(plugin.getMessage("command.difficulty.length"), new Placeholder("time", String.valueOf(time))));

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            world.setDifficulty(Difficulty.NORMAL);
            Bukkit.broadcast(plugin.getMessage("command.difficulty.reset"));
        }, time * 60 * 20);
    }


    private void setCooldown(Player player, long seconds) {
        Instant time = Instant.now().plusSeconds(seconds * 60);
        cooldown.put(player, time);
    }

    private boolean hasCooldown(Player player) {
        if (cooldown.containsKey(player)) {
            if (cooldown.get(player).isBefore(Instant.now())) {
                cooldown.remove(player);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (!(sender instanceof Player)) {
            return arrayList;
        }

        if (args.length <= 1) {
            arrayList.add("easy");
            arrayList.add("normal");
            arrayList.add("hard");
            arrayList.add("1");
            arrayList.add("2");
            arrayList.add("3");
            arrayList.add("noob");
            arrayList.add("asian");
            return arrayList;
        } else if (args.length == 2) {
            arrayList.add("10");
            arrayList.add("30");
            arrayList.add("60");

            return arrayList;
        }
        return null;
    }
}
