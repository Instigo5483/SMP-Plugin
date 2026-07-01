package com.smpplugin.core.commands;

import com.smpplugin.core.data.WarpManager;
import com.smpplugin.core.util.Messages;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpCommand implements CommandExecutor, TabCompleter {

    private final WarpManager warpManager;

    public WarpCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.error(sender, "Only players can use this command.");
            return true;
        }
        if (args.length != 1) {
            Messages.error(sender, "Usage: /warp <name>");
            return true;
        }
        Location location = warpManager.getWarp(args[0]);
        if (location == null) {
            Messages.error(sender, "No warp named '" + args[0] + "' exists.");
            return true;
        }
        player.teleport(location);
        Messages.success(sender, "Teleported to warp '" + args[0].toLowerCase() + "'.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return TabCompleteUtil.filter(warpManager.getWarpNames(), args[0]);
        }
        return new ArrayList<>();
    }
}
