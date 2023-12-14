package it.einjojo.smpengine.core.player;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.database.PlayerDatabase;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SMPPlayerManager {
    private final SMPEnginePlugin plugin;
    private final PlayerDatabase playerDatabase;

    public SMPPlayerManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.playerDatabase = new PlayerDatabase(plugin.getHikariCP());
    }

    public Optional<SMPPlayer> getPlayer(UUID uuid) {
        if (uuid == null) return Optional.empty();
        return playerDatabase.get(uuid);
    }

    public Optional<SMPPlayer> getPlayer(String name) {
        //TODO: Implement
        return Optional.empty();
    }

    public CompletableFuture<Optional<SMPPlayer>> getPlayerAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getPlayer(uuid));
    }

    public CompletableFuture<Optional<SMPPlayer>> getPlayerAsync(String name) {
        return CompletableFuture.supplyAsync(() -> getPlayer(name));
    }

    public void updatePlayer(SMPPlayer smpPlayer) {
        plugin.getLogger().info("Updating player " + smpPlayer.getName() + " (" + smpPlayer.getUuid() + ")");
        //TODO: Implement
    }

    public CompletableFuture<Void> updatePlayerAsync(SMPPlayer smpPlayer) {
        return CompletableFuture.runAsync(() -> updatePlayer(smpPlayer));
    }


    public Optional<SMPPlayer> createPlayer(UUID uuid, String name) {
        var smpPlayer = new SMPPlayerImpl(uuid, true, Instant.now(), Instant.now(), name, null);
        return playerDatabase.createPlayer(smpPlayer);
    }

}
