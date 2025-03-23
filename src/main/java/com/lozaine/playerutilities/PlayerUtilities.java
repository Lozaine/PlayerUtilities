package com.lozaine.playerutilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerUtilities extends JavaPlugin implements Listener {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        config = getConfig();

        // Register events
        getServer().getPluginManager().registerEvents(this, this);

        // Register commands
        getCommand("flyspeed").setExecutor(new FlySpeedCommand(this));
        getCommand("worldtp").setExecutor(new WorldTeleportCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("god").setExecutor(new GodCommand(this));

        getLogger().info("PlayerUtilities has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PlayerUtilities has been disabled!");
    }

    /**
     * Listen for player join events to restore their previous fly/god mode status
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();

        // Check if player had fly mode enabled previously
        if (config.getBoolean("players." + playerUUID + ".fly", false)) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.sendMessage(ChatColor.GREEN + "Your fly mode has been automatically restored.");
        }

        // Check if player had god mode enabled previously
        if (config.getBoolean("players." + playerUUID + ".godmode", false)) {
            // Enable god mode
            enableGodMode(player);
            player.sendMessage(ChatColor.GREEN + "Your god mode has been automatically restored.");
        }

        // Restore fly speed if it was customized
        if (config.contains("players." + playerUUID + ".flyspeed")) {
            float flySpeed = (float) config.getDouble("players." + playerUUID + ".flyspeed", 0.1);
            player.setFlySpeed(flySpeed);
        }
    }

    /**
     * Enable god mode for a player
     */
    public void enableGodMode(Player player) {
        player.setInvulnerable(true);
        savePlayerState(player, "godmode", true);
    }

    /**
     * Disable god mode for a player
     */
    public void disableGodMode(Player player) {
        player.setInvulnerable(false);
        savePlayerState(player, "godmode", false);
    }

    /**
     * Save player state to config
     */
    public void savePlayerState(Player player, String property, Object value) {
        String playerUUID = player.getUniqueId().toString();
        config.set("players." + playerUUID + "." + property, value);
        saveConfig();
    }

    /**
     * Send a formatted message to a command sender
     */
    public void sendMessage(CommandSender sender, String message) {
        String prefix = config.getString("messages.prefix", "&8[&bPlayerUtilities&8] &f");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }
}
