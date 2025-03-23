package com.lozaine.playerutilities.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.lozaine.playerutilities.PlayerUtilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStateManager {

    private final PlayerUtilities plugin;
    private final Map<UUID, PlayerState> playerStates;

    public PlayerStateManager(PlayerUtilities plugin) {
        this.plugin = plugin;
        this.playerStates = new HashMap<>();
        loadPlayerStates();
    }

    private void loadPlayerStates() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection playersSection = config.getConfigurationSection("players");

        if (playersSection != null) {
            for (String uuidString : playersSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    ConfigurationSection playerSection = playersSection.getConfigurationSection(uuidString);

                    if (playerSection != null) {
                        boolean flyEnabled = playerSection.getBoolean("fly-enabled", false);
                        float flySpeed = (float) playerSection.getDouble("fly-speed", 0.1);
                        boolean godEnabled = playerSection.getBoolean("god-enabled", false);

                        PlayerState state = new PlayerState(flyEnabled, flySpeed, godEnabled);
                        playerStates.put(uuid, state);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in config: " + uuidString);
                }
            }
        }
    }

    public void saveAllPlayerStates() {
        FileConfiguration config = plugin.getConfig();

        // Clear existing player data
        config.set("players", null);

        // Save current player states
        for (Map.Entry<UUID, PlayerState> entry : playerStates.entrySet()) {
            String path = "players." + entry.getKey().toString();
            PlayerState state = entry.getValue();

            config.set(path + ".fly-enabled", state.isFlyEnabled());
            config.set(path + ".fly-speed", state.getFlySpeed());
            config.set(path + ".god-enabled", state.isGodEnabled());
        }

        plugin.saveConfig();
    }

    public void applyPlayerState(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerState state = playerStates.get(uuid);

        if (state != null) {
            // Apply fly mode if enabled
            if (state.isFlyEnabled()) {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.setFlySpeed(state.getFlySpeed());
            }

            // Apply god mode if enabled
            if (state.isGodEnabled()) {
                player.setInvulnerable(true);
            }
        }
    }

    public void setFlyEnabled(UUID uuid, boolean enabled) {
        PlayerState state = playerStates.computeIfAbsent(uuid, k -> new PlayerState(false, 0.1f, false));
        state.setFlyEnabled(enabled);
    }

    public void setFlySpeed(UUID uuid, float speed) {
        PlayerState state = playerStates.computeIfAbsent(uuid, k -> new PlayerState(false, 0.1f, false));
        state.setFlySpeed(speed);
    }

    public void setGodEnabled(UUID uuid, boolean enabled) {
        PlayerState state = playerStates.computeIfAbsent(uuid, k -> new PlayerState(false, 0.1f, false));
        state.setGodEnabled(enabled);
    }

    public boolean isFlyEnabled(UUID uuid) {
        PlayerState state = playerStates.get(uuid);
        return state != null && state.isFlyEnabled();
    }

    public float getFlySpeed(UUID uuid) {
        PlayerState state = playerStates.get(uuid);
        return state != null ? state.getFlySpeed() : 0.1f;
    }

    public boolean isGodEnabled(UUID uuid) {
        PlayerState state = playerStates.get(uuid);
        return state != null && state.isGodEnabled();
    }

    public static class PlayerState {
        private boolean flyEnabled;
        private float flySpeed;
        private boolean godEnabled;

        public PlayerState(boolean flyEnabled, float flySpeed, boolean godEnabled) {
            this.flyEnabled = flyEnabled;
            this.flySpeed = flySpeed;
            this.godEnabled = godEnabled;
        }

        public boolean isFlyEnabled() {
            return flyEnabled;
        }

        public void setFlyEnabled(boolean flyEnabled) {
            this.flyEnabled = flyEnabled;
        }

        public float getFlySpeed() {
            return flySpeed;
        }

        public void setFlySpeed(float flySpeed) {
            this.flySpeed = flySpeed;
        }

        public boolean isGodEnabled() {
            return godEnabled;
        }

        public void setGodEnabled(boolean godEnabled) {
            this.godEnabled = godEnabled;
        }
    }
}
