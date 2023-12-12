package it.einjojo.smpengine.core.player;

import it.einjojo.smpengine.core.team.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SMPPlayer {
    private final UUID uuid;
    private Team team;
    private boolean online;
    private final Instant firstJoin;
    private Instant lastJoin;

}
