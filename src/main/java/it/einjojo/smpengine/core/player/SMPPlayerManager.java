package it.einjojo.smpengine.core.player;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.database.PlayerDatabase;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class SMPPlayerManager {
    private final SMPEnginePlugin plugin;
    private final PlayerDatabase playerDatabase;

    LoadingCache<UUID, SMPPlayer> playerCache;



    public SMPPlayerManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.playerDatabase = new PlayerDatabase(plugin.getHikariCP());
        this.playerCache = Caffeine.newBuilder()
                .build(playerDatabase::get);
    }

    public Optional<SMPPlayer> getPlayer(UUID uuid) {
        if (uuid == null) return Optional.empty();
        return Optional.ofNullable(playerCache.get(uuid));
    }

    public Optional<SMPPlayer> getPlayer(String name) {
        return Optional.empty();
    }


    public void updatePlayer(SMPPlayer smpPlayer) {
        plugin.getLogger().info("Updating player " + smpPlayer.getName() + " (" + smpPlayer.getUuid() + ")");
        //TODO: Implement
    }


    public Optional<SMPPlayer> createPlayer(UUID uuid, String name) {
        plugin.getLogger().info("Creating player " + name + " (" + uuid + ")");
        var smpPlayer = new SMPPlayerImpl(uuid, true, Instant.now(), Instant.now(), name, null);
        return Optional.ofNullable(playerDatabase.createPlayer(smpPlayer));
    }

}
