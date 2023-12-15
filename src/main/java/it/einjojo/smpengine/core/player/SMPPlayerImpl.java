package it.einjojo.smpengine.core.player;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.team.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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

    /**
     * @return {@link Team} or null if player is not in a team.
     */
    public Optional<Team> getTeam() {
        if (teamId == null) return Optional.empty();
        return plugin.getTeamManager().getTeam(teamId);
    }


    /**
     * @return {@link Player} or null if player is offline.
     */
    public Player getPlayer() {
        return plugin.getServer().getPlayer(uuid);
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
