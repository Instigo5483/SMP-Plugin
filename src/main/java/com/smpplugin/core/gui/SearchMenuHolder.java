package com.smpplugin.core.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

/** Identifies the anvil-based search box; tracks the currently typed rename text. */
public class SearchMenuHolder implements InventoryHolder {

    private final UUID targetUuid;
    private final String targetName;
    private String pendingQuery = "";
    private Inventory inventory;

    public SearchMenuHolder(UUID targetUuid, String targetName) {
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

    public String getPendingQuery() {
        return pendingQuery;
    }

    public void setPendingQuery(String pendingQuery) {
        this.pendingQuery = pendingQuery;
    }
}
