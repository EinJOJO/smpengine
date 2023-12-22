package it.einjojo.smpengine.core.stats;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.session.Session;
import it.einjojo.smpengine.database.StatsDatabase;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StatsManager {

    private final SMPEnginePlugin plugin;
    private final StatsDatabase statsDatabase;
    private final Cache<Integer, Stats> statsCache; // sessionID, stats
    private final AsyncLoadingCache<UUID, Stats> statsCacheByUUID; // player, global-stats

    public StatsManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.statsDatabase = new StatsDatabase(plugin.getHikariCP());
        statsCache = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(5))
                .evictionListener((k, v, cause) -> {
                    if (v instanceof Stats stats) {
                        statsDatabase.updateStats(stats);
                    }
                })
                .build();
        statsCacheByUUID = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(10))
                .buildAsync((uuid, executor) -> getGlobalStatsOfPlayer(uuid));
    }


    public Stats getByPlayer(UUID player ){
        return null;
    }

    /**
     * Gets the stats of a player by their UUID
     *
     * @param sessionID SessionID related to the stats or null if sessionID is not found
     * @return The stats of the session
     */
    public Stats getBySession(int sessionID) {
        // cache lookup
        Stats stats = statsCache.getIfPresent(sessionID);
        if (stats != null) {
            return stats;
        }
        // fetch from database
        Stats stats2 = statsDatabase.getBySession(sessionID);
        if (stats2 != null) {
            applyMeta(stats2);
            statsCache.put(sessionID, stats2);
        }
        return stats2;
    }

    void applyMeta(Stats stats) {
        if (stats instanceof StatsImpl) {
            ((StatsImpl) stats).setPlugin(plugin);

        }
    }

    public void createStats(Session session) {
        if (!statsDatabase.createStats(session.getSessionId(), session.getUuid())) {
            throw new IllegalStateException("Failed to create stats for session { " + session.getSessionId() + " }");
        }
    }

    private CompletableFuture<Stats> getGlobalStatsOfPlayer(UUID player) {
        return null;
    }

    public void updateStats(Stats stats) {
        StatsImpl statsImpl = (StatsImpl) stats;
        if (statsImpl.getSessionID() == null) return;
        statsDatabase.updateStats(stats);
        statsCache.invalidate(statsImpl.getSessionID());
    }

    public void closeStats() {
        statsCache.asMap().values().forEach(this::updateStats);
    }
}
