package com.smpplugin.core.gui;

import com.smpplugin.core.commands.ItemGiveCommand;
import com.smpplugin.core.util.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemGiveGuiListener implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (isOurs(event.getView().getTopInventory().getHolder())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!(event.getInventory().getHolder() instanceof SearchMenuHolder searchHolder)) {
            return;
        }
        event.getView().setRepairCost(0);

        String text = event.getView().getRenameText();
        searchHolder.setPendingQuery(text == null ? "" : text);
        boolean hasQuery = text != null && !text.isBlank();

        ItemStack result = new ItemStack(Material.COMPASS);
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            String label = hasQuery ? "Search: " + text : "Type a search term";
            meta.displayName(Component.text(label, NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            if (hasQuery) {
                meta.lore(List.of(Component.text("Click to search", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)));
            }
            result.setItemMeta(meta);
        }
        event.setResult(result);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!isOurs(holder)) {
            return;
        }
        event.setCancelled(true);

        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player viewer)) {
            return;
        }

        if (holder instanceof CategoryMenuHolder categoryHolder) {
            handleCategoryMenuClick(viewer, categoryHolder, event.getSlot());
        } else if (holder instanceof ItemMenuHolder menuHolder) {
            handleItemMenuClick(viewer, menuHolder, event.getSlot());
        } else if (holder instanceof QuantityMenuHolder quantityHolder) {
            handleQuantityMenuClick(viewer, quantityHolder, event.getSlot());
        } else if (holder instanceof SearchMenuHolder searchHolder) {
            handleSearchClick(viewer, searchHolder, event.getSlot());
        }
    }

    private boolean isOurs(InventoryHolder holder) {
        return holder instanceof CategoryMenuHolder
                || holder instanceof ItemMenuHolder
                || holder instanceof QuantityMenuHolder
                || holder instanceof SearchMenuHolder;
    }

    private void handleCategoryMenuClick(Player viewer, CategoryMenuHolder holder, int slot) {
        if (slot == ItemGiveMenu.CATEGORY_CLOSE_SLOT) {
            viewer.closeInventory();
            return;
        }
        if (slot == ItemGiveMenu.CATEGORY_SEARCH_SLOT) {
            ItemGiveMenu.openSearch(viewer, holder.getTargetUuid(), holder.getTargetName());
            return;
        }
        ItemCategory[] categories = ItemCategory.values();
        int[] categorySlots = ItemGiveMenu.CATEGORY_SLOTS;
        for (int i = 0; i < categorySlots.length && i < categories.length; i++) {
            if (categorySlots[i] == slot) {
                ItemCategory category = categories[i];
                List<Material> pool = ItemGiveMenu.materialsInCategory(category);
                ItemGiveMenu.openItemList(viewer, holder.getTargetUuid(), holder.getTargetName(), pool, category.label(), 0);
                return;
            }
        }
    }

    private void handleItemMenuClick(Player viewer, ItemMenuHolder holder, int slot) {
        if (slot == ItemGiveMenu.CLOSE_SLOT) {
            viewer.closeInventory();
            return;
        }
        if (slot == ItemGiveMenu.BACK_SLOT) {
            ItemGiveMenu.openCategoryMenu(viewer, holder.getTargetUuid(), holder.getTargetName());
            return;
        }
        if (slot == ItemGiveMenu.PREV_SLOT) {
            ItemGiveMenu.openItemList(viewer, holder.getTargetUuid(), holder.getTargetName(),
                    holder.getPool(), holder.getTitle(), holder.getPage() - 1);
            return;
        }
        if (slot == ItemGiveMenu.NEXT_SLOT) {
            ItemGiveMenu.openItemList(viewer, holder.getTargetUuid(), holder.getTargetName(),
                    holder.getPool(), holder.getTitle(), holder.getPage() + 1);
            return;
        }
        if (slot < 0 || slot >= ItemGiveMenu.ITEMS_PER_PAGE) {
            return;
        }
        int index = holder.getPage() * ItemGiveMenu.ITEMS_PER_PAGE + slot;
        List<Material> pool = holder.getPool();
        if (index >= pool.size()) {
            return;
        }
        Material material = pool.get(index);
        ItemGiveMenu.openQuantityPicker(viewer, holder.getTargetUuid(), holder.getTargetName(), material, 1,
                pool, holder.getTitle(), holder.getPage());
    }

    private void handleQuantityMenuClick(Player viewer, QuantityMenuHolder holder, int slot) {
        if (slot == ItemGiveMenu.QTY_BACK) {
            ItemGiveMenu.openItemList(viewer, holder.getTargetUuid(), holder.getTargetName(),
                    holder.getReturnPool(), holder.getReturnTitle(), holder.getReturnPage());
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
            ItemGiveMenu.openItemList(viewer, holder.getTargetUuid(), holder.getTargetName(),
                    holder.getReturnPool(), holder.getReturnTitle(), holder.getReturnPage());
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

    private void handleSearchClick(Player viewer, SearchMenuHolder holder, int slot) {
        if (slot != ItemGiveMenu.SEARCH_RESULT_SLOT) {
            return;
        }
        String query = holder.getPendingQuery();
        if (query == null || query.isBlank()) {
            Messages.error(viewer, "Type something to search for first.");
            return;
        }
        ItemGiveMenu.openSearchResults(viewer, holder.getTargetUuid(), holder.getTargetName(), query);
    }
}
