package it.einjojo.smpengine;

import it.einjojo.smpengine.command.admin.AdminCommand;
import it.einjojo.smpengine.config.DatabaseConfig;
import it.einjojo.smpengine.config.ModuleConfig;
import it.einjojo.smpengine.database.HikariCP;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SMPEnginePlugin extends JavaPlugin {
    private boolean startedSuccessfully = false;
    private DatabaseConfig databaseConfig;
    private ModuleConfig moduleConfig;
    private HikariCP hikariCP;


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

        startedSuccessfully = true;
    }

    @Override
    public void onDisable() {
        if (!startedSuccessfully) {
            return;
        }
    }


    private boolean initClasses() {
        try {
            hikariCP = new HikariCP(databaseConfig);
        } catch (Exception e) {
            getLogger().severe("Failed to initialize HikariCP! \n" + e.getMessage());
            return false;
        }
        return true;
    }

    private void loadCommands() {
        new AdminCommand(this);
    }

    private boolean loadConfig() {
        try {
            databaseConfig = new DatabaseConfig(this);
        } catch (IOException e) {
            getLogger().severe("Failed to load database config!");
            return false;
        }
        return true;
    }

    public ModuleConfig getModuleConfig() {
        return moduleConfig;
    }

    public HikariCP getHikariCP() {
        return hikariCP;
    }
}
