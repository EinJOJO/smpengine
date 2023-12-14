package it.einjojo.smpengine.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessagesConfig extends Config {
    private final Map<String, String> messages = new HashMap<>();
    private final JavaPlugin plugin;

    public MessagesConfig(JavaPlugin folder) throws IOException {
        super(folder.getDataFolder(), "messages.yml");
        this.plugin = folder;
        load();
    }


    @Override
    public void load() {
        loadMessages(getConfiguration(), "");
        plugin.getLogger().info("Loaded " + messages.size() + "  messages from config!");
    }

    private void loadMessages(ConfigurationSection section, String prefix) {
        for (String key : section.getKeys(false)) {
            if (section.isConfigurationSection(key)) {
                loadMessages(Objects.requireNonNull(section.getConfigurationSection(key)), prefix + key + ".");
            } else {
                messages.put(prefix + key, section.getString(key));
            }
        }
    }

    public String get(String key) {
        return messages.get(key);
    }

}
