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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerJoinListener implements Listener {

    private final SMPEnginePlugin plugin;
    private final MaintenanceConfig maintenanceConfig;

    public PlayerJoinListener(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.maintenanceConfig = plugin.getMaintenanceConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void maintenanceCheck(PlayerLoginEvent event) {
        if (maintenanceConfig.isEnabled() && !event.getPlayer().hasPermission(maintenanceConfig.getBypassPermission())) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.kickMessage(MessageUtil.format(maintenanceConfig.getKickMessage(), plugin.getPrimaryColor(), plugin.getPrefix()));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
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
                .thenAcceptAsync(smpPlayer -> handlePlayerData(player, smpPlayer.orElse(null)));
    }


    private void handleAsyncException(Player player, Throwable throwable) {
        plugin.getLogger().warning("Failed to load player " + player.getName() + " (" + player.getUniqueId() + ")");
        syncKick(player, plugin.getMessage(MessageUtil.KEY.GENERAL_ERROR));
    }

    private void handlePlayerData(Player player, SMPPlayer smpPlayer) {
        if (player == null) return;
        if (smpPlayer == null) {
            smpPlayer = plugin.getPlayerManager().createPlayer(player.getUniqueId(), player.getName()).orElseThrow(() -> {
                syncKick(player, plugin.getMessage("error.player-creation-failed"));
                return new IllegalStateException("Player creation failed");
            });
        }
        SMPPlayerImpl smpPlayerImpl = (SMPPlayerImpl) smpPlayer;
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
        event.joinMessage(plugin.getPrefix().appendSpace().append(joinMessage));

        UUID uuid = event.getPlayer().getUniqueId();
        plugin.getPlayerManager().getPlayerAsync(uuid)
                .thenAcceptAsync((smpPlayer) -> plugin.getSessionManager().startSession(smpPlayer.orElseThrow()))
                .thenRunAsync(() -> plugin.getSessionManager().getSession(uuid).orElseThrow().getSessionStats())
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    plugin.getLogger().warning("Failed to start session for " + event.getPlayer().getName() + " (" + event.getPlayer().getUniqueId() + ")");
                    syncKick(event.getPlayer(), plugin.getMessage(MessageUtil.KEY.GENERAL_ERROR));
                    return null;
                });

    }

    private void syncKick(Player player, Component kickMessage) {
        Bukkit.getScheduler().runTask(plugin, () -> player.kick(kickMessage));
    }
}
