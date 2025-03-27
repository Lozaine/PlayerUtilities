package com.lozaine.playerutilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTeleportCommand implements CommandExecutor {

    private final PlayerUtilities plugin;
    private static final int MAX_ATTEMPTS = 10;
    private static final int MAX_RADIUS = 5000;
    private static final int MIN_RADIUS = 500;

    public RandomTeleportCommand(PlayerUtilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage(sender, ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("playerutilities.rtp")) {
            plugin.sendMessage(player, ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        // Teleport to a safe location in the current world
        teleportRandomly(player, player.getWorld());
        return true;
    }

    /**
     * Teleport a player to a safe random location
     */
    private void teleportRandomly(Player player, World world) {
        Random random = ThreadLocalRandom.current();
        Location safeLocation = null;

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            // Generate random coordinates within a reasonable radius
            int x = random.nextInt(MAX_RADIUS * 2) - MAX_RADIUS;
            int z = random.nextInt(MAX_RADIUS * 2) - MAX_RADIUS;

            // Find highest safe location
            Location randomLoc = world.getHighestBlockAt(x, z).getLocation();

            if (isSafeLocation(randomLoc)) {
                safeLocation = randomLoc;
                break;
            }
        }

        if (safeLocation != null) {
            // Teleport player and center them on the block
            safeLocation.add(0.5, 1, 0.5);
            player.teleport(safeLocation);
            plugin.sendMessage(player, ChatColor.GREEN + "Teleported to a random safe location!");
        } else {
            plugin.sendMessage(player, ChatColor.RED + "Could not find a safe location to teleport.");
        }
    }

    /**
     * Check if a location is safe for teleportation
     */
    private boolean isSafeLocation(Location location) {
        Block feet = location.getBlock();
        Block ground = feet.getRelative(0, -1, 0);
        Block above1 = feet.getRelative(0, 1, 0);
        Block above2 = feet.getRelative(0, 2, 0);

        // Check if the location is safe:
        // 1. Ground block is solid
        // 2. Feet block is not solid
        // 3. Block above feet is not solid
        // 4. Block two above feet is not solid
        // 5. Not in water or lava
        return ground.isLiquid()
                && !feet.getType().isSolid()
                && !above1.getType().isSolid()
                && !above2.getType().isSolid()
                && !feet.isLiquid()
                && !above1.isLiquid();
    }
}