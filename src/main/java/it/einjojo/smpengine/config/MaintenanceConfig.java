package it.einjojo.smpengine.config;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

@Getter
public class MaintenanceConfig extends Config {
    private String bypassPermission;
    private String kickMessage;
    private boolean enabled;

    public MaintenanceConfig(JavaPlugin plugin) throws IOException {
        super(plugin.getDataFolder(), "maintenance.yml");
        load();
    }

    @Override
    public void defaults() {
        getConfiguration().addDefault("enabled", false);
        getConfiguration().addDefault("bypass-permission", "smpengine.maintenance.bypass");
        getConfiguration().addDefault("kick-message", "&cThe server is currently in maintenance mode.");
        getConfiguration().options().copyDefaults(true);
    }

    @Override
    public void load() {
        this.enabled = getConfiguration().getBoolean("enabled");
        this.bypassPermission = getConfiguration().getString("bypass-permission");
        this.kickMessage = getConfiguration().getString("kick-message");
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        getConfiguration().set("enabled", enabled);
    }
}
