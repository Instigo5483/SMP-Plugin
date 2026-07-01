package com.smpplugin.core.listeners;

import com.smpplugin.core.data.TpaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final TpaManager tpaManager;

    public PlayerQuitListener(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        tpaManager.clearInvolving(event.getPlayer().getUniqueId());
    }
}
