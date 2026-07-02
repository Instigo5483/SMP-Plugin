package com.smpplugin.core.commands;

import com.smpplugin.core.gui.ItemCategory;
import com.smpplugin.core.gui.ItemGiveMenu;
import com.smpplugin.core.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gives items straight through the Bukkit inventory API rather than dispatching
 * the vanilla /give command. Usable by anyone holding the smp.itemgive permission
 * (ops always have it), plus any player name listed under itemgive-allowed-players
 * in config.yml. That allow-list check happens here rather than via plugin.yml's
 * command permission field, since the two checks are OR'd together.
 *
 * With just a target name ("/itemgive <player>"), a Player sender gets a GUI
 * item/quantity picker instead of needing to type the material and amount.
 */
public class ItemGiveCommand implements CommandExecutor, TabCompleter {

    public static final int MAX_AMOUNT = 6400;
    private static final String PERMISSION = "smp.itemgive";

    private final JavaPlugin plugin;

    public ItemGiveCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean isAllowed(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (player.hasPermission(PERMISSION)) {
            return true;
        }
        for (String name : plugin.getConfig().getStringList("itemgive-allowed-players")) {
            if (name.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!isAllowed(sender)) {
            Messages.error(sender, "You don't have permission to use this command.");
            return true;
        }
        if (args.length < 1 || args.length > 3) {
            Messages.error(sender, "Usage: /" + label + " <player> [material] [amount]");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            Messages.error(sender, "Player '" + args[0] + "' is not online.");
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player viewer)) {
                Messages.error(sender, "Console must specify a material: /" + label + " <player> <material> [amount]");
                return true;
            }
            ItemGiveMenu.openCategory(viewer, target.getUniqueId(), target.getName(), ItemCategory.values()[0], 0);
            return true;
        }

        Material material = Material.matchMaterial(args[1]);
        if (material == null || !material.isItem()) {
            Messages.error(sender, "'" + args[1] + "' is not a valid item.");
            return true;
        }

        int amount = 1;
        if (args.length == 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                Messages.error(sender, "Amount must be a whole number.");
                return true;
            }
        }
        if (amount <= 0) {
            Messages.error(sender, "Amount must be greater than zero.");
            return true;
        }
        if (amount > MAX_AMOUNT) {
            Messages.error(sender, "Amount too large; max is " + MAX_AMOUNT + ".");
            return true;
        }

        giveItem(sender, target, material, amount);
        return true;
    }

    /** Shared by the direct-command path and the GUI's "Give" button. */
    public static void giveItem(CommandSender sender, Player target, Material material, int amount) {
        ItemStack stack = new ItemStack(material, amount);
        Map<Integer, ItemStack> leftover = target.getInventory().addItem(stack);
        for (ItemStack remaining : leftover.values()) {
            target.getWorld().dropItemNaturally(target.getLocation(), remaining);
        }

        Messages.success(sender, "Gave " + amount + " x " + material.name() + " to " + target.getName() + ".");
        if (!target.equals(sender)) {
            Messages.info(target, "You received " + amount + " x " + material.name() + ".");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(lower))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            String lower = args[1].toLowerCase();
            return Arrays.stream(Material.values())
                    .filter(Material::isItem)
                    .map(m -> m.name().toLowerCase())
                    .filter(name -> name.startsWith(lower))
                    .limit(50)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
