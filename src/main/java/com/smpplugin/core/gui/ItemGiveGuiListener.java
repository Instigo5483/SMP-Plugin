package com.smpplugin.core.gui;

import com.smpplugin.core.commands.ItemGiveCommand;
import com.smpplugin.core.util.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemGiveGuiListener implements Listener {

    /** Keyed by viewer UUID: a force-opened anvil has no custom InventoryHolder to attach state to. */
    private static final Map<UUID, SearchSession> ACTIVE_SEARCHES = new HashMap<>();

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (isOurs(event.getView().getTopInventory().getHolder()) || ACTIVE_SEARCHES.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            ACTIVE_SEARCHES.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        HumanEntity player = event.getView().getPlayer();
        SearchSession session = ACTIVE_SEARCHES.get(player.getUniqueId());
        if (session == null) {
            return;
        }

        String text = event.getView().getRenameText();
        session.setPendingQuery(text == null ? "" : text);
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
        if (!(event.getWhoClicked() instanceof Player viewer)) {
            return;
        }

        SearchSession searchSession = ACTIVE_SEARCHES.get(viewer.getUniqueId());
        boolean isSearchAnvil = searchSession != null
                && event.getView().getTopInventory().getType() == InventoryType.ANVIL;
        InventoryHolder holder = event.getView().getTopInventory().getHolder();

        if (!isSearchAnvil && !isOurs(holder)) {
            return;
        }
        event.setCancelled(true);

        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        if (isSearchAnvil) {
            handleSearchClick(viewer, searchSession, event.getSlot());
        } else if (holder instanceof BrowseMenuHolder browseHolder) {
            handleBrowseClick(viewer, browseHolder, event.getSlot());
        } else if (holder instanceof QuantityMenuHolder quantityHolder) {
            handleQuantityMenuClick(viewer, quantityHolder, event.getSlot());
        }
    }

    private boolean isOurs(InventoryHolder holder) {
        return holder instanceof BrowseMenuHolder || holder instanceof QuantityMenuHolder;
    }

    private void handleBrowseClick(Player viewer, BrowseMenuHolder holder, int slot) {
        if (slot == ItemGiveMenu.CLOSE_SLOT) {
            viewer.closeInventory();
            return;
        }
        if (slot == ItemGiveMenu.SEARCH_SLOT) {
            SearchSession session = new SearchSession(holder.getTargetUuid(), holder.getTargetName());
            if (ItemGiveMenu.openSearch(viewer)) {
                ACTIVE_SEARCHES.put(viewer.getUniqueId(), session);
            } else {
                Messages.error(viewer, "Could not open the search box.");
            }
            return;
        }
        if (slot == ItemGiveMenu.PREV_SLOT) {
            ItemGiveMenu.openBrowse(viewer, holder.getTargetUuid(), holder.getTargetName(),
                    holder.getPool(), holder.getTitle(), holder.getActiveCategory(), holder.getPage() - 1);
            return;
        }
        if (slot == ItemGiveMenu.NEXT_SLOT) {
            ItemGiveMenu.openBrowse(viewer, holder.getTargetUuid(), holder.getTargetName(),
                    holder.getPool(), holder.getTitle(), holder.getActiveCategory(), holder.getPage() + 1);
            return;
        }
        ItemCategory[] categories = ItemCategory.values();
        if (slot >= 0 && slot < categories.length) {
            ItemGiveMenu.openCategory(viewer, holder.getTargetUuid(), holder.getTargetName(), categories[slot], 0);
            return;
        }
        if (slot < ItemGiveMenu.GRID_START_SLOT || slot >= ItemGiveMenu.GRID_START_SLOT + ItemGiveMenu.ITEMS_PER_PAGE) {
            return;
        }
        int index = holder.getPage() * ItemGiveMenu.ITEMS_PER_PAGE + (slot - ItemGiveMenu.GRID_START_SLOT);
        List<Material> pool = holder.getPool();
        if (index >= pool.size()) {
            return;
        }
        Material material = pool.get(index);
        ItemGiveMenu.openQuantityPicker(viewer, holder.getTargetUuid(), holder.getTargetName(), material, 1,
                pool, holder.getTitle(), holder.getActiveCategory(), holder.getPage());
    }

    private void handleQuantityMenuClick(Player viewer, QuantityMenuHolder holder, int slot) {
        if (slot == ItemGiveMenu.QTY_BACK) {
            ItemGiveMenu.openBrowse(viewer, holder.getTargetUuid(), holder.getTargetName(),
                    holder.getReturnPool(), holder.getReturnTitle(), holder.getReturnCategory(), holder.getReturnPage());
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
            ItemGiveMenu.openBrowse(viewer, holder.getTargetUuid(), holder.getTargetName(),
                    holder.getReturnPool(), holder.getReturnTitle(), holder.getReturnCategory(), holder.getReturnPage());
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

    private void handleSearchClick(Player viewer, SearchSession session, int slot) {
        if (slot != ItemGiveMenu.SEARCH_RESULT_SLOT) {
            return;
        }
        String query = session.getPendingQuery();
        if (query == null || query.isBlank()) {
            Messages.error(viewer, "Type something to search for first.");
            return;
        }
        ACTIVE_SEARCHES.remove(viewer.getUniqueId());
        ItemGiveMenu.openSearchResults(viewer, session.getTargetUuid(), session.getTargetName(), query);
    }
}
