package it.einjojo.smpengine.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class Config {
    private final File file;
    private final YamlConfiguration configuration;

    public Config(File file) throws IOException {
        this.file = file;
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    throw new IOException("Unable to create directory " + file.getParentFile().getName());
                }
            }
            if (!file.createNewFile()) {
                throw new IOException("Unable to create file " + file.getName());
            }
        }
        this.configuration = YamlConfiguration.loadConfiguration(file);
        defaults();
        save();
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public void save() throws IOException {
        configuration.save(file);
    }

    public abstract void defaults();

    public abstract void load();

}
