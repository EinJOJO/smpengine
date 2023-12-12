package it.einjojo.smpengine.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        for (Map.Entry<String, Object> entry : getConfiguration().getValues(false).entrySet()) {
            messages.put(entry.getKey(), entry.getValue().toString());
        }
        plugin.getLogger().info("Loaded " + messages.size() + "  messages from config!");
    }

    public String get(String key) {
        return messages.get(key);
    }

}
