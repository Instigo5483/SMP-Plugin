package com.smpplugin.core.commands;

import com.smpplugin.core.data.WarpManager;
import com.smpplugin.core.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand implements CommandExecutor {

    private final WarpManager warpManager;

    public SetWarpCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.error(sender, "Only players can use this command.");
            return true;
        }
        if (args.length != 1) {
            Messages.error(sender, "Usage: /setwarp <name>");
            return true;
        }
        warpManager.setWarp(args[0], player.getLocation());
        Messages.success(sender, "Warp '" + args[0].toLowerCase() + "' created at your location.");
        return true;
    }
}
