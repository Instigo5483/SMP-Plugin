package com.smpplugin.core.commands;

import com.smpplugin.core.data.HomeManager;
import com.smpplugin.core.util.Messages;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand implements CommandExecutor, TabCompleter {

    private final HomeManager homeManager;

    public HomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.error(sender, "Only players can use this command.");
            return true;
        }
        if (args.length > 1) {
            Messages.error(sender, "Usage: /home [name]");
            return true;
        }
        String name = args.length == 1 ? args[0] : HomeManager.DEFAULT_HOME_NAME;
        Location location = homeManager.getHome(player.getUniqueId(), name);
        if (location == null) {
            Messages.error(sender, "You have no home named '" + name + "'.");
            return true;
        }
        player.teleport(location);
        Messages.success(sender, "Teleported to home '" + name.toLowerCase() + "'.");
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
