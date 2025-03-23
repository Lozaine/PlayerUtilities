package com.lozaine.playerutilities;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlySpeedCommand implements CommandExecutor, TabCompleter {

    private final PlayerUtilities plugin;
    private final List<String> speedOptions = Arrays.asList("0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0");

    public FlySpeedCommand(PlayerUtilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage(sender, ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("playerutilities.flyspeed")) {
            plugin.sendMessage(player, ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length != 1) {
            plugin.sendMessage(player, ChatColor.RED + "Usage: /flyspeed <speed>");
            return false;
        }

        try {
            float speed = Float.parseFloat(args[0]);

            // Ensure the speed is within reasonable bounds (0.1 to 1.0)
            if (speed < 0.1f) {
                speed = 0.1f;
            } else if (speed > 1.0f) {
                speed = 1.0f;
            }

            player.setFlySpeed(speed);
            plugin.savePlayerState(player, "flyspeed", speed);
            plugin.sendMessage(player, ChatColor.GREEN + "Your fly speed has been set to " + speed);

            return true;
        } catch (NumberFormatException e) {
            plugin.sendMessage(player, ChatColor.RED + "Invalid speed! Please enter a number between 0.1 and 1.0");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String partialInput = args[0].toLowerCase();
            return speedOptions.stream()
                    .filter(speed -> speed.startsWith(partialInput))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
