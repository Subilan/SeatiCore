package cc.seati.PlayerStats;

import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    public static File configFile;
    public static YamlConfiguration t;

    public static void init() {
        configFile = new File("playertracer.yml");
        try {
            if (configFile.createNewFile()) {
                Main.LOGGER.info("Generated new configuration file at " + configFile.getAbsolutePath());
            } else {
                Main.LOGGER.info("Configuration file already exists. Skipping creation.");
            }
        } catch (IOException e) {
            Main.LOGGER.warn("Could not create new configuration file.");
            e.printStackTrace();
        }
        t = YamlConfiguration.loadConfiguration(configFile);

        Main.LOGGER.info("Initialized configuration.");
    }
}
