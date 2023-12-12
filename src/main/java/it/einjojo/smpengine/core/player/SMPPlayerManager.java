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
        return Optional.empty();
    }

    public Optional<SMPPlayer> getPlayer(String name) {
        return Optional.empty();
    }

    public CompletableFuture<Optional<SMPPlayer>> getPlayerAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getPlayer(uuid));
    }

    public CompletableFuture<Optional<SMPPlayer>> getPlayerAsync(String name) {
        return CompletableFuture.supplyAsync(() -> getPlayer(name));
    }


    public SMPPlayer createPlayer(UUID uuid) {
        var smpPlayer = new SMPPlayer(uuid, null, true, Instant.now(), Instant.now());
        playerDatabase.createPlayer(smpPlayer);
        return smpPlayer;
    }

}
