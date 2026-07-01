package com.smpplugin.core.commands;

import com.smpplugin.core.data.WarpManager;
import com.smpplugin.core.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WarpListCommand implements CommandExecutor {

    private final WarpManager warpManager;

    public WarpListCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Messages.warpList(sender, warpManager.getWarpNames());
        return true;
    }
}
