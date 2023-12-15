package it.einjojo.smpengine;

import it.einjojo.smpengine.command.admin.AdminCommand;
import it.einjojo.smpengine.config.DatabaseConfig;
import it.einjojo.smpengine.config.MaintenanceConfig;
import it.einjojo.smpengine.config.MessagesConfig;
import it.einjojo.smpengine.config.ModuleConfig;
import it.einjojo.smpengine.core.player.SMPPlayerManager;
import it.einjojo.smpengine.core.session.SessionManager;
import it.einjojo.smpengine.core.stats.StatsManager;
import it.einjojo.smpengine.core.team.TeamManager;
import it.einjojo.smpengine.database.DatabaseMigrator;
import it.einjojo.smpengine.database.HikariCP;
import it.einjojo.smpengine.listener.JoinListener;
import it.einjojo.smpengine.listener.PlayerQuitListener;
import it.einjojo.smpengine.util.MessageUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

public class SMPEnginePlugin extends JavaPlugin {

    private static final Map<String, Component> cachedMessages = new WeakHashMap<>();

    private boolean startedSuccessfully = false;
    private DatabaseConfig databaseConfig;
    private MessagesConfig messagesConfig;

    @Getter
    private ModuleConfig moduleConfig;
    @Getter
    private MaintenanceConfig maintenanceConfig;
    @Getter
    private HikariCP hikariCP;

    @Getter
    SMPPlayerManager playerManager;
    @Getter
    SessionManager sessionManager;
    @Getter
    TeamManager teamManager;
    @Getter
    StatsManager manager;

    @Override
    public void onEnable() {
        if (!loadConfig()) {
            getLogger().severe("Disabling Plugin because of failed config loading!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!initClasses()) {
            getLogger().severe("Disabling Plugin because of failed class initialization!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        loadCommands();
        loadListener();
        startedSuccessfully = true;
    }

    @Override
    public void onDisable() {
        if (!startedSuccessfully) {
            return;
        }
        hikariCP.close();
        clearCache();
    }


    /**
     * Initializes all classes (2nd step of onEnable)
     */
    private boolean initClasses() {
        // Create database connection pool
        try {
            hikariCP = new HikariCP(databaseConfig);
            DatabaseMigrator databaseMigrator = new DatabaseMigrator(hikariCP.getDataSource());
            databaseMigrator.migrate();
        } catch (Exception e) {
            getLogger().severe("Failed to initialize Database!");
            getLogger().severe(e.getMessage());
            return false;
        }

        playerManager = new SMPPlayerManager(this);
        sessionManager = new SessionManager(this);
        teamManager = new TeamManager(this);
        manager = new StatsManager(this);

        return true;
    }

    public void clearCache() {
        cachedMessages.clear();
    }

    private void loadCommands() {
        new AdminCommand(this);
    }

    private void loadListener() {
        new JoinListener(this);
        new PlayerQuitListener(this);
    }


    /**
     * Loads all configs (1st step of onEnable)
     */
    private boolean loadConfig() {
        try {
            databaseConfig = new DatabaseConfig(this);
            moduleConfig = new ModuleConfig(this);
            maintenanceConfig = new MaintenanceConfig(this);
            messagesConfig = new MessagesConfig(this);
        } catch (IOException e) {
            getLogger().severe("Failed to load config! \n" + e.getMessage());
            return false;
        }
        return true;
    }


    public Component getMessage(String key) {
        Component cachedMessage = cachedMessages.get(key);
        if (cachedMessage != null) {
            return cachedMessage;
        }
        String toBeConverted = messagesConfig.get(key);
        if (toBeConverted == null) {
            getLogger().warning("Failed to get message with key '" + key + "'!");
            return Component.text(key);
        }
        Component converted = MessageUtil.format(toBeConverted, getPrimaryColor(), getPrefix());
        cachedMessages.put(key, converted);
        return converted;
    }

    public void reloadConfigs() {
        databaseConfig.load();
        moduleConfig.load();
        maintenanceConfig.load();
        messagesConfig.load();
        cachedMessages.clear();
    }


    public TextColor getPrimaryColor() {
        return moduleConfig.getColor();
    }

    public Component getPrefix() {
        return MessageUtil.format(messagesConfig.get("prefix"), getPrimaryColor());
    }
}
