package com.bahbah.shipautopilot.sable;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Lazy initialization and reflection-based integration with Sable Atmosphere Hooks.
 * Allows the mod to function even if Sable is not installed.
 */
public class SableIntegration {
    private static final Logger LOGGER = LoggerFactory.getLogger("shipautopilot");
    private static boolean isInitialized = false;
    private static boolean isSableAvailable = false;
    private static Object externalForcesApiInstance;
    private static Object lifecycleApiInstance;

    /**
     * Attempt to initialize Sable integration via reflection.
     * If Sable is not available, logs a warning and continues gracefully.
     */
    public static void initialize() {
        if (isInitialized) {
            return;
        }
        isInitialized = true;

        try {
            // Try to load the Sable Atmosphere Hooks class
            Class<?> sableHooksClass = Class.forName(
                    "io.github.plaeyr123.sable_atmosphere_hooks.SableAtmosphereHooks"
            );
            
            // Obtain the API instances
            Method getExternalForcesMethod = sableHooksClass.getMethod("externalForces");
            Method getLifecycleMethod = sableHooksClass.getMethod("lifecycle");
            
            externalForcesApiInstance = getExternalForcesMethod.invoke(null);
            lifecycleApiInstance = getLifecycleMethod.invoke(null);
            
            if (externalForcesApiInstance != null && lifecycleApiInstance != null) {
                isSableAvailable = true;
                LOGGER.info("Sable Atmosphere Hooks successfully loaded");
                LOGGER.info("External Forces API and Lifecycle API are ready");
            } else {
                LOGGER.warn("Sable Atmosphere Hooks found but API instances are null");
            }
        } catch (ClassNotFoundException e) {
            LOGGER.info("Sable Atmosphere Hooks not found on classpath. Ship Autopilot will operate in standalone mode.");
        } catch (NoSuchMethodException e) {
            LOGGER.warn("Sable Atmosphere Hooks found but expected methods not present. Version mismatch?", e);
        } catch (Exception e) {
            LOGGER.warn("Error initializing Sable integration", e);
        }
    }

    /**
     * Check if Sable is available and ready.
     */
    public static boolean isSableAvailable() {
        return isSableAvailable;
    }

    /**
     * Get the external forces API instance, or null if Sable is not available.
     */
    public static Object getExternalForcesApi() {
        return externalForcesApiInstance;
    }

    /**
     * Get the lifecycle API instance, or null if Sable is not available.
     */
    public static Object getLifecycleApi() {
        return lifecycleApiInstance;
    }

    /**
     * Get the resource location for this mod.
     */
    public static ResourceLocation getModLocation() {
        return ResourceLocation.fromNamespaceAndPath("shipautopilot", "autopilot");
    }
}
