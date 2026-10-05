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
  event/
    CommandRegistrationHandler.java    Registers commands on server start
    ServerTickHandler.java             Per-tick autopilot physics updates
```

### Key Classes

- **`AutopilotManager`**: Manages state for all active sublevels, handles player-to-sublevel mapping, and provides the public API.
- **`SubLevelAutopilotState`**: Tracks waypoints, running status, and current navigation direction for one sublevel.
- **`AutopilotPhysicsController`**: Applies propulsion forces via the Sable Atmosphere Hooks external forces API.
- **`ShipAutopilotCommand`**: Parses and executes the five commands.

### Integration with Sable

This mod **currently requires manual setup** to fully integrate with Sable sublevels:

1. **Sublevel Detection**: `AutopilotManager.getSubLevelUnderPlayer()` must be wired to query active Sable sublevels. Use `SableAtmosphereHooks` lifecycle API to detect sublevel boundaries.
2. **Force Application**: `AutopilotPhysicsController.applyForceToSubLevel()` must call `SableExternalForcesApi.applyForceAtCenterOfMass()` to apply propulsion.
3. **Physics Updates**: Autopilot logic runs on server tick; force accumulation happens in Sable's pre-physics phase.

## Dependencies

- **Minecraft**: 1.21.1  
- **NeoForge**: 21.1.252  
- **Java**: 21  
- **Sable** (optional): 2.0.3+  
- **Sable Atmosphere Hooks** (optional): 0.1.0-alpha.1+

## Building

```bash
./gradlew clean build
```

Output JAR: `build/libs/shipautopilot-1.0.0.jar`

## License

All Rights Reserved. See `LICENSE` or repository for details.

## Roadmap

- [ ] Full Sable Atmosphere Hooks integration (detect sublevels, apply forces)
- [ ] Per-player autopilot profiles
- [ ] Speed / thrust configuration
- [ ] Waypoint persistence across server restarts
- [ ] Advanced targeting (follow player, orbit, etc.)
- [ ] Client-side visualization of waypoints

## Support

For issues, questions, or contributions, visit the [GitHub repository](https://github.com/bahbah1233/SableCustom).
