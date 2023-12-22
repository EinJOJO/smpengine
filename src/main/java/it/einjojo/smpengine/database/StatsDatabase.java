package it.einjojo.smpengine.database;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.stats.GlobalStats;
import it.einjojo.smpengine.core.stats.Stats;
import it.einjojo.smpengine.core.stats.StatsImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StatsDatabase {

    private final HikariCP hikariCP;

    private final SMPEnginePlugin plugin;

    public StatsDatabase(HikariCP hikariCP, SMPEnginePlugin plugin) {
        this.hikariCP = hikariCP;
        this.plugin = plugin;
    }


    public StatsImpl getBySession(int sessionID) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM stats WHERE session_id = ?")) {
                ps.setInt(1, sessionID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    return rsToStats(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GlobalStats getTeamStats(int id){
        List<StatsImpl> statsList = new ArrayList<>();
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT COUNT(session_id) AS Logins, player_uuid, SUM(blocksDestroyed) AS blocksDestroyed, SUM(blocksPlaced) AS blocksPlaced, SUM(mobKills) AS mobKills, SUM(playerKills) AS playerKills, SUM(deaths) AS deaths, SUM(villagerTrades) AS villagerTrades, SUM(TIMESTAMPDIFF(SECOND, login_at, logout_at)) AS playtime FROM stats INNER JOIN sessions ON session_id = sessions.id WHERE player_uuid IN (SELECT uuid FROM spieler WHERE team_id = " + id + ")")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    Integer logins = rs.getObject("Logins", Integer.class);
                    String pUuid = rs.getString("player_uuid");
                    int bd = rs.getInt("blocksDestroyed");
                    int bp = rs.getInt("blocksPlaced");
                    int mk = rs.getInt("mobKills");
                    int pk = rs.getInt("playerKills");
                    int d = rs.getInt("deaths");
                    int vT = rs.getInt("villagerTrades");
                    int pt = rs.getInt("playtime");
                    Instant playtime = Instant.ofEpochSecond(pt);
                    return new GlobalStats(UUID.fromString(pUuid), bd, bp, mk, pk, d, vT, playtime, logins);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public GlobalStats getGlobalStats(UUID uuid) {
        List<StatsImpl> statsList = new ArrayList<>();
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT COUNT(session_id) AS Logins, player_uuid, SUM(blocksDestroyed) AS blocksDestroyed, SUM(blocksPlaced) AS blocksPlaced, SUM(mobKills) AS mobKills, SUM(playerKills) AS playerKills, SUM(deaths) AS deaths, SUM(villagerTrades) AS villagerTrades, SUM(TIMESTAMPDIFF(SECOND, login_at, logout_at)) AS playtime FROM stats INNER JOIN sessions ON session_id = sessions.id WHERE player_uuid = ?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    Integer logins = rs.getObject("Logins", Integer.class);
                    String pUuid = rs.getString("player_uuid");
                    int bd = rs.getInt("blocksDestroyed");
                    int bp = rs.getInt("blocksPlaced");
                    int mk = rs.getInt("mobKills");
                    int pk = rs.getInt("playerKills");
                    int d = rs.getInt("deaths");
                    int vT = rs.getInt("villagerTrades");
                    int pt = rs.getInt("playtime");
                    Instant playtime = Instant.ofEpochSecond(pt);
                    return new GlobalStats(UUID.fromString(pUuid), bd, bp, mk, pk, d, vT, playtime, logins);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean createStats(int sessionID, UUID uuid) {
        try (Connection connection = hikariCP.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO stats (session_id, player_uuid) VALUES (?, ?)")) {
                ps.setInt(1, sessionID);
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateStats(Stats _stats) {
        StatsImpl stats = (StatsImpl) _stats;
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

    private StatsImpl rsToStats(ResultSet rs) {
        try {
            Integer id = rs.getObject("session_id", Integer.class);
            String uuid = rs.getString("player_uuid");
            int bd = rs.getInt("blocksDestroyed");
            int bp = rs.getInt("blocksPlaced");
            int mk = rs.getInt("mobKills");
            int pk = rs.getInt("playerKills");
            int d = rs.getInt("deaths");
            int vT = rs.getInt("villagerTrades");
            return new StatsImpl(id, UUID.fromString(uuid), bd, bp, mk, pk, d, vT);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
