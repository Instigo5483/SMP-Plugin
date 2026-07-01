package com.smpplugin.core.commands;

import com.smpplugin.core.data.HomeManager;
import com.smpplugin.core.util.Messages;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class HomeListCommand implements CommandExecutor {

    private final HomeManager homeManager;

    public HomeListCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.error(sender, "Only players can use this command.");
            return true;
        }
        UUID uuid = player.getUniqueId();
        Map<String, Location> homes = new LinkedHashMap<>();
        for (String name : homeManager.getHomeNames(uuid)) {
            homes.put(name, homeManager.getHome(uuid, name));
        }
        Messages.homeList(sender, homes, homeManager.getMaxHomes());
        return true;
    }
}
