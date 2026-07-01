package com.smpplugin.core.commands;

import com.smpplugin.core.data.TpaManager;
import com.smpplugin.core.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TpaCommand implements CommandExecutor, TabCompleter {

    private final TpaManager tpaManager;

    public TpaCommand(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player requester)) {
            Messages.error(sender, "Only players can use this command.");
            return true;
        }
        if (args.length != 1) {
            Messages.error(sender, "Usage: /tpa <player>");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            Messages.error(sender, "Player '" + args[0] + "' is not online.");
            return true;
        }
        if (target.equals(requester)) {
            Messages.error(sender, "You can't send a teleport request to yourself.");
            return true;
        }

        TpaManager.SendResult result = tpaManager.sendRequest(requester, target, () -> {
            Messages.error(requester, target.getName() + " did not respond in time. Request expired.");
            Messages.error(target, requester.getName() + "'s teleport request expired.");
        });

        if (result == TpaManager.SendResult.ALREADY_PENDING) {
            Messages.error(sender, target.getName() + " already has a pending teleport request.");
            return true;
        }

        Messages.success(sender, "Teleport request sent to " + target.getName() + ".");
        Messages.tpaRequest(target, requester);
        return true;
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
        return new ArrayList<>();
    }
}
