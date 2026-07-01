package com.smpplugin.core.commands;

import com.smpplugin.core.data.TpaManager;
import com.smpplugin.core.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpaAcceptCommand implements CommandExecutor {

    private final TpaManager tpaManager;

    public TpaAcceptCommand(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player target)) {
            Messages.error(sender, "Only players can use this command.");
            return true;
        }
        UUID requesterId = tpaManager.consumeRequest(target.getUniqueId());
        if (requesterId == null) {
            Messages.error(sender, "You have no pending teleport request.");
            return true;
        }
        Player requester = Bukkit.getPlayer(requesterId);
        if (requester == null || !requester.isOnline()) {
            Messages.error(sender, "That player is no longer online.");
            return true;
        }
        requester.teleport(target.getLocation());
        Messages.success(requester, "Your teleport request to " + target.getName() + " was accepted.");
        Messages.success(target, "Teleported " + requester.getName() + " to you.");
        return true;
    }
}
