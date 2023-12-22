package it.einjojo.smpengine.database;

import it.einjojo.smpengine.core.session.Session;
import it.einjojo.smpengine.core.session.SessionImpl;

import java.sql.*;
import java.time.Instant;
import java.util.UUID;

public class SessionDatabase {

    private final HikariCP hikariCP;

    public SessionDatabase(HikariCP hikariCP) {
        this.hikariCP = hikariCP;
    }


    //PLAYTIME RANKING SQL
    //SELECT SUM(TIMESTAMPDIFF(SECOND, login_at, logout_at)) AS seconds, session_owner_uuid as player FROM sessions GROUP BY player ORDER BY minuten DESC;

    public Session getActiveSession(String uuid) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM sessions WHERE session_owner_uuid = ? AND logout_at IS NULL")) {
                ps.setString(1, uuid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    return rsToSession(rs);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            try (PreparedStatement ps = connection.prepareStatement("UPDATE sessions SET logout_at = ? WHERE id = ?")) {
                ps.setTimestamp(1, Timestamp.from(session.getEndTime()));
                ps.setInt(2, session.getSessionId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int closeUnclosedSessions() {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("UPDATE sessions SET logout_at = ? WHERE logout_at IS NULL")) {
                ps.setTimestamp(1, Timestamp.from(Instant.now()));
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Session rsToSession(ResultSet rs) {
        try {
            Integer sessionID = rs.getObject("id", Integer.class);
            UUID uuid = UUID.fromString(rs.getString("session_owner_uuid"));
            String ip = rs.getString("ip_address");
            Instant startTime = rs.getTimestamp("login_at").toInstant();
            Timestamp logoutAt = rs.getTimestamp("logout_at");
            Instant endTime = null;
            if (logoutAt != null) {
                endTime = logoutAt.toInstant();
            }
            return new SessionImpl(sessionID, uuid, ip, startTime, endTime);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
