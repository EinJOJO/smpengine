package it.einjojo.smpengine.core.session;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.database.SessionDatabase;
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
                .build(this::getSessionByUUID);


    }

    private Session getSessionByUUID(UUID uuid) {

    }

    public Optional<Session> getSession(UUID uuid) {
        return Optional.ofNullable(sessions.get(uuid));
    }

    public void startSession(SMPPlayer player) {
        Player bukkitPlayer = player.getPlayer();
        if (bukkitPlayer == null) {
            throw new IllegalStateException("Player is not online");
        }
        String playerIP = bukkitPlayer.getAddress().getAddress().getHostAddress();
        SessionImpl session = new SessionImpl(-1, player.getUuid(), playerIP, Instant.now(), null);
        sessionDatabase.createSession(session);
    }

    public void endSession(SMPPlayer player) {
        return;
    }

}
