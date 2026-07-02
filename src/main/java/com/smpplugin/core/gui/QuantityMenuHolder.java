package com.smpplugin.core.gui;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;
import java.util.UUID;

/** Identifies an open quantity-picker inventory: material, current amount, and which browse view to return to. */
public class QuantityMenuHolder implements InventoryHolder {

    private final UUID targetUuid;
    private final String targetName;
    private final Material material;
    private final List<Material> returnPool;
    private final String returnTitle;
    private final ItemCategory returnCategory;
    private final int returnPage;
    private int amount;
    private Inventory inventory;

    public QuantityMenuHolder(UUID targetUuid, String targetName, Material material, int amount,
                               List<Material> returnPool, String returnTitle, ItemCategory returnCategory,
                               int returnPage) {
        this.targetUuid = targetUuid;
        this.targetName = targetName;
        this.material = material;
        this.amount = amount;
        this.returnPool = returnPool;
        this.returnTitle = returnTitle;
        this.returnCategory = returnCategory;
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

    public List<Material> getReturnPool() {
        return returnPool;
    }

    public String getReturnTitle() {
        return returnTitle;
    }

    public ItemCategory getReturnCategory() {
        return returnCategory;
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
