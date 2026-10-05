package com.bahbah.shipautopilot.core;

import net.minecraft.world.phys.Vec3;

/**
 * Represents a spatial waypoint for autopilot navigation.
 */
public class WaypointData {
    public final double x;
    public final double y;
    public final double z;

    public WaypointData(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 toVec3() {
        return new Vec3(x, y, z);
    }

    public double distanceTo(WaypointData other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double dz = other.z - this.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public String toString() {
        return String.format("WaypointData(%.2f, %.2f, %.2f)", x, y, z);
    }
}
