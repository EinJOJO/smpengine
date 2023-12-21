package it.einjojo.smpengine.database;

import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.player.SMPPlayerImpl;
import it.einjojo.smpengine.core.session.Session;
import it.einjojo.smpengine.core.session.SessionImpl;
import it.einjojo.smpengine.core.stats.StatsImpl;

import java.sql.*;
import java.time.Instant;
import java.util.UUID;

public class SessionDatabase {

    private final HikariCP hikariCP;

    public SessionDatabase(HikariCP hikariCP) {
        this.hikariCP = hikariCP;
    }


    public Session getSession(String uuid) {
        try(Connection connection = hikariCP.getConnection()){
            try(PreparedStatement ps = connection.prepareStatement("SELECT * FROM sessions WHERE player_uuid = ?")){
                ps.setString(1, uuid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    return rsToSession(rs);
                }

            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Creates a session in the database
     *
     * @param session The session to be saved
     * @return The session that was saved in the database or null if an error occurred
     */
    public boolean createSession(SessionImpl session) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO sessions (session_owner_uuid, ip_address, login_at) VALUES (?, ?, ?)")) {
                ps.setString(1, session.getUuid().toString());
                ps.setString(2, session.getIp());
                ps.setTimestamp(3, Timestamp.from(session.getStartTime()));
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateSession(SessionImpl session) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("UPDATE sessions SET logout_at = ? WHERE session_owner_uuid = ?")) {
                ps.setString(1, session.getUuid().toString());
                ps.setString(2, session.getIp());
                ps.setTimestamp(3, Timestamp.from(session.getStartTime()));
                ps.setTimestamp(4, Timestamp.from(session.getEndTime()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Session rsToSession(ResultSet rs) {
        try {
            Integer sessionID = rs.getObject("session_id", Integer.class);
            UUID uuid = UUID.fromString(rs.getString("player_uuid"));
            String ip  = rs.getString("ip_address");
            Instant startTime = rs.getTimestamp("login_at").toInstant();
            Instant endTime = rs.getTimestamp("logout_at").toInstant();
            return new SessionImpl(sessionID, uuid, ip, startTime, endTime);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
