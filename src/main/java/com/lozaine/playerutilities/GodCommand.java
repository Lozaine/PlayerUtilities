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

public class GodCommand implements CommandExecutor, TabCompleter {

    private final PlayerUtilities plugin;

    public GodCommand(PlayerUtilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Handle self
        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("playerutilities.god")) {
                plugin.sendMessage(player, ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }

            toggleGodMode(player);
            return true;
        }
        // Handle targeting other player
        else if (args.length == 1) {
            if (!sender.hasPermission("playerutilities.god.others")) {
                plugin.sendMessage(sender, ChatColor.RED + "You don't have permission to toggle god mode for others!");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                plugin.sendMessage(sender, ChatColor.RED + "Player not found!");
                return true;
            }

            toggleGodMode(target);
            plugin.sendMessage(sender, ChatColor.GREEN + "Toggled god mode for " + target.getName());
            return true;
        }

        // Incorrect usage
        plugin.sendMessage(sender, ChatColor.RED + "Usage: /god [player]");
        return false;
    }

    private void toggleGodMode(Player player) {
        boolean currentGodMode = player.isInvulnerable();
        boolean newGodMode = !currentGodMode;

        if (newGodMode) {
            plugin.enableGodMode(player);
            plugin.sendMessage(player, ChatColor.GREEN + "God mode enabled!");
        } else {
            plugin.disableGodMode(player);
            plugin.sendMessage(player, ChatColor.YELLOW + "God mode disabled!");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("playerutilities.god.others")) {
            String partialName = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}