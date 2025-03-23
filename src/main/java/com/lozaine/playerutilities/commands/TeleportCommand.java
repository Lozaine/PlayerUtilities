package com.lozaine.playerutilities.commands;

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
import org.bukkit.plugin.Plugin;

import com.lozaine.playerutilities.PlayerUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TeleportCommand implements CommandExecutor, Listener {

    private final PlayerUtilities plugin;
    private final Map<UUID, Inventory> openGUIs = new HashMap<>();
    private final String guiTitle = ChatColor.DARK_BLUE + "World Teleporter";

    // Material mappings for different world types
    private final Map<String, Material> worldMaterials = new HashMap<>();

    public TeleportCommand(PlayerUtilities plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        // Initialize world material mappings
        initWorldMaterials();
    }

    private void initWorldMaterials() {
        // Default world types
        worldMaterials.put("normal", Material.GRASS_BLOCK);
        worldMaterials.put("nether", Material.NETHERRACK);
        worldMaterials.put("the_end", Material.END_STONE);

        // Additional world types based on name patterns
        worldMaterials.put("flat", Material.BEDROCK);
        worldMaterials.put("void", Material.GLASS);
        worldMaterials.put("amplified", Material.STONE);
        worldMaterials.put("water", Material.WATER_BUCKET);
        worldMaterials.put("island", Material.SAND);
        worldMaterials.put("sky", Material.BLUE_WOOL);
        worldMaterials.put("cave", Material.COBBLESTONE);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("playerutilities.teleport")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        // Check if Multiverse-Core is present
        if (!isMultiverseEnabled()) {
            player.sendMessage(ChatColor.RED + "Multiverse-Core is not installed or enabled. Cannot teleport between worlds.");
            return true;
        }

        // Open GUI with available worlds
        openWorldSelectionGUI(player);
        return true;
    }

    private boolean isMultiverseEnabled() {
        Plugin multiversePlugin = Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        return multiversePlugin != null && multiversePlugin.isEnabled();
    }

    private void openWorldSelectionGUI(Player player) {
        List<World> worlds = Bukkit.getWorlds();

        // Calculate inventory size (multiple of 9, at least big enough for all worlds)
        int invSize = ((worlds.size() / 9) + 1) * 9;
        invSize = Math.min(invSize, 54); // Maximum chest size is 54 slots

        Inventory inv = Bukkit.createInventory(null, invSize, guiTitle);

        // Add world items to inventory
        int slot = 0;
        for (World world : worlds) {
            if (slot >= invSize) break; // Don't exceed inventory size

            ItemStack worldItem = createWorldItem(world);
            inv.setItem(slot++, worldItem);
        }

        player.openInventory(inv);
        openGUIs.put(player.getUniqueId(), inv);
    }

    private ItemStack createWorldItem(World world) {
        // Choose a material based on world type or name
        Material material = getMaterialForWorld(world);

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + world.getName());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Environment: " + world.getEnvironment().name());

            // Show player count in this world
            int playerCount = world.getPlayers().size();
            lore.add(ChatColor.GRAY + "Players: " + ChatColor.WHITE + playerCount);

            // Show if it's the player's current world
            if (world.equals(Bukkit.getWorlds().get(0))) {
                lore.add(ChatColor.YELLOW + "Default World");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private Material getMaterialForWorld(World world) {
        String worldName = world.getName().toLowerCase();
        Material material = null;

        // Check by environment type first
        switch (world.getEnvironment()) {
            case NORMAL:
                material = worldMaterials.get("normal");
                break;
            case NETHER:
                material = worldMaterials.get("nether");
                break;
            case THE_END:
                material = worldMaterials.get("the_end");
                break;
        }

        // If still not assigned, check by name patterns
        if (material == null) {
            for (Map.Entry<String, Material> entry : worldMaterials.entrySet()) {
                if (worldName.contains(entry.getKey())) {
                    material = entry.getValue();
                    break;
                }
            }
        }

        // Default fallback if no match found
        if (material == null) {
            material = Material.COMPASS;
        }

        return material;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory openInventory = event.getInventory();

        // Check if this is our GUI
        if (!openInventory.equals(openGUIs.get(player.getUniqueId()))) return;
        if (!event.getView().getTitle().equals(guiTitle)) return;

        event.setCancelled(true); // Prevent taking the item

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        // Extract world name from the item's display name
        String worldName = ChatColor.stripColor(meta.getDisplayName());
        World targetWorld = Bukkit.getWorld(worldName);

        if (targetWorld != null) {
            // Close inventory first
            player.closeInventory();

            // Safe teleport to world spawn
            player.sendMessage(ChatColor.GREEN + "Teleporting to " + worldName + "...");
            targetWorld.getChunkAt(targetWorld.getSpawnLocation()).thenRun(() -> {
                player.teleport(targetWorld.getSpawnLocation());
                player.sendMessage(ChatColor.GREEN + "Teleported to " + worldName + "!");
            });
        } else {
            player.sendMessage(ChatColor.RED + "Error: World '" + worldName + "' could not be found!");
        }
    }
}
