package it.einjojo.smpengine;

import it.einjojo.smpengine.command.DifficultyCommand;
import it.einjojo.smpengine.command.admin.AdminCommand;
import it.einjojo.smpengine.command.team.TeamCommand;
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
import it.einjojo.smpengine.listener.*;
import it.einjojo.smpengine.scoreboard.TablistManager;
import it.einjojo.smpengine.util.MessageUtil;
import it.einjojo.smpengine.util.Placeholder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

public class SMPEnginePlugin extends JavaPlugin {

    private static final Map<String, Component> cachedMessages = new WeakHashMap<>();

    private boolean startedSuccessfully = false;

    private DatabaseConfig databaseConfig;
    private MessagesConfig messagesConfig;

    @Getter
    private boolean shuttingDown = false;

    @Getter
    private ModuleConfig moduleConfig;
    @Getter
    private MaintenanceConfig maintenanceConfig;
    @Getter
    private HikariCP hikariCP;

    @Getter
    private TablistManager tablistManager;

    @Getter
    private SMPPlayerManager playerManager;
    @Getter
    private SessionManager sessionManager;
    @Getter
    private TeamManager teamManager;
    @Getter
    private StatsManager statsManager;

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
        shuttingDown = true;
        statsManager.closeStats();
        sessionManager.closeSessions();
        playerManager.closePlayers();
        hikariCP.close();
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            onlinePlayer.kick(Component.text("Shutdown"));
        }
        clearCache();
        shuttingDown = false;
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
        playerManager.setEveryoneOffline();
        sessionManager = new SessionManager(this);
        sessionManager.cleanUpBuggySessions();
        teamManager = new TeamManager(this);
        statsManager = new StatsManager(this);
        tablistManager = new TablistManager(this);

        return true;
    }

    public void clearCache() {
        cachedMessages.clear();
    }

    private void loadCommands() {
        new AdminCommand(this);
        new TeamCommand(this);
        new DifficultyCommand(this);
    }

    private void loadListener() {
        new PlayerJoinListener(this);
        new DeathListener(this);
        new PlayerQuitListener(this);
        new TeamListener(this);
        new PlayerChatListener(this);
        new StatsListener(this);
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


    public Component getMessage(MessageUtil.KEY key) {
        return getMessage(key.getKey());
    }

    public Component getMessage(String key, Placeholder... placeholders) {
        return Placeholder.applyPlaceholders(getMessage(key), placeholders);
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

    @Override
    public @NotNull String toString() {
        return "SMPEnginePlugin{" +
                "startedSuccessfully=" + startedSuccessfully +
                '}';
    }

    public void syncKick(Player player, Component message) {
        getServer().getScheduler().runTask(this, () -> player.kick(message));
    }

    public Component getPrefix() {
        return MessageUtil.format(messagesConfig.get("prefix"), getPrimaryColor());
    }
}
