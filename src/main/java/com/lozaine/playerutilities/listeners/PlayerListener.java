package com.lozaine.playerutilities.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.lozaine.playerutilities.PlayerUtilities;
import com.lozaine.playerutilities.managers.PlayerStateManager;

public class PlayerListener implements Listener {

    private final PlayerUtilities plugin;

    public PlayerListener(PlayerUtilities plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Apply saved player state (fly mode, god mode, etc.)
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            PlayerStateManager stateManager = plugin.getPlayerStateManager();
            stateManager.applyPlayerState(player);

            if (stateManager.isFlyEnabled(player.getUniqueId())) {
                player.sendMessage("§aFly mode has been enabled automatically.");
            }

            if (stateManager.isGodEnabled(player.getUniqueId())) {
                player.sendMessage("§aGod mode has been enabled automatically.");
            }
        }, 20L); // Delay by 1 second to ensure player is fully loaded
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // We don't need to do anything here since states are stored in memory
        // and saved when the plugin disables
    }
}
