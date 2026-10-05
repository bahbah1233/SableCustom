# Ship Autopilot

A powerful autopilot system for **Sable sublevels** in Minecraft 1.21.1 (NeoForge).

## Features

This mod enables players to set two spatial waypoints and automatically move a Sable sublevel back and forth between them using physics forces.

### Commands

- **`/shipautopilot setA`**  
  Save the player's current position as waypoint A.

- **`/shipautopilot setB`**  
  Save the player's current position as waypoint B.

- **`/shipautopilot start`**  
  Begin automated movement, cycling A → B → A → ...

- **`/shipautopilot stop`**  
  Cease applying propulsion and freeze the sublevel.

- **`/shipautopilot status`**  
  Display whether autopilot is running, current target, and saved waypoint coordinates.

## Installation

### Standalone Mode (No Sable)

Ship Autopilot can run standalone and store waypoints even without Sable installed. Commands will work, but autopilot propulsion requires Sable.

1. Download the latest `shipautopilot-1.0.0.jar` from the releases page
2. Place it in your mods folder
3. Launch Minecraft

### With Sable Integration

For full autopilot functionality with physics forces:

1. Install **Sable** (download from [Modrinth](https://modrinth.com/mod/sable/))
2. Install **Sable Companion** (download from [Modrinth](https://modrinth.com/mod/sable-companion/))
3. Place Ship Autopilot in your mods folder
4. Launch Minecraft

Ship Autopilot will automatically detect Sable at startup and integrate with its physics API.

## Usage

1. **Create a Sable sublevel** in creative mode.
2. **Stand on the sublevel** and execute `/shipautopilot setA` at your starting position.
3. **Move to the destination** and execute `/shipautopilot setB`.
4. **Start autopilot** with `/shipautopilot start`.
5. **Monitor or stop** with `/shipautopilot status` or `/shipautopilot stop`.

## Technical Details

### Architecture

```
src/main/java/com/bahbah/shipautopilot/
  ShipAutopilot.java                    Main mod entry point
  command/
    ShipAutopilotCommand.java          Command dispatcher and handlers
  core/
    AutopilotManager.java              Central state and sublevel detection
    SubLevelAutopilotState.java        Per-sublevel autopilot state
    WaypointData.java                  Spatial waypoint representation
    AutopilotPhysicsController.java    Force application and physics updates
  sable/
    SableIntegration.java              Lazy init and reflection-based Sable detection
    SableSubLevelDetector.java         Sublevel detection via Sable API
  event/
    CommandRegistrationHandler.java    Registers commands on server start
    ServerTickHandler.java             Per-tick autopilot physics updates
```

### Key Classes

- **`AutopilotManager`**: Manages state for all active sublevels, handles player-to-sublevel mapping, and provides the public API.
- **`SubLevelAutopilotState`**: Tracks waypoints, running status, and current navigation direction for one sublevel.
- **`AutopilotPhysicsController`**: Applies propulsion forces via reflection to Sable's external forces API.
- **`SableIntegration`**: Graceful fallback using reflection; mod runs even if Sable is absent.
- **`ShipAutopilotCommand`**: Parses and executes the five commands.

### How Sable Integration Works

Ship Autopilot uses **reflection** to detect and interact with Sable at runtime:

1. On mod load, `SableIntegration.initialize()` attempts to load the Sable Atmosphere Hooks class.
2. If found, the mod caches references to the external forces API and lifecycle API.
3. If not found, the mod logs a warning and continues in standalone mode.
4. Per-tick, `AutopilotPhysicsController` checks if Sable is available before applying forces.
5. If Sable APIs are called but fail, the mod catches exceptions gracefully.

This design ensures the mod is always buildable and runnable, even without Sable present.

## Dependencies

- **Minecraft**: 1.21.1  
- **NeoForge**: 21.1.252  
- **Java**: 21  
- **Sable** (optional): 2.0.6+ — [download from Modrinth](https://modrinth.com/mod/sable/)
- **Sable Companion** (optional): 1.6.0+ — [download from Modrinth](https://modrinth.com/mod/sable-companion/)

## Building

### Prerequisites

- Java 21 or higher
- Gradle (uses included wrapper)

### Build Command

```bash
./gradlew clean build
```

Output JAR: `build/libs/shipautopilot-1.0.0.jar`

The JAR has no hard dependencies on Sable, so it will build successfully even if Sable is not in your local Maven cache.

## Configuration for Developers

If you want to **build against Sable locally** (for testing full integration):

1. Clone and build Sable:
   ```bash
   git clone https://github.com/ryanhcode/sable.git
   cd sable
   ./gradlew build
   ```
   This installs Sable artifacts to your local Maven repository (`~/.m2/repository/`).

2. Uncomment the Sable dependency in Ship Autopilot's `build.gradle`:
   ```gradle
   repositories {
       mavenLocal()  // Add this line
   }
   ```

3. Rebuild Ship Autopilot:
   ```bash
   ./gradlew clean build
   ```

For normal users, this is **not required** — Sable is installed as a separate mod.

## License

All Rights Reserved. See `LICENSE` or repository for details.

## Roadmap

- [ ] Improved Sable sublevel detection (currently manual registration)
- [ ] Per-player autopilot profiles
- [ ] Speed / thrust configuration
- [ ] Waypoint persistence across server restarts
- [ ] Advanced targeting (follow player, orbit, etc.)
- [ ] Client-side visualization of waypoints

## Support

For issues, questions, or contributions, visit the [GitHub repository](https://github.com/bahbah1233/SableCustom).

## Related Projects

- [Sable](https://github.com/ryanhcode/sable/) — The core physics mod for moving sublevels
- [Sable Companion](https://github.com/ryanhcode/sable-companion/) — Compatibility library for Sable integration
