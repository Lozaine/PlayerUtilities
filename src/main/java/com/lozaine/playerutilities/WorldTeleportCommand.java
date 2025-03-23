package com.lozaine.playerutilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldTeleportCommand implements CommandExecutor, Listener {

    private final PlayerUtilities plugin;
    private final Map<String, Inventory> playerInventories = new HashMap<>();

    public WorldTeleportCommand(PlayerUtilities plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage(sender, ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("playerutilities.worldtp")) {
            plugin.sendMessage(player, ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        // Create and open the world teleport GUI
        openWorldTeleportGUI(player);
        return true;
    }

    /**
     * Create and open the world teleport GUI for a player
     */
    private void openWorldTeleportGUI(Player player) {
        List<World> worlds = Bukkit.getWorlds();
        int inventorySize = (worlds.size() / 9 + 1) * 9; // Round up to nearest multiple of 9

        if (inventorySize > 54) {
            inventorySize = 54; // Max inventory size is 54
        }

        Inventory inventory = Bukkit.createInventory(null, inventorySize, ChatColor.DARK_AQUA + "World Teleporter");

        int slot = 0;
        for (World world : worlds) {
            if (slot >= 54) break; // Ensure we don't exceed inventory size

            Material icon = getIconForWorld(world);
            ItemStack item = new ItemStack(icon);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(ChatColor.GREEN + world.getName());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Environment: " + world.getEnvironment().name());

            // Try to get Multiverse alias if available
            String mvAlias = getMultiverseAlias(world);
            if (mvAlias != null && !mvAlias.isEmpty()) {
                lore.add(ChatColor.GRAY + "Alias: " + mvAlias);
            }

            lore.add("");
            lore.add(ChatColor.YELLOW + "Click to teleport to this world");

            meta.setLore(lore);
            item.setItemMeta(meta);

            inventory.setItem(slot, item);
            slot++;
        }

        playerInventories.put(player.getUniqueId().toString(), inventory);
        player.openInventory(inventory);
    }

    /**
     * Get an appropriate icon based on the world type
     */
    private Material getIconForWorld(World world) {
        switch (world.getEnvironment()) {
            case NORMAL:
                return Material.GRASS_BLOCK;
            case NETHER:
                return Material.NETHERRACK;
            case THE_END:
                return Material.END_STONE;
            default:
                return Material.COMPASS;
        }
    }

    /**
     * Try to get the Multiverse alias for a world (if Multiverse is present)
     */
    private String getMultiverseAlias(World world) {
        // Check if Multiverse-Core is present
        if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null) {
            try {
                // This will try to use reflection to get the Multiverse alias
                // But this is simplified - in a real plugin you'd need more robust code
                return ""; // Placeholder - would need proper implementation
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = playerInventories.get(player.getUniqueId().toString());

        if (inventory != null && event.getInventory().equals(inventory)) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            ItemMeta meta = event.getCurrentItem().getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String worldName = ChatColor.stripColor(meta.getDisplayName());
                World destination = Bukkit.getWorld(worldName);

                if (destination != null) {
                    player.closeInventory();
                    player.teleport(destination.getSpawnLocation());
                    plugin.sendMessage(player, ChatColor.GREEN + "Teleported to " + worldName);
                } else {
                    plugin.sendMessage(player, ChatColor.RED + "World not found!");
                }
            }
        }
    }
}
