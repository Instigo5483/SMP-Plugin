package com.smpplugin.core.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public final class Messages {

    private static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.AQUA + "SMP" + ChatColor.GRAY + "] ";

    private Messages() {
    }

    public static void info(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + ChatColor.WHITE + message);
    }

    public static void success(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + ChatColor.GREEN + message);
    }

    public static void error(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + ChatColor.RED + message);
    }

    private static Component prefix() {
        return Component.text()
                .append(Component.text("[", NamedTextColor.GRAY))
                .append(Component.text("SMP", NamedTextColor.AQUA))
                .append(Component.text("] ", NamedTextColor.GRAY))
                .build();
    }

    /** Sends the target a request notification with clickable Accept/Deny buttons. */
    public static void tpaRequest(Player target, Player requester) {
        Component accept = Component.text("[Accept]", NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/tpaaccept"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to accept", NamedTextColor.GRAY)));

        Component deny = Component.text("[Deny]", NamedTextColor.RED)
                .decorate(TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/tpadeny"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to deny", NamedTextColor.GRAY)));

        Component message = Component.text()
                .append(prefix())
                .append(Component.text(requester.getName() + " wants to teleport to you. ", NamedTextColor.WHITE))
                .append(accept)
                .append(Component.space())
                .append(deny)
                .build();

        target.sendMessage(message);
    }

    /** Sends a warp list where each warp name is clickable and runs /warp <name>. */
    public static void warpList(CommandSender sender, Set<String> names) {
        if (names.isEmpty()) {
            info(sender, "There are no warps yet.");
            return;
        }
        TextComponent.Builder builder = Component.text()
                .append(prefix())
                .append(Component.text("Warps (" + names.size() + "): ", NamedTextColor.WHITE));

        boolean first = true;
        for (String name : names) {
            if (!first) {
                builder.append(Component.text(", ", NamedTextColor.DARK_GRAY));
            }
            first = false;
            builder.append(Component.text(name, NamedTextColor.AQUA)
                    .decorate(TextDecoration.UNDERLINED)
                    .clickEvent(ClickEvent.runCommand("/warp " + name))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to teleport to " + name, NamedTextColor.GRAY))));
        }
        sender.sendMessage(builder.build());
    }

    /** Sends a home list where each home name is clickable and runs /home <name>. */
    public static void homeList(CommandSender sender, Map<String, Location> homes, int max) {
        String limitText = max < 0 ? "unlimited" : String.valueOf(max);
        if (homes.isEmpty()) {
            info(sender, "You have no homes yet. (limit: " + limitText + ")");
            return;
        }
        TextComponent.Builder builder = Component.text()
                .append(prefix())
                .append(Component.text("Homes (" + homes.size() + "/" + limitText + "): ", NamedTextColor.WHITE));

        boolean first = true;
        for (Map.Entry<String, Location> entry : homes.entrySet()) {
            if (!first) {
                builder.append(Component.text(", ", NamedTextColor.DARK_GRAY));
            }
            first = false;
            String name = entry.getKey();
            Location location = entry.getValue();
            String hoverText = location == null
                    ? "Click to teleport"
                    : String.format("Click to teleport\n%s (%d, %d, %d)",
                            location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
            builder.append(Component.text(name, NamedTextColor.AQUA)
                    .decorate(TextDecoration.UNDERLINED)
                    .clickEvent(ClickEvent.runCommand("/home " + name))
                    .hoverEvent(HoverEvent.showText(Component.text(hoverText, NamedTextColor.GRAY))));
        }
        sender.sendMessage(builder.build());
    }
}
