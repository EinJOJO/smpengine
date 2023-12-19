package it.einjojo.smpengine.config;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Getter
public abstract class Config {
    private final File file;
    private final YamlConfiguration configuration;
    private final String fileName;

    public Config(File folder, String fileName) throws IOException {
        this.fileName = fileName;
        this.file = new File(folder, fileName);
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

    public void save() throws IOException {
        configuration.save(file);
    }

    public void defaults() {
        // load from resources
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);
            if (stream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
                getConfiguration().addDefaults(defaultConfig);
                getConfiguration().options().copyDefaults(true);
                stream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void load();

}
