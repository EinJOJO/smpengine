package it.einjojo.smpengine.core.session;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;

public class SessionManager {

    private final SMPEnginePlugin plugin;

    public SessionManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    public Session startSession(SMPPlayer player) {
        return null;
    }

    public void endSession(SMPPlayer player) {
        return;
    }

}
