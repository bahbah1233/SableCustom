package com.bahbah.shipautopilot.core;

import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.UUID;

/**
 * Applies propulsion forces to sublevels based on autopilot targets.
 * Integrates with Sable's external forces API.
 */
public class AutopilotPhysicsController {
    private static final Logger LOGGER = LoggerFactory.getLogger("shipautopilot");
    private static final double AUTOPILOT_SPEED = 0.15; // Force magnitude per tick
    private static final double WAYPOINT_PROXIMITY = 2.0; // Distance to consider waypoint reached
    private static final double ORIENTATION_TOLERANCE = 0.5; // Radians

    /**
     * Update physics for all active autopilot sublevels.
     * Called once per server tick via event handler.
     */
    public static void updateAllAutopilots() {
        Collection<SubLevelAutopilotState> states = AutopilotManager.getActiveStates();
        for (SubLevelAutopilotState state : states) {
            if (state.isRunning && state.hasWaypoints()) {
                updateAutopilot(state);
            }
        }
    }

    /**
     * Apply propulsion to move a sublevel towards its current waypoint target.
     */
    private static void updateAutopilot(SubLevelAutopilotState state) {
        // TODO: Integrate with Sable Atmosphere Hooks API
        // The actual implementation would:
        // 1. Get the current physics view from SableExternalForcesApi.getPhysicsView()
        // 2. Calculate direction vector from current position to target
        // 3. Apply force at center of mass using applyForceAtCenterOfMass()
        // 4. Detect waypoint arrival and switch targets

        WaypointData currentTarget = state.getCurrentTarget();
        if (currentTarget == null) {
            return;
        }

        // Placeholder logic for direction calculation
        // In actual implementation, would use Sable's physics view for COM position
        Vec3 direction = new Vec3(
                currentTarget.x - 0, // Would be sublevel's current x
                currentTarget.y - 0, // Would be sublevel's current y
                currentTarget.z - 0  // Would be sublevel's current z
        );

        double distance = direction.length();

        // Check if waypoint reached
        if (distance < WAYPOINT_PROXIMITY) {
            state.toggleDirection();
            LOGGER.info("Sublevel {} reached waypoint, switching direction", state.subLevelId);
            return;
        }

        // Normalize and apply speed
        if (distance > 0) {
            Vec3 force = direction.normalize().scale(AUTOPILOT_SPEED);
            applyForceToSubLevel(state.subLevelId, force);
        }
    }

    /**
     * Apply a force to a sublevel via the Sable API.
     * Calls SableExternalForcesApi.applyForceAtCenterOfMass().
     */
    private static void applyForceToSubLevel(UUID subLevelId, Vec3 force) {
        try {
            // TODO: Obtain SableExternalForcesApi instance
            // SableExternalForcesApi api = SableAtmosphereHooks.externalForces();
            // ForceApplicationResult result = api.applyForceAtCenterOfMass(subLevelId, force);
            // if (result != ForceApplicationResult.QUEUED) {
            //     LOGGER.warn("Failed to apply force to sublevel {}: {}", subLevelId, result);
            // }

            LOGGER.debug("Applying force {} to sublevel {}", force, subLevelId);
        } catch (Exception e) {
            LOGGER.error("Error applying force to sublevel {}", subLevelId, e);
        }
    }
}
