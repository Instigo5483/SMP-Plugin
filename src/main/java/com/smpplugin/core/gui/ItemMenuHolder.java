package com.smpplugin.core.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

/** Identifies an open paginated item-picker inventory and which page/target it belongs to. */
public class ItemMenuHolder implements InventoryHolder {

    private final UUID targetUuid;
    private final String targetName;
    private final int page;
    private Inventory inventory;

    public ItemMenuHolder(UUID targetUuid, String targetName, int page) {
        this.targetUuid = targetUuid;
        this.targetName = targetName;
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

    public int getPage() {
        return page;
    }
}
