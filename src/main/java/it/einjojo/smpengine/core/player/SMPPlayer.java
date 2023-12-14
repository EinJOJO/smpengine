package it.einjojo.smpengine.core.player;

import java.time.Instant;
import java.util.UUID;

public interface SMPPlayer {

    UUID getUuid();

    String getName();

    boolean isOnline();

    Instant getFirstJoin();

    Instant getLastJoin();

    Integer getTeamId();
}
