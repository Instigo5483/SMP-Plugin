package com.smpplugin.core.data;

import com.smpplugin.core.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Persists each player's homes to {@code playerdata/<uuid>.yml}, lazily loaded
 * and cached per player. Home names are case-insensitive and stored lowercase.
 */
public class HomeManager {

    public static final String DEFAULT_HOME_NAME = "home";

    private static final String SECTION = "homes";

    private final JavaPlugin plugin;
    private final File playerDataFolder;
    private final Map<UUID, YamlConfiguration> cache = new HashMap<>();

    public HomeManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerDataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }

    public enum SetResult { CREATED, UPDATED, LIMIT_REACHED }

    private File fileFor(UUID uuid) {
        return new File(playerDataFolder, uuid + ".yml");
    }

    private YamlConfiguration configFor(UUID uuid) {
        return cache.computeIfAbsent(uuid, id -> YamlConfiguration.loadConfiguration(fileFor(id)));
    }

    private void save(UUID uuid) {
        try {
            configFor(uuid).save(fileFor(uuid));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save home data for " + uuid, e);
        }
    }

    /** -1 means unlimited. */
    public int getMaxHomes() {
        return plugin.getConfig().getInt("max-homes", -1);
    }

    public SetResult setHome(UUID uuid, String name, Location location) {
        String key = SECTION + "." + name.toLowerCase();
        YamlConfiguration config = configFor(uuid);
        boolean existed = config.getConfigurationSection(key) != null;

        int max = getMaxHomes();
        if (!existed && max >= 0 && getHomeNames(uuid).size() >= max) {
            return SetResult.LIMIT_REACHED;
        }

        ConfigurationSection section = config.createSection(key);
        LocationUtil.write(section, location);
        save(uuid);
        return existed ? SetResult.UPDATED : SetResult.CREATED;
    }

    public boolean deleteHome(UUID uuid, String name) {
        String key = SECTION + "." + name.toLowerCase();
        YamlConfiguration config = configFor(uuid);
        if (config.getConfigurationSection(key) == null) {
            return false;
        }
        config.set(key, null);
        save(uuid);
        return true;
    }

    public Location getHome(UUID uuid, String name) {
        ConfigurationSection section = configFor(uuid).getConfigurationSection(SECTION + "." + name.toLowerCase());
        if (section == null) {
            return null;
        }
        return LocationUtil.read(section);
    }

    public Set<String> getHomeNames(UUID uuid) {
        ConfigurationSection section = configFor(uuid).getConfigurationSection(SECTION);
        if (section == null) {
            return Collections.emptySet();
        }
        return new TreeSet<>(section.getKeys(false));
    }
}
