package com.smpplugin.core.gui;

import com.smpplugin.core.commands.ItemGiveCommand;
import com.smpplugin.core.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

public class ItemGiveGuiListener implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (holder instanceof ItemMenuHolder || holder instanceof QuantityMenuHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof ItemMenuHolder) && !(holder instanceof QuantityMenuHolder)) {
            return;
        }
        event.setCancelled(true);

        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player viewer)) {
            return;
        }

        if (holder instanceof ItemMenuHolder menuHolder) {
            handleItemMenuClick(viewer, menuHolder, event.getSlot());
        } else {
            handleQuantityMenuClick(viewer, (QuantityMenuHolder) holder, event.getSlot());
        }
    }

    private void handleItemMenuClick(Player viewer, ItemMenuHolder holder, int slot) {
        if (slot == ItemGiveMenu.CLOSE_SLOT) {
            viewer.closeInventory();
            return;
        }
        if (slot == ItemGiveMenu.PREV_SLOT) {
            ItemGiveMenu.openItemList(viewer, holder.getTargetUuid(), holder.getTargetName(), holder.getPage() - 1);
            return;
        }
        if (slot == ItemGiveMenu.NEXT_SLOT) {
            ItemGiveMenu.openItemList(viewer, holder.getTargetUuid(), holder.getTargetName(), holder.getPage() + 1);
            return;
        }
        if (slot < 0 || slot >= ItemGiveMenu.ITEMS_PER_PAGE) {
            return;
        }
        int index = holder.getPage() * ItemGiveMenu.ITEMS_PER_PAGE + slot;
        if (index >= ItemGiveMenu.materials().size()) {
            return;
        }
        Material material = ItemGiveMenu.materials().get(index);
        ItemGiveMenu.openQuantityPicker(viewer, holder.getTargetUuid(), holder.getTargetName(), material, 1, holder.getPage());
    }

    private void handleQuantityMenuClick(Player viewer, QuantityMenuHolder holder, int slot) {
        if (slot == ItemGiveMenu.QTY_BACK) {
            ItemGiveMenu.openItemList(viewer, holder.getTargetUuid(), holder.getTargetName(), holder.getReturnPage());
            return;
        }
        if (slot == ItemGiveMenu.QTY_GIVE) {
            Player target = Bukkit.getPlayer(holder.getTargetUuid());
            if (target == null || !target.isOnline()) {
                Messages.error(viewer, holder.getTargetName() + " is no longer online.");
                viewer.closeInventory();
                return;
            }
            ItemGiveCommand.giveItem(viewer, target, holder.getMaterial(), holder.getAmount());
            ItemGiveMenu.openItemList(viewer, holder.getTargetUuid(), holder.getTargetName(), holder.getReturnPage());
            return;
        }
        int adjust = ItemGiveMenu.amountAdjustFor(slot);
        if (adjust == 0) {
            return;
        }
        int newAmount = Math.max(1, Math.min(holder.getAmount() + adjust, ItemGiveCommand.MAX_AMOUNT));
        holder.setAmount(newAmount);
        ItemGiveMenu.renderPreview(holder.getInventory(), holder.getMaterial(), newAmount);
    }
}
