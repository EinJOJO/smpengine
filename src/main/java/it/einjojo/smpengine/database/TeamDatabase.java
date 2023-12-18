package it.einjojo.smpengine.database;

import it.einjojo.smpengine.core.team.Team;
import it.einjojo.smpengine.core.team.TeamImpl;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamDatabase {

    private final HikariCP hikariCP;
    private MiniMessage miniMessage = MiniMessage.miniMessage();

    public TeamDatabase(HikariCP hikariCP) {
        this.hikariCP = hikariCP;
    }

    public Team createTeam(TeamImpl team) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO team (name, displayName, owner_uuid, created_at) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, team.getName());
                ps.setString(2, miniMessage.serialize(team.getDisplayName()));
                ps.setString(3, team.getOwner_uuid().toString());
                ps.setTimestamp(4, Timestamp.from(team.getCreated_at()));
                ps.executeUpdate();
            }
            return team;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Team getTeamByName(String name) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT id, team.name AS team_name, displayName, owner_uuid, created_at, uuid AS member_uuid FROM team INNER JOIN spieler ON spieler.team_id = team.id WHERE team.name = ?")) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    return rsToTeam(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Team getTeam(int id) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT id, team.name AS team_name, displayName, owner_uuid, created_at, uuid AS member_uuid FROM team INNER JOIN spieler ON spieler.team_id = team.id WHERE team.id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rsToTeam(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Team rsToTeam(ResultSet rs) {
        try {
            List<UUID> members = new ArrayList<>();
            while (rs.next()) {
                members.add(UUID.fromString(rs.getString("member_uuid")));
            }
            if (members.isEmpty()) return null;
            rs.last();
            int id = rs.getInt("id");
            String name = rs.getString("team_name");
            UUID owner_uuid = UUID.fromString(rs.getString("owner_uuid"));
            String displayName = rs.getString("displayName");
            Instant created_at = rs.getTimestamp("created_at").toInstant();
            return new TeamImpl(id, name, miniMessage.deserialize(displayName), owner_uuid, created_at, members);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
