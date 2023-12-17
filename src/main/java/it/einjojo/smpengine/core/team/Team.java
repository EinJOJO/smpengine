package it.einjojo.smpengine.core.team;

import it.einjojo.smpengine.core.player.SMPPlayer;

import java.time.Instant;
import java.util.List;

public interface Team {

    String getName();
    String getDisplayName();
    SMPPlayer getOwner();

    List<SMPPlayer> getMembers();
    Instant getCreated_at();
}
