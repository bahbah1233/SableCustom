package com.bahbah.shipautopilot.sable;

import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

/**
 * Detects which Sable sublevel a player is currently standing on.
 * Uses reflection to call Sable Atmosphere Hooks APIs.
 */
public class SableSubLevelDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger("shipautopilot");
    private static final double DETECTION_RANGE = 1.0; // Check within 1 block radius

    // Cached method references for performance
    private static Method getPhysicsViewMethod;
    private static Method getComMethod;  // center of mass
    private static Method getWorldAABBMethod;
    private static Object cachedLifecycleApi;

    /**
     * Detect if a player is standing on a loaded Sable sublevel.
     * Returns the UUID of the sublevel, or null if not on one.
     */
    public static UUID detectSubLevelUnderPlayer(Player player) {
        if (player == null || player.level() == null) {
            return null;
        }

        if (!SableIntegration.isSableAvailable()) {
            return null; // Sable not available
        }

        try {
            // Get the external forces API
            Object api = SableIntegration.getExternalForcesApi();
            if (api == null) {
                return null;
            }

            // Try to iterate through known sublevels and check collision
            // This is a placeholder - in a real implementation, you would query
            // the Sable container observer for all loaded sublevels
            return detectViaPhysicsView(player, api);

        } catch (Exception e) {
            LOGGER.debug("Error detecting sublevel under player", e);
            return null;
        }
    }

    /**
     * Attempt detection by checking physics views of known sublevels.
     */
    private static UUID detectViaPhysicsView(Player player, Object api) {
        try {
            // Get the getPhysicsView method if not cached
            if (getPhysicsViewMethod == null) {
                getPhysicsViewMethod = api.getClass().getMethod("getPhysicsView", java.util.UUID.class);
            }

            // TODO: We need a way to iterate through all loaded sublevels.
            // Ideally, Sable would expose a method to get all loaded UUIDs.
            // For now, this is a limitation of the current Sable API.
            // Workaround: Store discovered sublevels in a local cache

            return null; // Unable to detect without Sable's container observer access

        } catch (Exception e) {
            LOGGER.debug("Error in physics view detection", e);
            return null;
        }
    }

    /**
     * Check if a point is within a sublevel's AABB.
     */
    private static boolean isPointInAABB(double x, double y, double z, Object physicsView) {
        try {
            if (getWorldAABBMethod == null) {
                getWorldAABBMethod = physicsView.getClass().getMethod("getWorldAABB");
            }

            Object aabb = getWorldAABBMethod.invoke(physicsView);
            if (aabb == null) {
                return false;
            }

            // Reflection check against AABB
            Method containsMethod = aabb.getClass().getMethod("contains", double.class, double.class, double.class);
            return (boolean) containsMethod.invoke(aabb, x, y, z);

        } catch (Exception e) {
            LOGGER.debug("Error checking AABB containment", e);
            return false;
        }
    }
}
