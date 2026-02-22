package com.seven.deadlysins.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 * Robust particle utility to prevent IllegalArgumentException (missing data)
 * and sanitize coordinates (preventing NaN).
 */
public class ParticleUtil {

    /**
     * Spawns a particle safely, ensuring all required data is provided
     * based on the 1.21.1 API expectations.
     */
    public static void spawnSafe(Location loc, Particle particle, int count, double ox, double oy, double oz, double extra, Object data) {
        if (loc == null || Double.isNaN(loc.getX()) || Double.isNaN(loc.getY()) || Double.isNaN(loc.getZ())) return;
        World world = loc.getWorld();
        if (world == null) return;

        Object finalData = validateData(particle, data);

        try {
            if (finalData != null) {
                world.spawnParticle(particle, loc, count, ox, oy, oz, extra, finalData);
            } else {
                world.spawnParticle(particle, loc, count, ox, oy, oz, extra);
            }
        } catch (Exception e) {
            // Final fallback to no-data if specified data failed
            if (finalData != null) {
                try {
                    world.spawnParticle(particle, loc, count, ox, oy, oz, extra);
                } catch (Exception ignored) {}
            }
        }
    }

    private static Object validateData(Particle particle, Object data) {
        Class<?> dataType = particle.getDataType();

        // If no data is required, return null
        if (dataType == Void.class) return null;

        // If correct data is already provided, return it
        if (data != null && dataType.isInstance(data)) return data;

        // Provide defaults for common required data types
        if (dataType == Particle.DustOptions.class) {
            return new Particle.DustOptions(Color.WHITE, 1.0f);
        }
        if (dataType == Color.class) {
            return Color.WHITE;
        }
        if (dataType == ItemStack.class) {
            return null; // ITEM particles MUST have an ItemStack, handled by the caller or nullified
        }
        // org.bukkit.block.data.BlockData usually required for BLOCK/FALLING_DUST
        
        return data; 
    }
}
