package cc.seati.SeatiCore;

import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LocalData {
    private File file;
    private YamlConfiguration t;
    private String filename;

    public LocalData(String filename) {
        this.setFilename(filename);
        this.init();
    }

    public void init() {
        file = new File(filename + ".yml");
        try {
            if (file.createNewFile()) {
                Main.LOGGER.info("Generated new configuration file at {}", file.getAbsolutePath());
            } else {
                Main.LOGGER.info("Configuration file already exists. Skipping creation.");
            }
        } catch (IOException e) {
            Main.LOGGER.warn("Could not create new configuration file.");
            e.printStackTrace();
        }
        t = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        this.init();
    }

    public void save() {
        try {
            t.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public YamlConfiguration target() {
        return t;
    }

    public File getFile() {
        return file;
    }
}
