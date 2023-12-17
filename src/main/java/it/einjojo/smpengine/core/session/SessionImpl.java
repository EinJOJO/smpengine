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

    private final int session_id;
    private final UUID uuid;
    private String ip;
    private Instant startTime;
    private Instant endTime;

    private transient SMPEnginePlugin plugin;

    public SessionImpl(int session_id, UUID uuid, String ip, Instant startTime, Instant endTime) {
        this.session_id = session_id;
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
        return null;
    }

    @Override
    public SMPPlayer getPlayer() {
        var player = plugin.getPlayerManager().getPlayer(uuid);
        if (player.isEmpty()) {
            throw new IllegalStateException("Player { " + uuid + " } is not loaded");
        }
        return player.get();
    }

    @Override
    public CompletableFuture<SMPPlayer> getPlayerAsync() {
        return CompletableFuture.supplyAsync(this::getPlayer);
    }

    @Override
    public String toString() {
        return "SessionImpl{" +
                "session_id=" + session_id +
                ", uuid=" + uuid +
                ", ip='" + ip + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", plugin=" + plugin +
                '}';
    }
}
