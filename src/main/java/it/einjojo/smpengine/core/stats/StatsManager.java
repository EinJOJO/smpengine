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
    private final AsyncLoadingCache<UUID, Stats> statsCacheByUUID;
    private final AsyncLoadingCache<Integer, Stats> statsCacheByTeam;// player, global-stats

    public StatsManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.statsDatabase = new StatsDatabase(plugin.getHikariCP(), plugin);
        statsCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(2)) // Update stats every 2 minutes
                .evictionListener((k, v, cause) -> {
                    if (v instanceof Stats stats) {
                        plugin.getLogger().info("updating "+ stats +" of player " + stats.getPlayer().getName() + " sessionID " + ((StatsImpl) stats).getSessionID());
                        statsDatabase.updateStats(stats);
                    }
                })
                .build();
        statsCacheByUUID = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofSeconds(10))
                .buildAsync((uuid, executor) -> getGlobalStatsOfPlayer(uuid));
        statsCacheByTeam = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(10))
                .buildAsync((integer, executor) -> getGlobalStatsOfTeam(integer));
    }


    public Stats getByPlayer(UUID player) {
        return statsCacheByUUID.synchronous().get(player);
    }

    public CompletableFuture<Stats> getByPlayerAsync(UUID player) {
        return statsCacheByUUID.get(player);
    }

    public Stats getByTeam(int id) {
        return statsCacheByTeam.synchronous().get(id);
    }

    public CompletableFuture<Stats> getByTeamAsync(int id) {
        return statsCacheByTeam.get(id);
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
        return CompletableFuture.supplyAsync(() -> {
            GlobalStats s1 = statsDatabase.getGlobalStats(player);
            if (s1 != null) {
                plugin.getSessionManager().getSession(player).ifPresent((v) -> {
                    Stats s2 = v.getSessionStats();
                    s1.add(s2);
                });
                applyMeta(s1);
                return s1;
            }
            return null;
        });
    }

    private CompletableFuture<Stats> getGlobalStatsOfTeam(int id) { //TODO Eventuell anderes Verfahren.
        Stats stats = statsDatabase.getTeamStats(id);
        if (stats != null) {
            applyMeta(stats);
            return CompletableFuture.completedFuture(stats);
        }
        return CompletableFuture.completedFuture(null);
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
