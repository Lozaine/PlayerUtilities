package com.lozaine.playerutilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlyCommand implements CommandExecutor, TabCompleter {

    private final PlayerUtilities plugin;

    public FlyCommand(PlayerUtilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Handle self
        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("playerutilities.fly")) {
                plugin.sendMessage(player, ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }

            toggleFly(player);
            return true;
        }
        // Handle targeting other player
        else if (args.length == 1) {
            if (!sender.hasPermission("playerutilities.fly.others")) {
                plugin.sendMessage(sender, ChatColor.RED + "You don't have permission to toggle fly for others!");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                plugin.sendMessage(sender, ChatColor.RED + "Player not found!");
                return true;
            }

            toggleFly(target);
            plugin.sendMessage(sender, ChatColor.GREEN + "Toggled fly mode for " + target.getName());
            return true;
        }

        // Incorrect usage
        plugin.sendMessage(sender, ChatColor.RED + "Usage: /fly [player]");
        return false;
    }

    private void toggleFly(Player player) {
        boolean newFlyState = !player.getAllowFlight();

        player.setAllowFlight(newFlyState);
        if (newFlyState) {
            player.setFlying(true);
            plugin.sendMessage(player, ChatColor.GREEN + "Fly mode enabled!");
        } else {
            plugin.sendMessage(player, ChatColor.YELLOW + "Fly mode disabled!");
        }

        // Save the player's fly state for automatic enabling on login
        plugin.savePlayerState(player, "fly", newFlyState);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("playerutilities.fly.others")) {
            String partialName = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}