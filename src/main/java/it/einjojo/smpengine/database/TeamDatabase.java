package it.einjojo.smpengine.database;

import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.core.team.TeamImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TeamDatabase {

    private final HikariCP hikariCP;

    public TeamDatabase(HikariCP hikariCP) {
        this.hikariCP = hikariCP;
    }

    public Team createTeam(TeamImpl team){
        try {
            try (Connection connection = hikariCP.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement("INSERT INTO team (name, displayName, owner_uuid, created_at) VALUES (?, ?, ?, ?)")) {
                    ps.setString(1, team.getName());
                    ps.setString(2, team.getDisplayName());
                    ps.setString(3, team.getOwner_uuid());
                    ps.setTimestamp(4, Timestamp.from(team.getCreated_at()));
                    ps.executeUpdate();
                }
                return team;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
