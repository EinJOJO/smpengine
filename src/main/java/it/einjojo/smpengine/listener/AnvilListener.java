package it.einjojo.smpengine.listener;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnvilListener implements Listener {

    private final SMPPlayerManager playerManager;

    private final Map<UUID, BossBar> bossBarMap = new HashMap<>();

    private final SMPEnginePlugin plugin;

    public AnvilListener(SMPEnginePlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.playerManager = plugin.getPlayerManager();
        this.plugin = plugin;
    }

    @EventHandler
    public void removeLevelCap(PrepareAnvilEvent e) {
        AnvilInventory inventory = e.getInventory();
        Player player = (Player) e.getView().getPlayer();
        inventory.setMaximumRepairCost(Integer.MAX_VALUE);
        BossBar bossBar = bossBarMap.get(player.getUniqueId());
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar(
                    "",
                    BarColor.WHITE,
                    BarStyle.SOLID
            );
            bossBar.addPlayer(player);
            bossBarMap.put(player.getUniqueId(), bossBar);
        }
        String textColor;
        double requirement = (double) player.getLevel() / (double) inventory.getRepairCost();
        double progress = Math.min(1.0, Math.max(0.0, requirement));
        if (requirement >= 1) {
            bossBar.setColor(BarColor.GREEN);
            textColor = "§a";
        } else {
            bossBar.setColor(BarColor.RED);
            textColor = "§c";
        }
        bossBar.setProgress(progress);
        bossBar.setTitle("§7Kosten: " + textColor + inventory.getRepairCost());
        bossBar.setVisible(true);
    }

    @EventHandler
    public void onCloseAnvil(InventoryCloseEvent e) {
        if (e.getInventory().getType().equals(InventoryType.ANVIL)) {
            BossBar bossbar = bossBarMap.get(e.getPlayer().getUniqueId());

            if (bossbar != null) {
                bossbar.setVisible(false);
            }
        }
    }
}
