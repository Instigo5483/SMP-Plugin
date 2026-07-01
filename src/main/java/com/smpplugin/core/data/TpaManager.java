package com.smpplugin.core.data;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks pending /tpa requests in memory, keyed by the target player's UUID.
 * A player can only have one incoming request at a time; requests auto-expire
 * after the configured timeout.
 */
public class TpaManager {

    private final JavaPlugin plugin;
    private final Map<UUID, PendingRequest> pendingByTarget = new HashMap<>();

    public TpaManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public enum SendResult { SENT, ALREADY_PENDING }

    private static final class PendingRequest {
        final UUID requester;
        final BukkitTask expiryTask;

        PendingRequest(UUID requester, BukkitTask expiryTask) {
            this.requester = requester;
            this.expiryTask = expiryTask;
        }
    }

    public SendResult sendRequest(Player requester, Player target, Runnable onExpire) {
        if (pendingByTarget.containsKey(target.getUniqueId())) {
            return SendResult.ALREADY_PENDING;
        }
        long timeoutSeconds = Math.max(1, plugin.getConfig().getInt("tpa-request-timeout-seconds", 60));
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            pendingByTarget.remove(target.getUniqueId());
            onExpire.run();
        }, timeoutSeconds * 20L);
        pendingByTarget.put(target.getUniqueId(), new PendingRequest(requester.getUniqueId(), task));
        return SendResult.SENT;
    }

    /** Removes and returns the pending requester for this target, cancelling its expiry task. */
    public UUID consumeRequest(UUID targetUuid) {
        PendingRequest request = pendingByTarget.remove(targetUuid);
        if (request == null) {
            return null;
        }
        request.expiryTask.cancel();
        return request.requester;
    }

    /** Clears any request where the given player is either the target or the requester. */
    public void clearInvolving(UUID playerUuid) {
        PendingRequest asTarget = pendingByTarget.remove(playerUuid);
        if (asTarget != null) {
            asTarget.expiryTask.cancel();
        }
        pendingByTarget.values().removeIf(request -> {
            if (request.requester.equals(playerUuid)) {
                request.expiryTask.cancel();
                return true;
            }
            return false;
        });
    }
}
