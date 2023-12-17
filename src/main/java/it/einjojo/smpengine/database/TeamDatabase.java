package it.einjojo.smpengine.database;

import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.core.team.TeamImpl;

import java.sql.*;

public class TeamDatabase {

    private final HikariCP hikariCP;

    public TeamDatabase(HikariCP hikariCP) {
        this.hikariCP = hikariCP;
    }

    public Team createTeam(TeamImpl team) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO team (name, displayName, owner_uuid, created_at) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, team.getName());
                ps.setString(2, team.getDisplayName());
                ps.setString(3, team.getOwner_uuid());
                ps.setTimestamp(4, Timestamp.from(team.getCreated_at()));
                ps.executeUpdate();
            }
            return team;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Team getTeam(int id) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT id, team.name AS team_name, displayName, owner_uuid, created_at, uuid AS member_uuid FROM team INNER JOIN spieler ON spieler.team_id = team.id")) {
                try (ResultSet rs =  ps.executeQuery()) {
                    while (rs.next()) {

                    }
                }
            }
            return team;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
