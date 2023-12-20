package it.einjojo.smpengine.listener;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.config.MaintenanceConfig;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.player.SMPPlayerImpl;
import it.einjojo.smpengine.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PlayerJoinListener implements Listener {

    private final SMPEnginePlugin plugin;
    private final MaintenanceConfig maintenanceConfig;

    public PlayerJoinListener(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.maintenanceConfig = plugin.getMaintenanceConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void maintenanceCheck(PlayerLoginEvent event) {
        if (maintenanceConfig.isEnabled() && !event.getPlayer().hasPermission(maintenanceConfig.getBypassPermission())) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.kickMessage(MessageUtil.format(maintenanceConfig.getKickMessage(), plugin.getPrimaryColor(), plugin.getPrefix()));
        }
    }

    @EventHandler
    public void loadPlayer(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }
        Player player = event.getPlayer();
        CompletableFuture.supplyAsync(() -> plugin.getPlayerManager().getPlayer(player.getUniqueId()))
                .exceptionally((throwable) -> {
                    handleAsyncException(player, throwable);
                    return Optional.empty();
                })
                .thenAcceptAsync(smpPlayer -> handlePlayerData(player, smpPlayer));
    }

    private void handleAsyncException(Player player, Throwable throwable) {
        plugin.getLogger().warning("Failed to load player " + player.getName() + " (" + player.getUniqueId() + ")");
        syncKick(player, plugin.getMessage(MessageUtil.KEY.GENERAL_ERROR));
    }

    private void handlePlayerData(Player player, Optional<SMPPlayer> playerOptional) {
        if (playerOptional.isEmpty()) {
            playerOptional = plugin.getPlayerManager().createPlayer(player.getUniqueId(), player.getName());
        }
        SMPPlayerImpl smpPlayerImpl = (SMPPlayerImpl) playerOptional.orElseThrow(() -> {
            syncKick(player, plugin.getMessage("error.player-creation-failed"));
            return new IllegalStateException("Player creation failed");
        });
        smpPlayerImpl.setOnline(true);
        smpPlayerImpl.setName(player.getName());
        smpPlayerImpl.setLastJoin(Instant.now());
        CompletableFuture.runAsync(() -> plugin.getPlayerManager().updatePlayer(smpPlayerImpl))
                .exceptionally(throwable -> {
                    plugin.getLogger().warning("Failed to update player " + player.getName() + " (" + player.getUniqueId() + ")");
                    syncKick(player, plugin.getMessage(MessageUtil.KEY.GENERAL_ERROR));
                    return null;
                });
    }

    @EventHandler
    public void joinHandler(PlayerJoinEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getTablistManager().update(player);
        }
        Component joinMessage = event.joinMessage();
        if (joinMessage == null) {
            return;
        }
        event.joinMessage(plugin.getPrefix().append(joinMessage));
    }

    private void syncKick(Player player, Component kickMessage) {
        Bukkit.getScheduler().runTask(plugin, () -> player.kick(kickMessage));
    }
}
