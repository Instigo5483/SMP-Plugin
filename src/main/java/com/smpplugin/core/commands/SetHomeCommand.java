package com.smpplugin.core.commands;

import com.smpplugin.core.data.HomeManager;
import com.smpplugin.core.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {

    private final HomeManager homeManager;

    public SetHomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.error(sender, "Only players can use this command.");
            return true;
        }
        if (args.length > 1) {
            Messages.error(sender, "Usage: /sethome [name]");
            return true;
        }
        String name = args.length == 1 ? args[0] : HomeManager.DEFAULT_HOME_NAME;
        HomeManager.SetResult result = homeManager.setHome(player.getUniqueId(), name, player.getLocation());
        switch (result) {
            case CREATED -> Messages.success(sender, "Home '" + name.toLowerCase() + "' created.");
            case UPDATED -> Messages.success(sender, "Home '" + name.toLowerCase() + "' updated to your location.");
            case LIMIT_REACHED -> Messages.error(sender, "You've reached your home limit ("
                    + homeManager.getMaxHomes() + "). Delete one with /delhome first.");
        }
        return true;
    }
}
