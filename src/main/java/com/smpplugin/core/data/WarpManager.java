package com.smpplugin.core.data;

import com.smpplugin.core.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

/**
 * Persists server-wide warps to {@code warps.yml} in the plugin's data folder.
 * Warp names are case-insensitive and stored lowercase.
 */
public class WarpManager {

    private static final String SECTION = "warps";

    private final JavaPlugin plugin;
    private final File file;
    private YamlConfiguration config;

    public WarpManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "warps.yml");
        load();
    }

    private void load() {
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save warps.yml", e);
        }
    }

    public void setWarp(String name, Location location) {
        ConfigurationSection section = config.createSection(SECTION + "." + name.toLowerCase());
        LocationUtil.write(section, location);
        save();
    }

    public boolean deleteWarp(String name) {
        String key = SECTION + "." + name.toLowerCase();
        if (config.getConfigurationSection(key) == null) {
            return false;
        }
        config.set(key, null);
        save();
        return true;
    }

    public Location getWarp(String name) {
        ConfigurationSection section = config.getConfigurationSection(SECTION + "." + name.toLowerCase());
        if (section == null) {
            return null;
        }
        return LocationUtil.read(section);
    }

    public Set<String> getWarpNames() {
        ConfigurationSection section = config.getConfigurationSection(SECTION);
        if (section == null) {
            return Collections.emptySet();
        }
        return new TreeSet<>(section.getKeys(false));
    }
}
