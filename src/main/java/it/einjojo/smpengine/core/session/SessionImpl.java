package it.einjojo.smpengine.core.session;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.stats.Stats;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class SessionImpl implements Session {

    private final Integer sessionId;
    private final UUID uuid;
    private String ip;
    private Instant startTime;
    private Instant endTime;

    private transient SMPEnginePlugin plugin;

    public SessionImpl(int session_id, UUID uuid, String ip, Instant startTime, Instant endTime) {
        this.sessionId = session_id;
        this.uuid = uuid;
        this.ip = ip;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public boolean isActive() {
        return endTime == null;
    }

    @Override
    public Stats getSessionStats() {
        Stats stats = plugin.getStatsManager().getBySession(getSessionId());
        if (stats == null) {
            plugin.getStatsManager().createStats(this);
            return plugin.getStatsManager().getBySession(getSessionId());
        }
        return stats;
    }

    @Override
    public CompletableFuture<Stats> getSessionStatsAsync() {
        return CompletableFuture.supplyAsync(this::getSessionStats);
    }

    @Override
    public SMPPlayer getPlayer() {
        return plugin.getPlayerManager().getPlayer(uuid).orElseThrow(() -> new IllegalStateException("Player { " + uuid + " } is not loaded"));

    }

    @Override
    public CompletableFuture<SMPPlayer> getPlayerAsync() {
        return CompletableFuture.supplyAsync(this::getPlayer);
    }

    @Override
    public Instant duration() {
        if (endTime == null) {
            return Instant.now().minusMillis(startTime.toEpochMilli());
        }
        return endTime.minusMillis(startTime.toEpochMilli());
    }

    @Override
    public String toString() {
        return "SessionImpl{" +
                "session_id=" + sessionId +
                ", uuid=" + uuid +
                ", ip='" + ip + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", plugin=" + plugin +
                '}';
    }
}
