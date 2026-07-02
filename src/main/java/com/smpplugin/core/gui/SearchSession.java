package com.smpplugin.core.gui;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Tracks an in-progress /itemgive search for one viewer.
 * A force-opened anvil (Player#openAnvil) has no custom InventoryHolder to attach
 * state to, so ItemGiveGuiListener keys these by viewer UUID instead.
 */
public class SearchSession {

    private final UUID targetUuid;
    private final String targetName;
    private String pendingQuery = "";
    private ItemStack placeholder;

    public SearchSession(UUID targetUuid, String targetName) {
        this.targetUuid = targetUuid;
        this.targetName = targetName;
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

    /** The dummy paper placed in the anvil's input slot so the rename box is interactive. */
    public ItemStack getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(ItemStack placeholder) {
        this.placeholder = placeholder;
    }
}
