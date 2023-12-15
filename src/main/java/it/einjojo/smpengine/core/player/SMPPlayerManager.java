package it.einjojo.smpengine.core.player;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.database.PlayerDatabase;
import it.einjojo.smpengine.util.NameUUIDCache;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class SMPPlayerManager {
    private final SMPEnginePlugin plugin;
    private final PlayerDatabase playerDatabase;
    private final LoadingCache<UUID, SMPPlayer> playerCache;

    public SMPPlayerManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.playerDatabase = new PlayerDatabase(plugin.getHikariCP());
        this.playerCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(Duration.ofMinutes(10))
                .build(this::getByUUIDUncached);
    }

    private SMPPlayer getByUUIDUncached(UUID uuid) {
        plugin.getLogger().info("Loading player " + uuid);
        SMPPlayerImpl smpPlayer = (SMPPlayerImpl) playerDatabase.get(uuid);
        if (smpPlayer == null) return null;
        smpPlayer.setPlugin(plugin);
        return smpPlayer;
    }

    /**
     * @param uuid UUID of Player from cache or database.
     * @return {@link SMPPlayer}
     */
    public Optional<SMPPlayer> getPlayer(UUID uuid) {
        if (uuid == null) return Optional.empty();
        return Optional.ofNullable(playerCache.get(uuid));
    }

    /**
     * @param name Name of Player
     * @return {@link SMPPlayer}
     */
    public Optional<SMPPlayer> getPlayer(String name) {
        UUID uuid = NameUUIDCache.getUUID(name);
        if (uuid == null) return Optional.empty();
        return getPlayer(uuid);
    }


    /**
     * Invalidates the cache and updates the player in the database.
     *
     * @param smpPlayer {@link SMPPlayer}
     */
    public void updatePlayer(SMPPlayer smpPlayer) {
        plugin.getLogger().info("Updating player " + smpPlayer.getName() + " (" + smpPlayer.getUuid() + ")");
        playerCache.invalidate(smpPlayer.getUuid());
        playerDatabase.updatePlayer(smpPlayer);
    }


    /**
     * Creates a new player in the database.
     *
     * @param uuid UUID of Player
     * @param name Name of Player
     * @return {@link SMPPlayer}
     */
    public Optional<SMPPlayer> createPlayer(UUID uuid, String name) {
        plugin.getLogger().info("Creating player " + name + " (" + uuid + ")");
        var smpPlayer = new SMPPlayerImpl(uuid, true, Instant.now(), Instant.now(), name, null);
        smpPlayer.setPlugin(plugin);
        return Optional.ofNullable(playerDatabase.createPlayer(smpPlayer));
    }

}
