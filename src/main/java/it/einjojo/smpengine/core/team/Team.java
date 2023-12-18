package it.einjojo.smpengine.core.team;

import it.einjojo.smpengine.core.player.SMPPlayer;
import net.kyori.adventure.text.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Team {

    int getId();

    String getName();

    void setName(String name);

    Component getDisplayName();

    void setDisplayName(Component displayName);

    SMPPlayer getOwner();

    List<SMPPlayer> getMembers();

    CompletableFuture<List<SMPPlayer>> getMembersAsync();

    Instant getCreated_at();

    /**
     * @param player {@link SMPPlayer} to add to team
     * @return true if player was added to team, false if player was already in team
     */
    boolean addMember(SMPPlayer player);

    /**
     * @param player {@link SMPPlayer} to remove from team
     * @return true if player was removed from team, false if player was not in team
     */
    boolean removeMember(SMPPlayer player);

    boolean isMember(SMPPlayer player);

    boolean isOwner(SMPPlayer player);

}
