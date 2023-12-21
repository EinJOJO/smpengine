package it.einjojo.smpengine.database;

import it.einjojo.smpengine.core.stats.StatsImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatsDatabase {

    private final HikariCP hikariCP;

    public StatsDatabase(HikariCP hikariCP) {
        this.hikariCP = hikariCP;
    }

    public boolean createStats(StatsImpl stats) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO stats (session_id, player_uuid, blocksDestroyed, blocksPlaced, mobKills, playerKills, deaths, villagerTrades) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, stats.getSessionID());
                ps.setString(2, stats.getUuid().toString());
                ps.setInt(3, stats.getBlocksDestroyed());
                ps.setInt(4, stats.getBlocksPlaced());
                ps.setInt(5, stats.getMobKills());
                ps.setInt(6, stats.getPlayerKills());
                ps.setInt(7, stats.getDeaths());
                ps.setInt(8, stats.getVillagerTrades());
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        }
    }

    public void updateStats(StatsImpl stats) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("UPDATE stats SET blocksDestroyed = ?, blocksPlaced = ?, mobKills = ?, playerKills = ?, deaths = ?, villagerTrades = ? WHERE session_id = ?")) {
                ps.setInt(1, stats.getBlocksDestroyed());
                ps.setInt(2, stats.getBlocksPlaced());
                ps.setInt(3, stats.getMobKills());
                ps.setInt(4, stats.getPlayerKills());
                ps.setInt(5, stats.getDeaths());
                ps.setInt(6, stats.getVillagerTrades());
                ps.setInt(7, stats.getSessionID());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
