package com.seven.deadlysins.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Advanced renderer providing helper methods for high-fidelity
 * geometric particle animations (Rings, Spheres, Helices, Beams).
 */
public class GeometricRenderer {

    /**
     * Spawns a horizontal ring of particles.
     */
    public static void spawnRing(Location center, Particle particle, double radius, int density, Object data) {
        World world = center.getWorld();
        if (world == null)
            return;
        for (double a = 0; a < Math.PI * 2; a += (Math.PI * 2 / density)) {
            Location loc = center.clone().add(Math.cos(a) * radius, 0, Math.sin(a) * radius);
            spawn(particle, loc, data);
        }
    }

    public static void spawnHelix(Location center, Particle particle, double radius, double height, int density,
            double rotations, Object data) {
        World world = center.getWorld();
        if (world == null)
            return;
        for (double y = 0; y < height; y += (height / density)) {
            double angle = y * (rotations * Math.PI * 2 / height);
            Location loc = center.clone().add(Math.cos(angle) * radius, y, Math.sin(angle) * radius);
            spawn(particle, loc, data);
        }
    }

    public static void spawnSphere(Location center, Particle particle, double radius, int density, Object data) {
        World world = center.getWorld();
        if (world == null)
            return;
        for (double i = 0; i <= Math.PI; i += Math.PI / Math.sqrt(density)) {
            double r = Math.sin(i) * radius;
            double y = Math.cos(i) * radius;
            for (double a = 0; a < Math.PI * 2; a += Math.PI * 2 / Math.sqrt(density)) {
                Location loc = center.clone().add(Math.cos(a) * r, y, Math.sin(a) * r);
                spawn(particle, loc, data);
            }
        }
    }

    public static void spawnBeam(Location start, Location end, Particle particle, double step, Object data) {
        World world = start.getWorld();
        if (world == null)
            return;
        Vector dir = end.toVector().subtract(start.toVector());
        double dist = dir.length();
        if (dist < 0.001)
            return; // Prevent NaN in normalize()

        dir.normalize().multiply(step);
        Location current = start.clone();
        for (double i = 0; i < dist; i += step) {
            spawn(particle, current, data);
            current.add(dir);
        }
    }

    public static void spawnPulse(Location center, Particle particle, double maxRadius, int layers, Object data) {
        for (int i = 1; i <= layers; i++) {
            double r = (maxRadius / layers) * i;
            spawnRing(center, particle, r, (int) (20 * r), data);
        }
    }

    private static void spawn(Particle particle, Location loc, Object data) {
        ParticleUtil.spawnSafe(loc, particle, 1, 0, 0, 0, 0, data);
    }
}
