package it.einjojo.smpengine.config;

import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

@Getter
public class ModuleConfig extends Config {
    static final TextColor DEFAULT_COLOR = TextColor.color(213, 91, 218);
    private TextColor color;
    private boolean joinMessages;
    private String joinMessage;
    private boolean quitMessages;
    private String quitMessage;


    public ModuleConfig(JavaPlugin folder) throws IOException {
        super(folder.getDataFolder(), "modules.yml");
        load();
    }

    @Override
    public void defaults() {
        getConfiguration().addDefault("primaryColor", DEFAULT_COLOR.asHexString());
        getConfiguration().addDefault("join-messages", true);
        getConfiguration().addDefault("quit-messages", true);
        getConfiguration().options().copyDefaults(true);
    }

    @Override
    public void load() {
        String hexString = getConfiguration().getString("primaryColor");
        color = TextColor.fromCSSHexString(hexString == null ? DEFAULT_COLOR.asHexString() : hexString);
        if (color == null) {
            color = DEFAULT_COLOR;
        }
        joinMessages = getConfiguration().getBoolean("join-messages");
        joinMessage = getConfiguration().getString("join-message");
        quitMessages = getConfiguration().getBoolean("quit-messages");
        quitMessage = getConfiguration().getString("quit-message");
    }

}
