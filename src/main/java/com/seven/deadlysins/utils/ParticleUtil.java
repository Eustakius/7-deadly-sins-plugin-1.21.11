package com.seven.deadlysins.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class ParticleUtil {

    /**
     * Draws a pair of blood-red wings on the target.
     */
    public static void drawBloodEagleWings(Location loc) {
        World world = loc.getWorld();
        if (world == null)
            return;

        Vector direction = loc.getDirection().setY(0).normalize();
        Vector right = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
        Vector up = new Vector(0, 1, 0);

        // Simple V-shaped wing logic
        for (double i = 0; i < 2.5; i += 0.2) {
            for (double j = -1; j < 1; j += 0.2) {
                // Left Wing
                Location leftWing = loc.clone().add(up.clone().multiply(1.5 + j)).add(right.clone().multiply(-1.5 * i))
                        .add(direction.clone().multiply(-0.5 * i));
                world.spawnParticle(Particle.DUST, leftWing, 1, 0, 0, 0, 0,
                        new Particle.DustOptions(org.bukkit.Color.RED, 1.5f));

                // Right wing
                Location rightWing = loc.clone().add(up.clone().multiply(1.5 + j)).add(right.clone().multiply(1.5 * i))
                        .add(direction.clone().multiply(-0.5 * i));
                world.spawnParticle(Particle.DUST, rightWing, 1, 0, 0, 0, 0,
                        new Particle.DustOptions(org.bukkit.Color.RED, 1.5f));
            }
        }
    }

    /**
     * Spawns a 3x3 fiery zone of particles.
     */
    public static void drawHellfireZone(Location center, double radius) {
        World world = center.getWorld();
        if (world == null)
            return;

        for (double x = -radius; x <= radius; x += 0.5) {
            for (double z = -radius; z <= radius; z += 0.5) {
                if (Math.random() > 0.5) {
                    world.spawnParticle(Particle.FLAME, center.clone().add(x, 0.2, z), 2, 0.1, 0.1, 0.1, 0.02);
                }
            }
        }
    }

    public static void spawnCircle(Location center, Particle particle, double radius, int points) {
        World world = center.getWorld();
        if (world == null)
            return;

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            world.spawnParticle(particle, center.clone().add(x, 0, z), 1, 0, 0, 0, 0);
        }
    }
}
