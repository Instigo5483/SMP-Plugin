package com.smpplugin.core.gui;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

/** Identifies an open quantity-picker inventory: which material, current amount, and where "Back" returns to. */
public class QuantityMenuHolder implements InventoryHolder {

    private final UUID targetUuid;
    private final String targetName;
    private final Material material;
    private final int returnPage;
    private int amount;
    private Inventory inventory;

    public QuantityMenuHolder(UUID targetUuid, String targetName, Material material, int amount, int returnPage) {
        this.targetUuid = targetUuid;
        this.targetName = targetName;
        this.material = material;
        this.amount = amount;
        this.returnPage = returnPage;
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

    public Material getMaterial() {
        return material;
    }

    public int getReturnPage() {
        return returnPage;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
