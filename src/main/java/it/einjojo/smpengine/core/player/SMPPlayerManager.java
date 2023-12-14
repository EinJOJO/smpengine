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

    public Optional<SMPPlayerImpl> getPlayer(UUID uuid) {
        return Optional.empty();
    }

    public Optional<SMPPlayerImpl> getPlayer(String name) {
        return Optional.empty();
    }

    public CompletableFuture<Optional<SMPPlayerImpl>> getPlayerAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getPlayer(uuid));
    }

    public CompletableFuture<Optional<SMPPlayerImpl>> getPlayerAsync(String name) {
        return CompletableFuture.supplyAsync(() -> getPlayer(name));
    }

    public void updatePlayer(SMPPlayerImpl smpPlayer) {
        //playerDatabase.updatePlayer(smpPlayer);
    }

    public CompletableFuture<Void> updatePlayerAsync(SMPPlayerImpl smpPlayer) {
        return CompletableFuture.runAsync(() -> updatePlayer(smpPlayer));
    }


    public Optional<SMPPlayerImpl> createPlayer(UUID uuid, String name) {
        var smpPlayer = new SMPPlayerImpl(uuid, true, Instant.now(), Instant.now(), name, null);
        return playerDatabase.createPlayer(smpPlayer);
    }

}
