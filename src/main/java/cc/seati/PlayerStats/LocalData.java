package cc.seati.PlayerStats;

import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LocalData {
    public File dist;
    public YamlConfiguration t;
    public String filename;

    public LocalData(String filename) {
        this.filename = filename;
        this.init();
    }

    public void init() {
        dist = new File(filename + ".yml");
        try {
            if (dist.createNewFile()) {
                Main.LOGGER.info("Generated new configuration file at " + dist.getAbsolutePath());
            } else {
                Main.LOGGER.info("Configuration file already exists. Skipping creation.");
            }
        } catch (IOException e) {
            Main.LOGGER.warn("Could not create new configuration file.");
            e.printStackTrace();
        }
        t = YamlConfiguration.loadConfiguration(dist);
        Main.LOGGER.info("Initialized " + filename + ".yml");
    }

    public void reload() {
        this.init();
        Main.LOGGER.info("Reloaded " + filename + ".yml");
    }
}
