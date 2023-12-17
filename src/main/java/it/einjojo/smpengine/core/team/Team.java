package it.einjojo.smpengine.core.team;

import it.einjojo.smpengine.core.player.SMPPlayer;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface Team {

    String getName();
    void setName(String name);

    String getDisplayName();
    void setDisplayName(String displayName);

    SMPPlayer getOwner();

    List<SMPPlayer> getMembers();

    Instant getCreated_at();

    void addMember(SMPPlayer player);

    void removeMember(SMPPlayer player);

}
