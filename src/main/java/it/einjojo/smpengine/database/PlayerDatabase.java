package it.einjojo.smpengine.database;

import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.player.SMPPlayerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.UUID;

public class PlayerDatabase {
    private final HikariCP hikariCP;

    public PlayerDatabase(HikariCP hikariCP) {
        this.hikariCP = hikariCP;
    }

    @Nullable
    public SMPPlayer get(@NotNull UUID uuid) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `spieler` WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    return new SMPPlayerImpl(
                            UUID.fromString(rs.getString("uuid")),
                            rs.getBoolean("online"),
                            rs.getTimestamp("first_join").toInstant(),
                            rs.getTimestamp("last_join").toInstant(),
                            rs.getString("name"),
                            rs.getObject("team_id", Integer.class)
                    );
                }
            }
        } catch (SQLException e) {
            return null;
        }
    }

    @Nullable
    public SMPPlayer createPlayer(@NotNull SMPPlayer player) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO `spieler`(uuid, online, first_join, last_join, name) VALUES(?, ?, ?, ?, ?)")) {
                ps.setString(1, player.getUuid().toString());
                ps.setBoolean(2, player.isOnline());
                ps.setTimestamp(3, Timestamp.from(player.getFirstJoin()));
                ps.setTimestamp(4, Timestamp.from(player.getLastJoin()));
                ps.setString(5, player.getName());
                int res = ps.executeUpdate();
                if (res == 0) {
                    return null;
                }
                return player;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    @Nullable
    public SMPPlayer updatePlayer(@NotNull SMPPlayer player) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("UPDATE `spieler` SET online = ?, last_join = ?, name = ?, team_id = ? WHERE uuid = ?")) {
                ps.setBoolean(1, player.isOnline());
                ps.setTimestamp(2, Timestamp.from(player.getLastJoin()));
                ps.setString(3, player.getName());
                ps.setObject(4, player.getTeamId());
                ps.setString(5, player.getUuid().toString());
                ps.executeUpdate();
                return player;
            }
        } catch (SQLException e) {
            return null;
        }
    }


}
