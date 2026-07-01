package com.smpplugin.core.commands;

import com.smpplugin.core.data.TpaManager;
import com.smpplugin.core.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpaDenyCommand implements CommandExecutor {

    private final TpaManager tpaManager;

    public TpaDenyCommand(TpaManager tpaManager) {
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
        Messages.error(sender, "Teleport request denied.");
        Player requester = Bukkit.getPlayer(requesterId);
        if (requester != null && requester.isOnline()) {
            Messages.error(requester, target.getName() + " denied your teleport request.");
        }
        return true;
    }
}
