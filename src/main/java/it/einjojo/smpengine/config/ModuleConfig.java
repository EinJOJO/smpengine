package it.einjojo.smpengine.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ModuleConfig extends Config {
    private final JavaPlugin plugin;
    private boolean joinMessages;
    private String joinMessage;
    private boolean quitMessages;
    private String quitMessage;


    public ModuleConfig(JavaPlugin folder) throws IOException {
        super(new File(folder.getDataFolder(), "modules.yml"));
        this.plugin = folder;
        load();
    }

    @Override
    public void defaults() {
        getConfiguration().addDefault("join-messages", true);
        getConfiguration().addDefault("join-message", "&7[&a+&7] &7%player%");
        getConfiguration().addDefault("quit-messages", true);
        getConfiguration().addDefault("quit-message", "&7[&c-&7] &7%player%");
        getConfiguration().options().copyDefaults(true);
        plugin.getLogger().info("Created modules config!");
    }

    @Override
    public void load() {
        joinMessages = getConfiguration().getBoolean("join-messages");
        joinMessage = getConfiguration().getString("join-message");
        quitMessages = getConfiguration().getBoolean("quit-messages");
        quitMessage = getConfiguration().getString("quit-message");
        plugin.getLogger().info("Loaded modules config!");
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public boolean isJoinMessages() {
        return joinMessages;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public boolean isQuitMessages() {
        return quitMessages;
    }

    public String getQuitMessage() {
        return quitMessage;
    }
}
