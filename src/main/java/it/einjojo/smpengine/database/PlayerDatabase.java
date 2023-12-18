package it.einjojo.smpengine.database;

import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.player.SMPPlayerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
                    return rsToSMPPlayer(rs);
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
    public SMPPlayer updatePlayer(@NotNull SMPPlayer smpPlayer) {
        SMPPlayerImpl player = (SMPPlayerImpl) smpPlayer;
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

    public List<SMPPlayer> getPlayersByUUIDs(List<UUID> uuids) {
        List<SMPPlayer> players = new ArrayList<>();
        if (uuids.isEmpty()) {
            return players;
        }
        String placeholders = String.join(",", Collections.nCopies(uuids.size(), "?"));
        String query = "SELECT uuid, name, team_id, online, first_join, last_join FROM spieler WHERE uuid IN (" + placeholders + ")";

        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                for (int i = 1; i <= uuids.size(); i++) {
                    ps.setString(i, uuids.get(i).toString());
                }
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        players.add(rsToSMPPlayer(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public SMPPlayer rsToSMPPlayer(ResultSet rs) {
        try {
            UUID uuid = UUID.fromString(rs.getString("uuid"));
            String name = rs.getString("name");
            Integer teamId = rs.getObject("team_id", Integer.class);
            boolean online = rs.getBoolean("online");
            Instant firstJoin = rs.getTimestamp("first_join").toInstant();
            Instant lastJoin = rs.getTimestamp("last_join").toInstant();
            return new SMPPlayerImpl(uuid, online, firstJoin, lastJoin, name, teamId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
