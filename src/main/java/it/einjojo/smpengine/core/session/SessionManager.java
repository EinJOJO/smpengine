package it.einjojo.smpengine.core.session;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.database.SessionDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class SessionManager {

    private final SMPEnginePlugin plugin;
    private final LoadingCache<UUID, Session> sessions;
    private final SessionDatabase sessionDatabase;

    public SessionManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.sessionDatabase = new SessionDatabase(plugin.getHikariCP());
        sessions = Caffeine.newBuilder()
                .build(uuid -> {
                    var session = sessionDatabase.getActiveSession(uuid.toString());
                    if (session != null) {
                        applyPlugin(session);
                    }
                    return session;
                });
    }


    public Optional<Session> getSessionByID(int id) {
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
        return Optional.ofNullable(sessions.get(uuid));
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
        sessions.invalidate(player.getUuid());
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

    private void applyPlugin(Session s) {
        if (s instanceof SessionImpl) {
            ((SessionImpl) s).setPlugin(plugin);
        }
    }

}
