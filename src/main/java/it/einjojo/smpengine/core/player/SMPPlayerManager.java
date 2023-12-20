package it.einjojo.smpengine.core.player;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.database.PlayerDatabase;
import it.einjojo.smpengine.util.NameUUIDCache;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SMPPlayerManager {
    private final SMPEnginePlugin plugin;
    private final PlayerDatabase playerDatabase;
    private final LoadingCache<UUID, SMPPlayer> playerCache;

    public SMPPlayerManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.playerDatabase = new PlayerDatabase(plugin.getHikariCP());
        this.playerCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .evictionListener((uuid, smpPlayerObject, cause) -> {
                    if (!cause.wasEvicted()) {
                        return;
                    }
                    if (smpPlayerObject instanceof SMPPlayer smpPlayer) {
                        plugin.getLogger().info("Evicting player " + smpPlayer.getName() + " (" + smpPlayer.getUuid() + ")");
                        playerDatabase.updatePlayer(smpPlayer);
                    }
                })
                .expireAfterAccess(Duration.ofMinutes(5))
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

    public CompletableFuture<Optional<SMPPlayer>> getPlayerAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getPlayer(uuid)).exceptionally(throwable -> {
            plugin.getLogger().severe("Error while getting player " + uuid);
            throwable.printStackTrace();
            return Optional.empty();
        });
    }


    /**
     * Invalidates the cache and updates the player in the database.
     *
     * @param smpPlayer {@link SMPPlayer}
     */
    public void updatePlayer(SMPPlayer smpPlayer) {
        plugin.getLogger().info("Updating player " + smpPlayer.getName() + " (" + smpPlayer.getUuid() + ")");
        playerDatabase.updatePlayer(smpPlayer);
        playerCache.invalidate(smpPlayer.getUuid());
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

    public List<SMPPlayer> getPlayers(Collection<UUID> uuids) {
        Map<UUID, SMPPlayer> cached = playerCache.getAllPresent(uuids);
        ArrayList<UUID> toBeFetched = new ArrayList<>();
        for (UUID uuid : uuids) {
            if (!cached.containsKey(uuid)) {
                toBeFetched.add(uuid);
            }
        }
        if (toBeFetched.isEmpty()) { // Wenn alles im Cache ist
            return new ArrayList<>(cached.values());
        }
        List<SMPPlayer> fetched = playerDatabase.getPlayersByUUIDs(toBeFetched);
        fetched.forEach(smpPlayer -> {
            ((SMPPlayerImpl) smpPlayer).setPlugin(plugin);
            playerCache.put(smpPlayer.getUuid(), smpPlayer);
        });
        fetched.addAll(cached.values());
        return fetched;
    }


    /**
     * Updates all players in the database and invalidates the cache.
     * This method should be called when the server is shutting down.
     */
    public void closePlayers() {
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            getPlayer(onlinePlayer.getUniqueId()).ifPresentOrElse(smpPlayer -> {
                        SMPPlayerImpl impl = (SMPPlayerImpl) smpPlayer;
                        impl.setOnline(false);
                        updatePlayer(impl);
                        onlinePlayer.kick(Component.text("closed"));
                    },
                    () -> {
                        plugin.getLogger().warning("Player " + onlinePlayer.getName() + " (" + onlinePlayer.getUniqueId() + ") is empty!");
                        plugin.getLogger().warning("This player will not be saved!");
                    });
        }
    }

    /**
     * When the server starts, all players are offline.
     * It gets set in case the server got shut down unexpectedly and the players are still online in database.
     */
    public void setEveryoneOffline() {
        playerDatabase.updateEveryoneToOffline();
    }


}
