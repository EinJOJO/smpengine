package it.einjojo.smpengine.listener;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.player.SMPPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;

import java.util.Optional;

public class AnvilListener implements Listener {

    private final SMPPlayerManager playerManager;
    private final SMPEnginePlugin plugin;

    public AnvilListener (SMPEnginePlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.playerManager = plugin.getPlayerManager();
        this.plugin = plugin;
    }

    @EventHandler
    public void removeLevelCap(PrepareAnvilEvent e) {
        AnvilInventory inventory = e.getInventory();
        Player player = (Player) e.getView().getPlayer();


        inventory.setMaximumRepairCost(Integer.MAX_VALUE);


        Optional<SMPPlayer> survivalPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        BossBar bossBar = plugin.getPlayerManager().getBossBar();
        if(bossBar == null) {
            bossBar = Bukkit.createBossBar(
                    "",
                    BarColor.WHITE,
                    BarStyle.SOLID
            );
            bossBar.addPlayer(player);
            plugin.getPlayerManager().setBossBar(bossBar);
        }
        String textColor;
        double progress = (double) player.getLevel() / (double) inventory.getRepairCost();

        if(Double.isNaN(progress)) {
            progress = 0D;
        }

        if(progress >= 1) {
            progress = 1D;
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
    public void applyCustomEnchants(PrepareAnvilEvent e) {
        ItemStack tool = e.getInventory().getItem(0);
        ItemStack enchantmentBook = e.getInventory().getItem(1);
        if(tool == null) return;
        if(enchantmentBook == null) return;
        if(enchantmentBook.getType() != Material.ENCHANTED_BOOK) return;
        ItemMeta enchantmentBookItemMeta = enchantmentBook.getItemMeta();
        if(enchantmentBookItemMeta == null) return;
        Map<Enchantment, Integer> enchantmentLevelMap = enchantmentBookItemMeta.getEnchants();
        if(enchantmentLevelMap.size() == 0) return;


        ItemStack result = new ItemStack(tool);
        ItemMeta resultItemMeta = result.getItemMeta();

        List<String> lore = new ArrayList<>();
        if(tool.getItemMeta() != null) {
            if(tool.getItemMeta().getLore() != null) {
                lore = tool.getItemMeta().getLore();
            }
        }
        List<String> finalLore = lore;
        enchantmentLevelMap.forEach(((enchantment, level) -> {
            String text = "§7" + enchantment.getName() + " " + TextUtil.toRoman(level);
            if(!finalLore.contains(text)) {
                finalLore.add(text);
            }
        }));


        resultItemMeta.setDisplayName(e.getInventory().getRenameText());
        resultItemMeta.setLore(finalLore);
        result.setItemMeta(resultItemMeta);
        result.addUnsafeEnchantments(enchantmentLevelMap);

        e.setResult(result);
    }

    @EventHandler
    public void onCloseAnvil(InventoryCloseEvent e) {
        if(e.getInventory().getType().equals(InventoryType.ANVIL)) {
            BossBar bossbar = playerManager.getPlayer((Player) e.getPlayer()).getBossBar();

            if(bossbar != null) {
                bossbar.setVisible(false);
            }
        }
    }
}
