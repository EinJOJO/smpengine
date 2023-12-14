package it.einjojo.smpengine.core.player;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class SMPPlayerImpl implements SMPPlayer {
    private final UUID uuid;
    private boolean online;
    private final Instant firstJoin;
    private Instant lastJoin;
    private String name;
    private Integer teamId;

    public SMPPlayerImpl(UUID uuid, boolean online, Instant firstJoin, Instant lastJoin, String name, Integer teamId) {
        this.uuid = uuid;
        this.online = online;
        this.firstJoin = firstJoin;
        this.lastJoin = lastJoin;
        this.name = name;
        this.teamId = teamId;
    }


}
