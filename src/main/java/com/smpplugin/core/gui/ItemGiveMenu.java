package com.smpplugin.core.gui;

import com.smpplugin.core.commands.ItemGiveCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Builds and opens the /itemgive browse screen (a persistent row of category tabs,
 * like creative mode, with a paginated item grid below), the quantity-picker, and
 * the anvil-based search box.
 */
public final class ItemGiveMenu {

    public static final int ITEMS_PER_PAGE = 36;
    public static final int GRID_START_SLOT = 9;
    public static final int PREV_SLOT = 45;
    public static final int SEARCH_SLOT = 46;
    public static final int PAGE_INFO_SLOT = 49;
    public static final int CLOSE_SLOT = 50;
    public static final int NEXT_SLOT = 53;

    public static final int QTY_MINUS_64 = 0;
    public static final int QTY_MINUS_10 = 1;
    public static final int QTY_MINUS_1 = 2;
    public static final int QTY_PREVIEW = 4;
    public static final int QTY_PLUS_1 = 6;
    public static final int QTY_PLUS_10 = 7;
    public static final int QTY_PLUS_64 = 8;
    public static final int QTY_BACK = 18;
    public static final int QTY_GIVE = 22;

    public static final int SEARCH_RESULT_SLOT = 2;

    private static final List<Material> GIVEABLE_MATERIALS;
    private static final Map<ItemCategory, List<Material>> BY_CATEGORY;

