package it.einjojo.smpengine.database;

import it.einjojo.smpengine.core.player.SMPPlayer;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class PlayerDatabase {
    private final HikariCP hikariCP;

    public PlayerDatabase(HikariCP hikariCP) {
        this.hikariCP = hikariCP;
    }

    public void createPlayer(SMPPlayer player) {

    }


}
