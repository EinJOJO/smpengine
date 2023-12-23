package it.einjojo.smpengine.task;

import it.einjojo.smpengine.SMPEnginePlugin;

public class TablistUpdater implements Runnable {

    private final SMPEnginePlugin plugin;

    public TablistUpdater(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getServer().getOnlinePlayers().forEach(plugin.getTablistManager()::update);
    }

    public void start() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 0, 20 * 30);
    }
}
