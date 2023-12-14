package it.einjojo.smpengine.database;

import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.player.SMPPlayerImpl;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class PlayerDatabase {
    private final HikariCP hikariCP;

    public PlayerDatabase(HikariCP hikariCP) {
        this.hikariCP = hikariCP;
    }

    public Optional<SMPPlayer> get(@NotNull UUID uuid) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `spieler` WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(new SMPPlayerImpl(
                            UUID.fromString(rs.getString("uuid")),
                            rs.getBoolean("online"),
                            rs.getTimestamp("first_join").toInstant(),
                            rs.getTimestamp("last_join").toInstant(),
                            rs.getString("name"),
                            rs.getObject("team_id", Integer.class)
                    ));
                }
            }
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    public Optional<SMPPlayer> createPlayer(@NotNull SMPPlayer player) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO `spieler`(uuid, online, first_join, last_join, name) VALUES(?, ?, ?, ?, ?)")) {
                ps.setString(1, player.getUuid().toString());
                ps.setBoolean(2, player.isOnline());
                ps.setTimestamp(3, Timestamp.from(player.getFirstJoin()));
                ps.setTimestamp(4, Timestamp.from(player.getLastJoin()));
                ps.setString(5, player.getName());
                int res = ps.executeUpdate();
                if (res == 0) {
                    return Optional.empty();
                }
                return Optional.of(player);
            }
        } catch (SQLException e) {
            return Optional.empty();
        }
    }


}
