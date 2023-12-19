package it.einjojo.smpengine.core.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.player.SMPPlayerImpl;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class TeamImpl implements Team {

    private int id;
    private String name;
    private Component displayName;
    private UUID owner_uuid;
    private final List<UUID> members;
    private Instant created_at;

    private transient SMPEnginePlugin plugin;

    public TeamImpl(int id, String name, Component displayName, UUID owner_uuid, Instant created_at, List<UUID> members) {
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


    public CompletableFuture<SMPPlayer> getOwnerAsync() {
        return CompletableFuture.supplyAsync(this::getOwner);
    }

    @Override
    public List<SMPPlayer> getMembers() {
        return Collections.unmodifiableList(plugin.getPlayerManager().getPlayers(members));
    }

    public CompletableFuture<List<SMPPlayer>> getMembersAsync() {
        return CompletableFuture.supplyAsync(this::getMembers);
    }


    @Override
    public boolean addMember(SMPPlayer player) {
        boolean isInTeam = members.contains(player.getUuid());
        if (isInTeam) {
            return false;
        }
        SMPPlayerImpl impl = (SMPPlayerImpl) player;
        impl.setTeamId(id);
        plugin.getPlayerManager().updatePlayer(impl);
        return members.add(player.getUuid());
    }

    @Override
    public boolean removeMember(SMPPlayer player) {
        return removeMember(player, false);
    }

    /**
     * @param player    {@link SMPPlayer} to remove from team
     * @param withOwner if true, owner can be removed from team
     * @return true if player was removed from team, false if player was not in team
     */
    public boolean removeMember(SMPPlayer player, boolean withOwner) {
        if (!withOwner && isOwner(player)) {
            throw new IllegalStateException("Owner cannot be removed from team");
        }
        SMPPlayerImpl impl = (SMPPlayerImpl) player;
        impl.setTeamId(null);
        plugin.getPlayerManager().updatePlayer(impl);
        return members.remove(player.getUuid());
    }

    @Override
    public boolean isMember(SMPPlayer player) {
        return members.contains(player.getUuid());
    }

    @Override
    public boolean isOwner(SMPPlayer player) {
        return owner_uuid.equals(player.getUuid());
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Team team)) {
            return false;
        }
        return team.getId() == id;
    }
}
