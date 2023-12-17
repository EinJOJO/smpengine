package it.einjojo.smpengine.core.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TeamImpl implements Team {

    private int id;
    private String name;
    private String displayName;
    private UUID owner_uuid;
    private final Collection<UUID> members;
    private Instant created_at;

    private transient SMPEnginePlugin plugin;

    public TeamImpl(int id, String name, String displayName, UUID owner_uuid, Instant created_at, Collection<UUID> members) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.owner_uuid = owner_uuid;
        this.created_at = created_at;
        this.members = members;
    }

    @Override
    public SMPPlayer getOwner() {
        return plugin.getPlayerManager().getPlayer(owner_uuid).orElseThrow(() -> new IllegalStateException("Owner is not in database"));
    }

    @Override
    public List<SMPPlayer> getMembers() {
        return null; // TODO: 12/17/2023
    }
}
