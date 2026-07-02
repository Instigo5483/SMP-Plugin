package com.smpplugin.core;

import com.smpplugin.core.commands.DelHomeCommand;
import com.smpplugin.core.commands.DelWarpCommand;
import com.smpplugin.core.commands.HomeCommand;
import com.smpplugin.core.commands.HomeListCommand;
import com.smpplugin.core.commands.ItemGiveCommand;
import com.smpplugin.core.commands.SetHomeCommand;
import com.smpplugin.core.commands.SetWarpCommand;
import com.smpplugin.core.commands.TpaAcceptCommand;
import com.smpplugin.core.commands.TpaCommand;
import com.smpplugin.core.commands.TpaDenyCommand;
import com.smpplugin.core.commands.WarpCommand;
import com.smpplugin.core.commands.WarpListCommand;
import com.smpplugin.core.data.HomeManager;
import com.smpplugin.core.data.TpaManager;
import com.smpplugin.core.data.WarpManager;
import com.smpplugin.core.gui.ItemGiveGuiListener;
import com.smpplugin.core.listeners.PlayerQuitListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class SMPPlugin extends JavaPlugin {

    private WarpManager warpManager;
    private HomeManager homeManager;
    private TpaManager tpaManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        warpManager = new WarpManager(this);
        homeManager = new HomeManager(this);
        tpaManager = new TpaManager(this);

        WarpCommand warpCommand = new WarpCommand(warpManager);
        register("warp", warpCommand, warpCommand);
        register("warps", new WarpListCommand(warpManager), null);
        register("setwarp", new SetWarpCommand(warpManager), null);
        DelWarpCommand delWarpCommand = new DelWarpCommand(warpManager);
        register("delwarp", delWarpCommand, delWarpCommand);

        register("sethome", new SetHomeCommand(homeManager), null);
        HomeCommand homeCommand = new HomeCommand(homeManager);
        register("home", homeCommand, homeCommand);
        DelHomeCommand delHomeCommand = new DelHomeCommand(homeManager);
        register("delhome", delHomeCommand, delHomeCommand);
        register("homes", new HomeListCommand(homeManager), null);

        TpaCommand tpaCommand = new TpaCommand(tpaManager);
        register("tpa", tpaCommand, tpaCommand);
        register("tpaaccept", new TpaAcceptCommand(tpaManager), null);
        register("tpadeny", new TpaDenyCommand(tpaManager), null);

        ItemGiveCommand itemGiveCommand = new ItemGiveCommand(this);
        register("itemgive", itemGiveCommand, itemGiveCommand);

        getServer().getPluginManager().registerEvents(new PlayerQuitListener(tpaManager), this);
        getServer().getPluginManager().registerEvents(new ItemGiveGuiListener(), this);
    }

    private void register(String name, CommandExecutor executor, TabCompleter completer) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            getLogger().warning("Command '" + name + "' is missing from plugin.yml");
            return;
        }
        command.setExecutor(executor);
        if (completer != null) {
            command.setTabCompleter(completer);
        }
    }
}
