package it.einjojo.smpengine;

import it.einjojo.smpengine.command.admin.AdminCommand;
import it.einjojo.smpengine.config.DatabaseConfig;
import it.einjojo.smpengine.config.MaintenanceConfig;
import it.einjojo.smpengine.config.MessagesConfig;
import it.einjojo.smpengine.config.ModuleConfig;
import it.einjojo.smpengine.database.HikariCP;
import it.einjojo.smpengine.util.PlaceholderValue;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

public class SMPEnginePlugin extends JavaPlugin {

    private final Map<String, Component> cachedMessages = new WeakHashMap<>();

    private boolean startedSuccessfully = false;
    private DatabaseConfig databaseConfig;
    private MessagesConfig messagesConfig;

    @Getter
    private MiniMessage miniMessage;
    @Getter
    private ModuleConfig moduleConfig;
    @Getter
    private MaintenanceConfig maintenanceConfig;
    @Getter
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
        loadCommands();
        loadListener();
        startedSuccessfully = true;
    }

    @Override
    public void onDisable() {
        if (!startedSuccessfully) {
            return;
        }
        saveConfigs();
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
        } catch (Exception e) {
            getLogger().severe("Failed to initialize HikariCP! \n" + e.getMessage());
            return false;
        }
        // Initialize MiniMessage
        System.out.println(moduleConfig.getColor());
        miniMessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(Placeholder.component("prefix", getPrefix()))
                        .resolver(Placeholder.styling("pc", moduleConfig.getColor()))
                        .build())
                .build();

        return true;
    }

    public void clearCache() {
        cachedMessages.clear();
        cachedPrefix = null;
    }

    private void loadCommands() {
        new AdminCommand(this);
    }

    private void loadListener() {

    }

    public void saveConfigs() {
        try {
            maintenanceConfig.save();
        } catch (IOException e) {
            getLogger().severe("Failed to save maintenance config! \n" + e.getMessage());
        }
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
        Component legacy = LegacyComponentSerializer.legacy('&').deserialize(messagesConfig.get(key));
        String toBeConverted = MiniMessage.miniMessage().serialize(legacy);
        Component converted = getMiniMessage().deserialize(toBeConverted);
        cachedMessages.put(key, converted);
        return converted;
    }

    public Component applyPlaceholders(Component component, PlaceholderValue... placeholders) {
        TextReplacementConfig.Builder configBuilder = TextReplacementConfig.builder();
        for (var placeholder : placeholders) {
            configBuilder.matchLiteral("{" + placeholder.getKey() + "}")
                    .replacement(placeholder.getValue());
        }

        return component.replaceText(configBuilder.build());
    }

    private Component cachedPrefix;

    public TextColor getPrimaryColor() {
        return moduleConfig.getColor();
    }

    public Component getPrefix() {
        if (cachedPrefix != null) {
            return cachedPrefix;
        }
        cachedPrefix = MiniMessage.miniMessage().deserialize(messagesConfig.get("prefix"));
        return cachedPrefix;
    }
}
