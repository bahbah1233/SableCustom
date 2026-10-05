package com.bahbah.shipautopilot.core;

import com.bahbah.shipautopilot.sable.SableIntegration;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.UUID;

/**
 * Applies propulsion forces to sublevels based on autopilot targets.
 * Integrates with Sable's external forces API via reflection.
 */
public class AutopilotPhysicsController {
    private static final Logger LOGGER = LoggerFactory.getLogger("shipautopilot");
    private static final double AUTOPILOT_SPEED = 0.15; // Force magnitude per tick in Newtons
    private static final double WAYPOINT_PROXIMITY = 2.0; // Distance to consider waypoint reached
    private static final double MAX_SPEED = 1.0; // Maximum velocity (m/s)

    // Cached method references for performance
    private static Method applyForceAtCenterOfMassMethod;
    private static Method isReadyMethod;
    private static Method getPhysicsViewMethod;

    /**
     * Update physics for all active autopilot sublevels.
     * Called once per server tick via event handler.
     */
    public static void updateAllAutopilots() {
        if (!SableIntegration.isSableAvailable()) {
            // Sable not available, skip physics updates
            return;
        }

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
        try {
            Object externalForcesApi = SableIntegration.getExternalForcesApi();
            if (externalForcesApi == null) {
                return;
            }

            // Check if the sublevel is ready for force application
            if (!isSubLevelReady(externalForcesApi, state.subLevelId)) {
                LOGGER.debug("Sublevel {} not ready for forces", state.subLevelId);
                return;
            }

            // Get the physics view to read current position
            Object physicsView = getSubLevelPhysicsView(externalForcesApi, state.subLevelId);
            if (physicsView == null) {
                return;
            }

            // Extract center of mass position from physics view
            Vec3 currentCom = extractCenterOfMass(physicsView);
            if (currentCom == null) {
                return;
            }

            WaypointData currentTarget = state.getCurrentTarget();
            if (currentTarget == null) {
                return;
            }

            // Calculate direction vector from current position to target
            Vec3 direction = new Vec3(
                    currentTarget.x - currentCom.x,
                    currentTarget.y - currentCom.y,
                    currentTarget.z - currentCom.z
            );

            double distance = direction.length();

            // Check if waypoint reached
            if (distance < WAYPOINT_PROXIMITY) {
                state.toggleDirection();
                LOGGER.info("Sublevel {} reached waypoint, switching direction", state.subLevelId);
                return;
            }

            // Normalize and apply speed
            if (distance > 0.1) { // Avoid division by very small numbers
                Vec3 force = direction.normalize().scale(AUTOPILOT_SPEED);
                applyForceToSubLevel(externalForcesApi, state.subLevelId, force);
                LOGGER.debug("Applied force {} to sublevel {}, distance to target: {}", 
                        force, state.subLevelId, distance);
            }
        } catch (Exception e) {
            LOGGER.error("Error updating autopilot for sublevel {}", state.subLevelId, e);
        }
    }

    /**
     * Check if a sublevel is ready for force application.
     */
    private static boolean isSubLevelReady(Object externalForcesApi, UUID subLevelId) {
        try {
            if (isReadyMethod == null) {
                isReadyMethod = externalForcesApi.getClass().getMethod("isReady", UUID.class);
            }
            return (boolean) isReadyMethod.invoke(externalForcesApi, subLevelId);
        } catch (Exception e) {
            LOGGER.debug("Error checking sublevel readiness", e);
            return false;
        }
    }

    /**
     * Get the physics view for a sublevel.
     */
    private static Object getSubLevelPhysicsView(Object externalForcesApi, UUID subLevelId) {
        try {
            if (getPhysicsViewMethod == null) {
                getPhysicsViewMethod = externalForcesApi.getClass().getMethod("getPhysicsView", UUID.class);
            }
            Object optionalView = getPhysicsViewMethod.invoke(externalForcesApi, subLevelId);
            
            // Handle Optional<SubLevelPhysicsView>
            if (optionalView != null) {
                Method getMethod = optionalView.getClass().getMethod("get");
                try {
                    return getMethod.invoke(optionalView);
                } catch (Exception e) {
                    return null; // Optional was empty
                }
            }
            return null;
        } catch (Exception e) {
            LOGGER.debug("Error getting physics view", e);
            return null;
        }
    }

    /**
     * Extract the center of mass position from a physics view.
     */
    private static Vec3 extractCenterOfMass(Object physicsView) {
        try {
            Method getComMethod = physicsView.getClass().getMethod("getCenterOfMass");
            Object comObject = getComMethod.invoke(physicsView);
            
            if (comObject instanceof Vec3) {
                return (Vec3) comObject;
            }
            // If it's a different type (e.g., Vec3d), convert it
            if (comObject != null) {
                Method getXMethod = comObject.getClass().getMethod("x");
                Method getYMethod = comObject.getClass().getMethod("y");
                Method getZMethod = comObject.getClass().getMethod("z");
                
                double x = ((Number) getXMethod.invoke(comObject)).doubleValue();
                double y = ((Number) getYMethod.invoke(comObject)).doubleValue();
                double z = ((Number) getZMethod.invoke(comObject)).doubleValue();
                
                return new Vec3(x, y, z);
            }
            return null;
        } catch (Exception e) {
            LOGGER.debug("Error extracting center of mass", e);
            return null;
        }
    }

    /**
     * Apply a force to a sublevel via the Sable API.
     * Calls SableExternalForcesApi.applyForceAtCenterOfMass().
     */
    private static void applyForceToSubLevel(Object externalForcesApi, UUID subLevelId, Vec3 force) {
        try {
            if (applyForceAtCenterOfMassMethod == null) {
                applyForceAtCenterOfMassMethod = externalForcesApi.getClass().getMethod(
                        "applyForceAtCenterOfMass",
                        UUID.class,
                        Vec3.class
                );
            }
            
            Object result = applyForceAtCenterOfMassMethod.invoke(
                    externalForcesApi,
                    subLevelId,
                    force
            );
            
            // Check the ForceApplicationResult
            if (result != null) {
                String resultString = result.toString();
                if (!resultString.contains("QUEUED")) {
                    LOGGER.warn("Force application returned: {}", resultString);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error applying force to sublevel {}", subLevelId, e);
        }
    }
}
