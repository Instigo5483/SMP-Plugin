package com.smpplugin.core.gui;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;
import java.util.UUID;

/** Identifies an open paginated item-list inventory: which pool of materials, title, and page. */
public class ItemMenuHolder implements InventoryHolder {

    private final UUID targetUuid;
    private final String targetName;
    private final List<Material> pool;
    private final String title;
    private final int page;
    private Inventory inventory;

    public ItemMenuHolder(UUID targetUuid, String targetName, List<Material> pool, String title, int page) {
        this.targetUuid = targetUuid;
        this.targetName = targetName;
        this.pool = pool;
        this.title = title;
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

    public int getPage() {
        return page;
    }
}
