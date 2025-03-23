package com.lozaine.playerutilities.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.lozaine.playerutilities.PlayerUtilities;
import com.lozaine.playerutilities.managers.PlayerStateManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlyCommand implements CommandExecutor, TabCompleter {

    private final PlayerUtilities plugin;
    private final List<String> speedSuggestions = Arrays.asList("0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0");

    public FlyCommand(PlayerUtilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("playerutilities.fly")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        PlayerStateManager stateManager = plugin.getPlayerStateManager();

        // Handle fly speed adjustment if an argument is provided
        if (args.length > 0) {
            try {
                float speed = Float.parseFloat(args[0]);

                // Minecraft's fly speed is between -1.0 and 1.0, but typical values are 0.1 to 1.0
                if (speed < 0.1 || speed > 1.0) {
                    player.sendMessage(ChatColor.RED + "Speed must be between 0.1 and 1.0");
                    return true;
                }

                player.setFlySpeed(speed);
                stateManager.setFlySpeed(player.getUniqueId(), speed);
                player.sendMessage(ChatColor.GREEN + "Fly speed set to " + speed);

                // If they're adjusting speed but not flying, enable flying too
                if (!player.getAllowFlight()) {
                    toggleFlyMode(player, stateManager);
                }

                return true;
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid speed value. Use a number between 0.1 and 1.0");
                return true;
            }
        }

        // Toggle fly mode
        toggleFlyMode(player, stateManager);
        return true;
    }

    private void toggleFlyMode(Player player, PlayerStateManager stateManager) {
        boolean flyEnabled = !player.getAllowFlight();

        player.setAllowFlight(flyEnabled);
        player.setFlying(flyEnabled);
        stateManager.setFlyEnabled(player.getUniqueId(), flyEnabled);

        if (flyEnabled) {
            player.sendMessage(ChatColor.GREEN + "Fly mode enabled.");

            // Apply saved fly speed if available
            float savedSpeed = stateManager.getFlySpeed(player.getUniqueId());
            player.setFlySpeed(savedSpeed);
        } else {
            player.sendMessage(ChatColor.YELLOW + "Fly mode disabled.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();

            // Offer speed suggestions
            for (String speed : speedSuggestions) {
                if (speed.startsWith(partial)) {
                    completions.add(speed);
                }
            }
        }

        return completions;
    }
}
