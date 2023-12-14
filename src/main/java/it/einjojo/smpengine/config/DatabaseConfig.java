package it.einjojo.smpengine.config;

import it.einjojo.smpengine.database.DatabaseCredentials;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

@Getter
public class DatabaseConfig extends Config implements DatabaseCredentials {
    private final JavaPlugin plugin;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private int connectionTimeout;

    public DatabaseConfig(JavaPlugin plugin) throws IOException {
        super(plugin.getDataFolder(), "database.yml");
        this.plugin = plugin;
        load();
    }

    @Override
    public void defaults() {
        getConfiguration().addDefault("host", "localhost");
        getConfiguration().addDefault("port", 3306);
        getConfiguration().addDefault("database", "minecraft");
        getConfiguration().addDefault("username", "root");
        getConfiguration().addDefault("password", "password");
        getConfiguration().addDefault("connection-timeout", 5000);
        getConfiguration().options().copyDefaults(true);
    }

    @Override
    public void load() {
        host = getConfiguration().getString("host");
        port = getConfiguration().getInt("port");
        database = getConfiguration().getString("database");
        username = getConfiguration().getString("username");
        password = getConfiguration().getString("password");
        connectionTimeout = getConfiguration().getInt("connection-timeout");
        plugin.getLogger().info("Loaded database config!");
    }

}
