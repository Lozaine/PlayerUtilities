package com.lozaine.playerutilities;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.lozaine.playerutilities.commands.FlyCommand;
import com.lozaine.playerutilities.commands.GodCommand;
import com.lozaine.playerutilities.commands.TeleportCommand;
import com.lozaine.playerutilities.listeners.PlayerListener;
import com.lozaine.playerutilities.managers.PlayerStateManager;

public class PlayerUtilities extends JavaPlugin {

    private PlayerStateManager playerStateManager;

    @Override
    public void onEnable() {
        // Create configuration file if it doesn't exist
        saveDefaultConfig();

        // Initialize player state manager
        playerStateManager = new PlayerStateManager(this);

        // Register event listeners
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        // Register commands
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("god").setExecutor(new GodCommand(this));
        getCommand("worldtp").setExecutor(new TeleportCommand(this));

        getLogger().info("PlayerUtilities has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save player states before plugin disables
        playerStateManager.saveAllPlayerStates();
        getLogger().info("PlayerUtilities has been disabled!");
    }

    public PlayerStateManager getPlayerStateManager() {
        return playerStateManager;
    }
}
