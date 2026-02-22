package com.seven.deadlysins.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PdcUtil {

    /**
     * Checks if an entity is on cooldown for a specific key.
     */
    public static boolean isOnCooldown(Entity entity, NamespacedKey key) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (pdc.has(key, PersistentDataType.LONG)) {
            long expiry = pdc.get(key, PersistentDataType.LONG);
            if (System.currentTimeMillis() < expiry) {
                return true;
            } else {
                pdc.remove(key); // Cleanup
            }
        }
        return false;
    }

    /**
     * Sets a cooldown for an entity.
     * 
     * @param durationMs Duration in milliseconds.
     */
    public static void setCooldown(Entity entity, NamespacedKey key, long durationMs) {
        entity.getPersistentDataContainer().set(key, PersistentDataType.LONG, System.currentTimeMillis() + durationMs);
    }

    /**
     * Gets an integer value from PDC.
     */
    public static int getInt(Entity entity, NamespacedKey key, int defaultValue) {
        return entity.getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, defaultValue);
    }

    /**
     * Sets an integer value in PDC.
     */
    public static void setInt(Entity entity, NamespacedKey key, int value) {
        entity.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
    }

    /**
     * Removes a value from PDC.
     */
    public static void remove(Entity entity, NamespacedKey key) {
        entity.getPersistentDataContainer().remove(key);
    }
}
