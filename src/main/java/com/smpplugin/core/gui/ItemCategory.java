package com.smpplugin.core.gui;

import org.bukkit.Material;

/** Groups giveable materials by rough similarity for the /itemgive category menu. */
public enum ItemCategory {

    TOOLS("Tools", Material.IRON_PICKAXE),
    WEAPONS("Weapons", Material.IRON_SWORD),
    ARMOR("Armor", Material.IRON_CHESTPLATE),
    REDSTONE("Redstone", Material.REDSTONE),
    BREWING("Brewing", Material.POTION),
    FOOD("Food", Material.COOKED_BEEF),
    SPAWN_EGGS("Spawn Eggs", Material.ZOMBIE_SPAWN_EGG),
    BLOCKS("Blocks", Material.BRICKS),
    MISC("Miscellaneous", Material.CHEST);

    private final String label;
    private final Material icon;

    ItemCategory(String label, Material icon) {
        this.label = label;
        this.icon = icon;
    }

    public String label() {
        return label;
    }

    public Material icon() {
        return icon;
    }

    public static ItemCategory classify(Material material) {
        String name = material.name();

        if (name.endsWith("_SPAWN_EGG")) {
            return SPAWN_EGGS;
        }
        if (name.endsWith("_SWORD") || name.equals("BOW") || name.equals("CROSSBOW") || name.equals("TRIDENT")
                || name.equals("ARROW") || name.equals("SPECTRAL_ARROW") || name.equals("TIPPED_ARROW")) {
            return WEAPONS;
        }
        if (name.endsWith("_PICKAXE") || name.endsWith("_AXE") || name.endsWith("_SHOVEL") || name.endsWith("_HOE")
                || name.equals("SHEARS") || name.equals("FLINT_AND_STEEL") || name.equals("FISHING_ROD")
                || name.equals("SPYGLASS") || name.equals("BRUSH") || name.equals("COMPASS")
                || name.equals("RECOVERY_COMPASS") || name.equals("CLOCK")) {
            return TOOLS;
        }
        if (name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS")
                || name.endsWith("_BOOTS") || name.equals("SHIELD") || name.equals("ELYTRA")
                || name.equals("TURTLE_HELMET")) {
            return ARMOR;
        }
        if (name.contains("POTION") || name.equals("CAULDRON") || name.equals("BREWING_STAND")
                || name.equals("GLASS_BOTTLE")) {
            return BREWING;
        }
        if (name.contains("REDSTONE") || name.contains("REPEATER") || name.contains("COMPARATOR")
                || name.contains("PISTON") || name.equals("OBSERVER") || name.contains("DISPENSER")
                || name.contains("DROPPER") || name.contains("HOPPER") || name.contains("RAIL")
                || name.contains("LEVER") || name.contains("BUTTON") || name.contains("PRESSURE_PLATE")
                || name.contains("TRIPWIRE") || name.equals("TARGET") || name.contains("DAYLIGHT_DETECTOR")
                || name.contains("NOTE_BLOCK") || name.equals("LECTERN") || name.contains("SCULK_SENSOR")) {
            return REDSTONE;
        }
        if (material.isEdible()) {
            return FOOD;
        }
        if (material.isBlock()) {
            return BLOCKS;
        }
        return MISC;
    }
}
