# PlayerUtilities Minecraft Plugin

## Description
PlayerUtilities is a comprehensive Bukkit/Spigot plugin that provides essential player management features for Minecraft servers. The plugin offers convenient commands to enhance player mobility, administrative control, and quality of life improvements.

## Features
- **Fly Command** (`/fly`)
  - Toggle flight mode for yourself or other players
  - Persistent fly mode across server restarts
  - Configurable permissions

- **Fly Speed Command** (`/flyspeed`)
  - Customize your flying speed
  - Range from 0.1 to 1.0
  - Saves speed preference

- **God Mode Command** (`/god`)
  - Toggle invulnerability for yourself or other players
  - Persistent god mode across server restarts

- **World Teleport Command** (`/worldtp`)
  - Interactive GUI for world teleportation
  - Supports multiple worlds
  - Visually distinguishes world types

- **Random Teleport Command** (`/rtp`)
  - Teleport to a random safe location
  - Intelligent location selection
  - Prevents teleporting into dangerous areas
  - Configurable teleport radius

## Commands
- `/fly [player]` - Toggle fly mode
- `/flyspeed <speed>` - Set fly speed (0.1-1.0)
- `/worldtp` - Open world teleport GUI
- `/god [player]` - Toggle god mode
- `/rtp` - Teleport to a random safe location

## Permissions
- `playerutilities.fly` - Use fly command on self
- `playerutilities.fly.others` - Use fly command on others
- `playerutilities.flyspeed` - Change fly speed
- `playerutilities.worldtp` - Use world teleport
- `playerutilities.god` - Use god mode on self
- `playerutilities.god.others` - Use god mode on others
- `playerutilities.rtp` - Use random teleport
- `playerutilities.admin` - All permissions

## Configuration
The plugin uses a `config.yml` to store:
- Message prefixes
- Default player settings
- Persistent player states
- Customizable RTP settings

## Installation
1. Download the plugin JAR
2. Place in your server's `plugins` directory
3. Restart or reload the server
4. Configure permissions as needed

## Compatibility
- Minecraft: 1.21
- Spigot: 1.21.5-R0.1-SNAPSHOT
- Java: 21

## Building from Source
1. Ensure you have Java 21 and Gradle installed
2. Clone the repository
3. Run `gradle build`
4. Find the compiled JAR in `build/libs/`

## Contributing
Contributions are welcome! Please submit pull requests or open issues on the project repository.

## Support
For issues or feature requests, please open a GitHub issue.

## License
This project is licensed under the MIT License.
