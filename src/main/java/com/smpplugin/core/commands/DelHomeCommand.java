package com.smpplugin.core.commands;

import com.smpplugin.core.data.HomeManager;
import com.smpplugin.core.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DelHomeCommand implements CommandExecutor, TabCompleter {

    private final HomeManager homeManager;

    public DelHomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.error(sender, "Only players can use this command.");
            return true;
        }
        if (args.length > 1) {
            Messages.error(sender, "Usage: /delhome [name]");
            return true;
        }
        String name = args.length == 1 ? args[0] : HomeManager.DEFAULT_HOME_NAME;
        if (homeManager.deleteHome(player.getUniqueId(), name)) {
            Messages.success(sender, "Home '" + name.toLowerCase() + "' deleted.");
        } else {
            Messages.error(sender, "You have no home named '" + name + "'.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            return TabCompleteUtil.filter(homeManager.getHomeNames(player.getUniqueId()), args[0]);
        }
        return new ArrayList<>();
    }
}
