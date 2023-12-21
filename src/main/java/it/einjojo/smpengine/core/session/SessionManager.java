package it.einjojo.smpengine.core.session;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.database.SessionDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SessionManager {

    private final SMPEnginePlugin plugin;
    private final Map<UUID, Session> sessions;
    private final SessionDatabase sessionDatabase;

    public SessionManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.sessionDatabase = new SessionDatabase(plugin.getHikariCP());
        sessions = new HashMap<>();
    }

    private Session getActiveSession(UUID uuid) {
        return sessionDatabase.getActiveSession(uuid.toString());
    }

    private Session getSessionByID(int id) {
        return null;
        // TODO: 12/21/2023  
    }

    /**
     * Gets the session of a player
     *
     * @param uuid The UUID of the player
     * @return The session of the player if it exists or an empty optional if it doesn't
     */
    public Optional<Session> getSession(UUID uuid) {
        Session session = sessions.get(uuid);
        if (session == null) {
            session = getActiveSession(uuid);
            if (session != null) {
                sessions.put(uuid, session);
            }
        }
        return Optional.ofNullable(session);
    }

    public void startSession(SMPPlayer player) {
        Player bukkitPlayer = player.getPlayer();
        if (bukkitPlayer == null) {
            throw new IllegalStateException("Player is not online");
        }

        if (getSession(player.getUuid()).isPresent()) {
            endSession(player);
        }

        String playerIP = bukkitPlayer.getAddress().getAddress().getHostAddress();
        SessionImpl session = new SessionImpl(-1, player.getUuid(), playerIP, Instant.now(), null);
        if (!sessionDatabase.createSession(session)) {
            throw new IllegalStateException("Failed to create session");
        }
    }

    public void endSession(SMPPlayer player) {
        Optional<Session> session = getSession(player.getUuid());
        if (session.isEmpty()) {
            throw new IllegalStateException("Player has no active session");
        }
        SessionImpl sessionImpl = (SessionImpl) session.get();
        sessionImpl.setEndTime(Instant.now());
        sessionDatabase.updateSession(sessionImpl);
        sessions.remove(player.getUuid());
    }

    public void closeSessions() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                endSession(plugin.getPlayerManager().getPlayer(player.getUniqueId()).orElseThrow());
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to close session for " + player.getName() + " (" + player.getUniqueId() + ")");
                e.printStackTrace();
            }
        }
    }

    public void cleanUpBuggySessions() {
        int closed = sessionDatabase.closeUnclosedSessions();
        plugin.getLogger().info("Closed " + closed + " buggy sessions");
    }


}
