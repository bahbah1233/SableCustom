package com.bahbah.shipautopilot.core;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central manager for sublevel autopilot state and detection.
 * Handles waypoint storage, autopilot state, and sublevel discovery.
 */
public class AutopilotManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("shipautopilot");
    private static final double SUBLEVEL_DETECTION_RANGE = 0.5; // Half a block
    private static final double AUTOPILOT_SPEED = 0.15; // m/s thrust per tick
    private static final double WAYPOINT_PROXIMITY = 2.0; // Distance to consider waypoint reached

    private static final Map<UUID, SubLevelAutopilotState> autopilotStates = new ConcurrentHashMap<>();
    private static final Map<String, UUID> playerToSubLevel = new ConcurrentHashMap<>(); // Track which player is on which sublevel

    /**
     * Detects which sublevel (if any) a player is standing on.
     * Returns the UUID of the detected sublevel, or null if not on one.
     */
    public static UUID getSubLevelUnderPlayer(Player player) {
        if (player == null || player.level() == null) {
            return null;
        }

        // TODO: Integrate with actual Sable API to detect sublevel under player
        // For now, this is a placeholder that can be extended once Sable's
        // collision detection API is available.
        // The Sable Atmosphere Hooks would provide access to loaded SubLevels
        // via SubLevelLifecycleApi.getLoadedSubLevels() or similar.

        LOGGER.debug("Checking for sublevel under player: {}", player.getName().getString());
        return playerToSubLevel.get(player.getUUID().toString());
    }

    /**
     * Manually register a player as being on a sublevel (for testing/debugging).
     */
    public static void setPlayerOnSubLevel(Player player, UUID subLevelId) {
        playerToSubLevel.put(player.getUUID().toString(), subLevelId);
        ensureAutopilotState(subLevelId);
        LOGGER.info("Player {} is now on sublevel {}", player.getName().getString(), subLevelId);
    }

    /**
     * Remove player from sublevel tracking.
     */
    public static void removePlayerFromSubLevel(Player player) {
        playerToSubLevel.remove(player.getUUID().toString());
    }

    /**
     * Set waypoint A for a sublevel.
     */
    public static void setWaypointA(UUID subLevelId, WaypointData waypoint) {
        SubLevelAutopilotState state = ensureAutopilotState(subLevelId);
        state.waypointA = waypoint;
        LOGGER.info("Set waypoint A for sublevel {}: {}", subLevelId, waypoint);
    }

    /**
     * Set waypoint B for a sublevel.
     */
    public static void setWaypointB(UUID subLevelId, WaypointData waypoint) {
        SubLevelAutopilotState state = ensureAutopilotState(subLevelId);
        state.waypointB = waypoint;
        LOGGER.info("Set waypoint B for sublevel {}: {}", subLevelId, waypoint);
    }

    /**
     * Check if a sublevel has both waypoints set.
     */
    public static boolean hasWaypoints(UUID subLevelId) {
        SubLevelAutopilotState state = autopilotStates.get(subLevelId);
        return state != null && state.hasWaypoints();
    }

    /**
     * Start autopilot for a sublevel.
     */
    public static void startAutopilot(UUID subLevelId) {
        SubLevelAutopilotState state = ensureAutopilotState(subLevelId);
        if (!state.hasWaypoints()) {
            throw new IllegalStateException("Cannot start autopilot without waypoints A and B");
        }
        state.isRunning = true;
        state.headingToB = true;
        state.lastUpdateTick = 0;
        LOGGER.info("Started autopilot for sublevel {}", subLevelId);
    }

    /**
     * Stop autopilot for a sublevel.
     */
    public static void stopAutopilot(UUID subLevelId) {
        SubLevelAutopilotState state = autopilotStates.get(subLevelId);
        if (state != null) {
            state.isRunning = false;
            LOGGER.info("Stopped autopilot for sublevel {}", subLevelId);
        }
    }

    /**
     * Get formatted status string for a sublevel.
     */
    public static String getStatus(UUID subLevelId) {
        SubLevelAutopilotState state = autopilotStates.get(subLevelId);
        if (state == null) {
            return "[Ship Autopilot] No autopilot state for this sublevel";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[Ship Autopilot Status]\n");
        sb.append("Running: ").append(state.isRunning ? "YES" : "NO").append("\n");
        sb.append("Waypoint A: ").append(state.waypointA == null ? "UNSET" : formatWaypoint(state.waypointA)).append("\n");
        sb.append("Waypoint B: ").append(state.waypointB == null ? "UNSET" : formatWaypoint(state.waypointB)).append("\n");
        if (state.isRunning && state.hasWaypoints()) {
            WaypointData current = state.getCurrentTarget();
            sb.append("Current Target: ").append(formatWaypoint(current)).append("\n");
            if (state.waypointA != null) {
                double distToA = state.waypointA.distanceTo(current);
                sb.append("Distance to Target: ").append(String.format("%.1f blocks", distToA));
            }
        }
        return sb.toString();
    }

    /**
     * Get the current autopilot state for a sublevel.
     */
    public static SubLevelAutopilotState getState(UUID subLevelId) {
        return autopilotStates.get(subLevelId);
    }

    /**
     * Get all active autopilot states (for tick updates).
     */
    public static Collection<SubLevelAutopilotState> getActiveStates() {
        return autopilotStates.values();
    }

    /**
     * Clear all autopilot states (useful for testing or shutdown).
     */
    public static void clearAllStates() {
        autopilotStates.clear();
        playerToSubLevel.clear();
    }

    // Private helper methods

    private static SubLevelAutopilotState ensureAutopilotState(UUID subLevelId) {
        return autopilotStates.computeIfAbsent(subLevelId, SubLevelAutopilotState::new);
    }

    private static String formatWaypoint(WaypointData waypoint) {
        return String.format("(%.1f, %.1f, %.1f)", waypoint.x, waypoint.y, waypoint.z);
    }
}
