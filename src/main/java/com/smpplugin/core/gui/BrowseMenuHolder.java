package com.smpplugin.core.gui;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;
import java.util.UUID;

/**
 * Identifies the /itemgive browse screen: a persistent row of category tabs (like
 * creative mode) plus a paginated grid below for the active category or search result.
 * activeCategory is null when the pool being shown is a search result rather than a category.
 */
public class BrowseMenuHolder implements InventoryHolder {

    private final UUID targetUuid;
    private final String targetName;
    private final List<Material> pool;
    private final String title;
    private final ItemCategory activeCategory;
    private final int page;
    private Inventory inventory;

    public BrowseMenuHolder(UUID targetUuid, String targetName, List<Material> pool, String title,
                             ItemCategory activeCategory, int page) {
        this.targetUuid = targetUuid;
        this.targetName = targetName;
        this.pool = pool;
        this.title = title;
        this.activeCategory = activeCategory;
        this.page = page;
    }

    void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public UUID getTargetUuid() {
        return targetUuid;
    }

    public String getTargetName() {
        return targetName;
    }

    public List<Material> getPool() {
        return pool;
    }

    public String getTitle() {
        return title;
    }

    public ItemCategory getActiveCategory() {
        return activeCategory;
    }

    public int getPage() {
        return page;
    }
}