    static {
        List<Material> materials = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.isItem() && !material.isLegacy()) {
                materials.add(material);
            }
        }
        materials.sort((a, b) -> a.name().compareTo(b.name()));
        GIVEABLE_MATERIALS = List.copyOf(materials);

        Map<ItemCategory, List<Material>> byCategory = new EnumMap<>(ItemCategory.class);
        for (ItemCategory category : ItemCategory.values()) {
            byCategory.put(category, new ArrayList<>());
        }
        for (Material material : GIVEABLE_MATERIALS) {
            byCategory.get(ItemCategory.classify(material)).add(material);
        }
        BY_CATEGORY = byCategory;
    }

    private ItemGiveMenu() {
    }

    public static List<Material> materialsInCategory(ItemCategory category) {
        return BY_CATEGORY.get(category);
    }

    public static int pageCount(List<Material> pool) {
        return Math.max(1, (pool.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE);
    }

    public static void openCategory(Player viewer, UUID targetUuid, String targetName, ItemCategory category, int page) {
        openBrowse(viewer, targetUuid, targetName, materialsInCategory(category), category.label(), category, page);
    }

    public static void openSearchResults(Player viewer, UUID targetUuid, String targetName, String query) {
        String needle = query.toLowerCase().trim().replace(' ', '_');
        List<Material> results = new ArrayList<>();
        for (Material material : GIVEABLE_MATERIALS) {
            if (material.name().toLowerCase().contains(needle)) {
                results.add(material);
            }
        }
        openBrowse(viewer, targetUuid, targetName, results, "Search: " + query, null, 0);
    }

    public static void openBrowse(Player viewer, UUID targetUuid, String targetName, List<Material> pool,
                                   String title, ItemCategory activeCategory, int page) {
        int pages = pageCount(pool);
        int clampedPage = Math.max(0, Math.min(page, pages - 1));

        BrowseMenuHolder holder = new BrowseMenuHolder(targetUuid, targetName, pool, title, activeCategory, clampedPage);
        Inventory inventory = Bukkit.createInventory(holder, 54,
                Component.text("Give to " + targetName + ": " + title, NamedTextColor.DARK_GRAY));
        holder.setInventory(inventory);

        ItemCategory[] categories = ItemCategory.values();
        for (int i = 0; i < categories.length; i++) {
            inventory.setItem(i, tabIcon(categories[i], categories[i] == activeCategory));
        }

        int start = clampedPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, pool.size());
        for (int i = start; i < end; i++) {
            inventory.setItem(GRID_START_SLOT + (i - start), icon(pool.get(i)));
        }

        if (clampedPage > 0) {
            inventory.setItem(PREV_SLOT, navIcon(Material.ARROW, "Previous Page"));
        }
        inventory.setItem(SEARCH_SLOT, navIcon(Material.COMPASS, activeCategory == null ? "Search (active)" : "Search"));
        inventory.setItem(PAGE_INFO_SLOT, navIcon(Material.PAPER, title + " - Page " + (clampedPage + 1) + " / " + pages));
        inventory.setItem(CLOSE_SLOT, navIcon(Material.BARRIER, "Close"));
        if (clampedPage < pages - 1) {
            inventory.setItem(NEXT_SLOT, navIcon(Material.ARROW, "Next Page"));
        }

        viewer.openInventory(inventory);
    }

    /**
     * Force-opens a real anvil menu (not a fake Bukkit.createInventory one, which never
     * fires PrepareAnvilEvent) so the player can type a search term into the rename box.
     */
    @SuppressWarnings("deprecation")
    public static boolean openSearch(Player viewer) {
        InventoryView view = viewer.openAnvil(null, true);
        if (view == null) {
            return false;
        }
        Inventory inventory = view.getTopInventory();
        ItemStack base = new ItemStack(Material.PAPER);
        ItemMeta meta = base.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Type a search term", NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false));
            base.setItemMeta(meta);
        }
        inventory.setItem(0, base);
        return true;
    }

    public static void openQuantityPicker(Player viewer, UUID targetUuid, String targetName, Material material,
                                           int amount, List<Material> returnPool, String returnTitle,
                                           ItemCategory returnCategory, int returnPage) {
        QuantityMenuHolder holder = new QuantityMenuHolder(targetUuid, targetName, material, amount,
                returnPool, returnTitle, returnCategory, returnPage);
        Inventory inventory = Bukkit.createInventory(holder, 27,
                Component.text("Give " + displayName(material) + " to " + targetName, NamedTextColor.DARK_GRAY));
        holder.setInventory(inventory);

        inventory.setItem(QTY_MINUS_64, navIcon(Material.RED_STAINED_GLASS_PANE, "-64"));
        inventory.setItem(QTY_MINUS_10, navIcon(Material.ORANGE_STAINED_GLASS_PANE, "-10"));
        inventory.setItem(QTY_MINUS_1, navIcon(Material.YELLOW_STAINED_GLASS_PANE, "-1"));
        inventory.setItem(QTY_PLUS_1, navIcon(Material.LIME_STAINED_GLASS_PANE, "+1"));
        inventory.setItem(QTY_PLUS_10, navIcon(Material.GREEN_STAINED_GLASS_PANE, "+10"));
        inventory.setItem(QTY_PLUS_64, navIcon(Material.CYAN_STAINED_GLASS_PANE, "+64"));
        inventory.setItem(QTY_BACK, navIcon(Material.ARROW, "Back"));
        inventory.setItem(QTY_GIVE, navIcon(Material.LIME_DYE, "Give"));
        renderPreview(inventory, material, amount);

        viewer.openInventory(inventory);
    }

    public static void renderPreview(Inventory inventory, Material material, int amount) {
        ItemStack preview = new ItemStack(material, Math.max(1, Math.min(amount, ItemGiveCommand.MAX_AMOUNT)));
        ItemMeta meta = preview.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName(material), NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(Component.text("Amount: " + amount, NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)));
            preview.setItemMeta(meta);
        }
        inventory.setItem(QTY_PREVIEW, preview);
    }

    public static int amountAdjustFor(int slot) {
        return switch (slot) {
            case QTY_MINUS_64 -> -64;
            case QTY_MINUS_10 -> -10;
            case QTY_MINUS_1 -> -1;
            case QTY_PLUS_1 -> 1;
            case QTY_PLUS_10 -> 10;
            case QTY_PLUS_64 -> 64;
            default -> 0;
        };
    }

    private static ItemStack tabIcon(ItemCategory category, boolean active) {
        ItemStack stack = new ItemStack(category.icon());
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            NamedTextColor color = active ? NamedTextColor.GREEN : NamedTextColor.YELLOW;
            String prefix = active ? "> " : "";
            meta.displayName(Component.text(prefix + category.label(), color)
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, active));
            meta.lore(List.of(Component.text(materialsInCategory(category).size() + " items", NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)));
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private static ItemStack icon(Material material) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName(material), NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false));
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private static ItemStack navIcon(Material material, String name) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name, NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private static String displayName(Material material) {
        String[] words = material.name().toLowerCase().split("_");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return builder.toString();
    }
}
