package it.einjojo.smpengine.core.team;

import it.einjojo.smpengine.core.player.SMPPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

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

    /**
     * @param player {@link SMPPlayer} to check if he is in team
     * @return true if player is in team, false if player is not in team
     */
    boolean isMember(SMPPlayer player);

    /**
     * @param player {@link SMPPlayer} to check if he is owner of team
     * @return true if player is owner of team, false if player is not owner of team
     */
    boolean isOwner(SMPPlayer player);

    List<Player> getOnlineMembers();


}
