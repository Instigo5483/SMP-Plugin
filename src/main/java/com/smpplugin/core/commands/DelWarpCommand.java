package com.smpplugin.core.commands;

import com.smpplugin.core.data.WarpManager;
import com.smpplugin.core.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class DelWarpCommand implements CommandExecutor, TabCompleter {

    private final WarpManager warpManager;

    public DelWarpCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            Messages.error(sender, "Usage: /delwarp <name>");
            return true;
        }
        if (warpManager.deleteWarp(args[0])) {
            Messages.success(sender, "Warp '" + args[0].toLowerCase() + "' deleted.");
        } else {
            Messages.error(sender, "No warp named '" + args[0] + "' exists.");
        }
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
