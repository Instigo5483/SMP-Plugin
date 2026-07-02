package com.smpplugin.core.gui;

import com.smpplugin.core.commands.ItemGiveCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Builds and opens the /itemgive item-picker and quantity-picker inventories. */
public final class ItemGiveMenu {

    public static final int ITEMS_PER_PAGE = 45;
    public static final int PREV_SLOT = 45;
    public static final int PAGE_INFO_SLOT = 49;
    public static final int NEXT_SLOT = 53;
    public static final int CLOSE_SLOT = 50;

    public static final int QTY_MINUS_64 = 0;
    public static final int QTY_MINUS_10 = 1;
    public static final int QTY_MINUS_1 = 2;
    public static final int QTY_PREVIEW = 4;
    public static final int QTY_PLUS_1 = 6;
    public static final int QTY_PLUS_10 = 7;
    public static final int QTY_PLUS_64 = 8;
    public static final int QTY_BACK = 18;
    public static final int QTY_GIVE = 22;

    private static final List<Material> GIVEABLE_MATERIALS;

    static {
        List<Material> materials = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.isItem() && !material.isLegacy()) {
                materials.add(material);
            }
        }
        materials.sort((a, b) -> a.name().compareTo(b.name()));
        GIVEABLE_MATERIALS = List.copyOf(materials);
    }

    private ItemGiveMenu() {
    }

    public static List<Material> materials() {
        return GIVEABLE_MATERIALS;
    }

    public static int pageCount() {
        return Math.max(1, (GIVEABLE_MATERIALS.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE);
    }

    public static void openItemList(Player viewer, UUID targetUuid, String targetName, int page) {
        int pages = pageCount();
        int clampedPage = Math.max(0, Math.min(page, pages - 1));

        ItemMenuHolder holder = new ItemMenuHolder(targetUuid, targetName, clampedPage);
        Inventory inventory = Bukkit.createInventory(holder, 54,
                Component.text("Give items to " + targetName, NamedTextColor.DARK_GRAY));
        holder.setInventory(inventory);

        int start = clampedPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, GIVEABLE_MATERIALS.size());
        for (int i = start; i < end; i++) {
            inventory.setItem(i - start, icon(GIVEABLE_MATERIALS.get(i)));
        }

        if (clampedPage > 0) {
            inventory.setItem(PREV_SLOT, navIcon(Material.ARROW, "Previous Page"));
        }
        inventory.setItem(PAGE_INFO_SLOT, navIcon(Material.PAPER, "Page " + (clampedPage + 1) + " / " + pages));
        inventory.setItem(CLOSE_SLOT, navIcon(Material.BARRIER, "Close"));
        if (clampedPage < pages - 1) {
            inventory.setItem(NEXT_SLOT, navIcon(Material.ARROW, "Next Page"));
        }

        viewer.openInventory(inventory);
    }

    public static void openQuantityPicker(Player viewer, UUID targetUuid, String targetName, Material material,
                                           int amount, int returnPage) {
        QuantityMenuHolder holder = new QuantityMenuHolder(targetUuid, targetName, material, amount, returnPage);
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
