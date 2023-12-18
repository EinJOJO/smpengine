package it.einjojo.smpengine.core.team;

import it.einjojo.smpengine.core.player.SMPPlayer;
import net.kyori.adventure.text.Component;

import java.time.Instant;
import java.util.List;

public interface Team {

    String getName();

    void setName(String name);

    Component getDisplayName();

    void setDisplayName(Component displayName);

    SMPPlayer getOwner();

    List<SMPPlayer> getMembers();

    Instant getCreated_at();

    void addMember(SMPPlayer player);

    void removeMember(SMPPlayer player);

}
