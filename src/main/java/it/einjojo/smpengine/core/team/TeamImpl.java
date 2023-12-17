package it.einjojo.smpengine.core.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.player.SMPPlayerImpl;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TeamImpl implements Team {

    private int id;
    private String name;
    private String displayName;
    private UUID owner_uuid;
    private final List<UUID> members;
    private Instant created_at;

    private transient SMPEnginePlugin plugin;

    public TeamImpl(int id, String name, String displayName, UUID owner_uuid, Instant created_at, List<UUID> members) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.owner_uuid = owner_uuid;
        this.created_at = created_at;
        this.members = members;
    }

    @Override
    /**
     * WARNING: This method might do database calls.
     * @return {@link SMPPlayer} or throw {@link IllegalStateException} if owner is not in database.
     */
    public SMPPlayer getOwner() {
        return plugin.getPlayerManager().getPlayer(owner_uuid).orElseThrow(() -> new IllegalStateException("Owner is not in database"));
    }

    /**
     * WARNING: This method might do database calls.
     * @return {@link Collection<SMPPlayer>} of members
     */
    @Override
    public List<SMPPlayer> getMembers() {
        return Collections.unmodifiableList(plugin.getPlayerManager().getPlayers(members));
    }

    @Override
    public void addMember(SMPPlayer player) {
        SMPPlayerImpl impl = (SMPPlayerImpl) player;
        impl.setTeamId(id);
        members.add(player.getUuid());
    }

    @Override
    public void removeMember(SMPPlayer player) {
        SMPPlayerImpl impl = (SMPPlayerImpl) player;
        impl.setTeamId(null);
        members.remove(player.getUuid());
    }
}
