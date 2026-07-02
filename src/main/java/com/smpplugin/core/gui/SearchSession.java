package com.smpplugin.core.gui;

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
}
