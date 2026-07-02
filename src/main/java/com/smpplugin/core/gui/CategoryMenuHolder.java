package com.smpplugin.core.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

/** Identifies the /itemgive main menu (category tiles + search) for a given target player. */
public class CategoryMenuHolder implements InventoryHolder {

    private final UUID targetUuid;
    private final String targetName;
    private Inventory inventory;

    public CategoryMenuHolder(UUID targetUuid, String targetName) {
        this.targetUuid = targetUuid;
        this.targetName = targetName;
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
}
