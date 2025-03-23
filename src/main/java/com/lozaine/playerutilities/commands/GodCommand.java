package com.lozaine.playerutilities.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lozaine.playerutilities.PlayerUtilities;
import com.lozaine.playerutilities.managers.PlayerStateManager;

public class GodCommand implements CommandExecutor {

    private final PlayerUtilities plugin;

    public GodCommand(PlayerUtilities plugin) {
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
        if (!player.hasPermission("playerutilities.god")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        PlayerStateManager stateManager = plugin.getPlayerStateManager();
        boolean currentGodState = stateManager.isGodEnabled(player.getUniqueId());
        boolean newGodState = !currentGodState;

        // Toggle god mode
        player.setInvulnerable(newGodState);
        stateManager.setGodEnabled(player.getUniqueId(), newGodState);

        if (newGodState) {
            // Restore health and hunger when enabling god mode
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20); // 20 is max food level
            player.setSaturation(20); // Max saturation level
            player.setFireTicks(0); // Extinguish fire if burning

            player.sendMessage(ChatColor.GREEN + "God mode enabled. Health and hunger restored.");
        } else {
            player.sendMessage(ChatColor.YELLOW + "God mode disabled.");
        }

        return true;
    }

    // Handle targeting another player (requires additional permission)
    private void handleTargetPlayer(CommandSender sender, String targetName) {
        if (!sender.hasPermission("playerutilities.god.others")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to toggle god mode for other players.");
            return;
        }

        Player targetPlayer = plugin.getServer().getPlayer(targetName);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + targetName);
            return;
        }

        PlayerStateManager stateManager = plugin.getPlayerStateManager();
        boolean currentGodState = stateManager.isGodEnabled(targetPlayer.getUniqueId());
        boolean newGodState = !currentGodState;

        // Toggle god mode for target player
        targetPlayer.setInvulnerable(newGodState);
        stateManager.setGodEnabled(targetPlayer.getUniqueId(), newGodState);

        if (newGodState) {
            // Restore health and hunger when enabling god mode
            targetPlayer.setHealth(targetPlayer.getMaxHealth());
            targetPlayer.setFoodLevel(20);
            targetPlayer.setSaturation(20);
            targetPlayer.setFireTicks(0);

            targetPlayer.sendMessage(ChatColor.GREEN + "God mode enabled by " + sender.getName() + ". Health and hunger restored.");
            sender.sendMessage(ChatColor.GREEN + "God mode enabled for " + targetPlayer.getName() + ".");
        } else {
            targetPlayer.sendMessage(ChatColor.YELLOW + "God mode disabled by " + sender.getName() + ".");
            sender.sendMessage(ChatColor.YELLOW + "God mode disabled for " + targetPlayer.getName() + ".");
        }
    }
}
