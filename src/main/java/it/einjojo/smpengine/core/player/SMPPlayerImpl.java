package it.einjojo.smpengine.core.player;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.session.Session;
import it.einjojo.smpengine.core.stats.Stats;
import it.einjojo.smpengine.core.team.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class SMPPlayerImpl implements SMPPlayer {
    private final UUID uuid;
    private boolean online;
    private final Instant firstJoin;
    private Instant lastJoin;
    private String name;
    private Integer teamId;

    private transient SMPEnginePlugin plugin;

    public SMPPlayerImpl(UUID uuid, boolean online, Instant firstJoin, Instant lastJoin, String name, Integer teamId) {
        this.uuid = uuid;
        this.online = online;
        this.firstJoin = firstJoin;
        this.lastJoin = lastJoin;
        this.name = name;
        this.teamId = teamId;
    }

    public Optional<Team> getTeam() {
        if (teamId == null) return Optional.empty();
        return plugin.getTeamManager().getTeamById(teamId);
    }

    @Override
    public boolean isInsideTeam() {
        return teamId != null;
    }

    @Override
    public CompletableFuture<Optional<Team>> getTeamAsync() {
        return CompletableFuture.supplyAsync(this::getTeam);
    }

    @Override
    public Stats getStats() {
        return plugin.getStatsManager().getByPlayer(uuid);
    }

    @Override
    public CompletableFuture<Stats> getStatsAsync() {
        return CompletableFuture.supplyAsync(this::getStats);
    }

    @Override
    public Optional<Session> getSession() {
        return plugin.getSessionManager().getSession(uuid);
    }

    @Override
    public CompletableFuture<Optional<Session>> getSessionAsync() {
        return CompletableFuture.supplyAsync(this::getSession);
    }

    public Player getPlayer() {
        return plugin.getServer().getPlayer(uuid);
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    @Override
    public String toString() {
        return "SMPPlayerImpl{" +
                "uuid=" + uuid +
                ", online=" + online +
                ", firstJoin=" + firstJoin +
                ", lastJoin=" + lastJoin +
                ", name='" + name + '\'' +
                ", teamId=" + teamId +
                ", plugin=" + plugin +
                '}';
    }
}
