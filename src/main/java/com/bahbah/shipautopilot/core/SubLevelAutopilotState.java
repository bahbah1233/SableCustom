package com.bahbah.shipautopilot.core;

import java.util.UUID;

/**
 * Tracks the autopilot state for a single sublevel.
 */
public class SubLevelAutopilotState {
    public final UUID subLevelId;
    public WaypointData waypointA;
    public WaypointData waypointB;
    public boolean isRunning = false;
    public boolean headingToB = true; // true = moving towards B, false = moving towards A
    public long lastUpdateTick = 0;

    public SubLevelAutopilotState(UUID subLevelId) {
        this.subLevelId = subLevelId;
    }

    public boolean hasWaypoints() {
        return waypointA != null && waypointB != null;
    }

    public WaypointData getCurrentTarget() {
        return headingToB ? waypointB : waypointA;
    }

    public void toggleDirection() {
        headingToB = !headingToB;
    }

    @Override
    public String toString() {
        return String.format(
                "SubLevelAutopilot[id=%s, running=%s, A=%s, B=%s, target=%s]",
                subLevelId.toString().substring(0, 8),
                isRunning,
                waypointA == null ? "unset" : waypointA,
                waypointB == null ? "unset" : waypointB,
                getCurrentTarget()
        );
    }
}
