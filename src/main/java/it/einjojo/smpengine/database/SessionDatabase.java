package it.einjojo.smpengine.database;

import it.einjojo.smpengine.core.session.Session;
import it.einjojo.smpengine.core.session.SessionImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SessionDatabase {

    private final HikariCP hikariCP;

    public SessionDatabase(HikariCP hikariCP) {
        this.hikariCP = hikariCP;
    }

    /**
     * Creates a session in the database
     * @param session The session to be saved
     * @return The session that was saved in the database or null if an error occurred
     */
    public Session createSession(SessionImpl session) {
        try {
            try (Connection connection = hikariCP.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement("INSERT INTO sessions (session_owner_uuid, ip_address, login_at ,logout_at) VALUES (?, ?, ?, ?)")) {
                    ps.setString(1, session.getUuid().toString());
                    ps.setString(2, session.getIp());
                    ps.setTimestamp(3, Timestamp.from(session.getStartTime()));
                    ps.setTimestamp(4, Timestamp.from(session.getEndTime()));
                    ps.executeUpdate();
                }
                return session;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
