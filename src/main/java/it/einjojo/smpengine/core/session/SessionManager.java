package it.einjojo.smpengine.core.session;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.database.SessionDatabase;
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

    private Session getLatestSession(UUID uuid) {
        return null;
    }

    private Session getSessionByID(int id) {
        return null;
    }

    public Optional<Session> getSession(UUID uuid) {
        Session session = sessions.get(uuid);
        if (session == null) {
            session = getLatestSession(uuid);
        }
        return Optional.ofNullable(session);
    }

    public void startSession(SMPPlayer player) {
        Player bukkitPlayer = player.getPlayer();
        if (bukkitPlayer == null) {
            throw new IllegalStateException("Player is not online");
        }
        String playerIP = bukkitPlayer.getAddress().getAddress().getHostAddress();
        SessionImpl session = new SessionImpl(-1, player.getUuid(), playerIP, Instant.now(), null);
        if (sessionDatabase.createSession(session)) {
            sessions.put(player.getUuid(), session);

        } else {
            throw new IllegalStateException("Failed to create session");
        }
        ;
    }

    public void endSession(SMPPlayer player) {
        Optional<Session> session = getSession(player.getUuid());
        if (session.isEmpty()) {
            return;
        }
        SessionImpl sessionImpl = (SessionImpl) session.get();
        sessionImpl.setEndTime(Instant.now());
        sessionDatabase.updateSession(sessionImpl);

    }

    public void closeSessions() {

    }

}
