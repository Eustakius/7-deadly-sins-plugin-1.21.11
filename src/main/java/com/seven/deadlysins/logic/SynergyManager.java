package com.seven.deadlysins.logic;

import com.seven.deadlysins.registry.CustomEnchant;
import com.seven.deadlysins.utils.VisualUtil;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * SynergyManager handles "Sin Synergies" where multiple enchantments
 * from different Sins work together to create enhanced effects.
 */
public class SynergyManager {

    public enum SynergyType {
        VAMPIRIC_RUPTURE, // Wrath + Gluttony
        IMPACT_STAGNATION, // Pride + Sloth
        THIEFS_FORTUNE, // Greed + Envy
        FRENZIED_ATTRACTION, // Lust + Wrath
        METABOLIC_SLOWDOWN // Gluttony + Sloth
    }

    // Tracks internal state for synergies if needed (e.g. cooldowns)
    private static final Map<UUID, Long> synergyCooldowns = new HashMap<>();

    /**
     * Checks if a synergy should trigger and applies the effect.
     */
    public static double evaluateSynergy(Player player, LivingEntity target, SynergyType type, double baseValue) {
        if (player == null || target == null)
            return baseValue;

        switch (type) {
            case VAMPIRIC_RUPTURE:
                // If target is bleeding (Wrath: Mutilation) AND player has Gluttony (Leeching
                // Plague or Devourer's Maw)
                // This is often checked inside the listener, but here we confirm the "Synergy"
                // bonus.
                return baseValue * 2.0;

            case IMPACT_STAGNATION:
                // Enhanced damage when Pride pushes into Sloth trails
                return baseValue + 5.0;

            case METABOLIC_SLOWDOWN:
                // Double duration of debuffs
                return baseValue * 2.0;

            default:
                return baseValue;
        }
    }

    /**
     * Plays unique visuals for a synergy trigger.
     */
    public static void playSynergyVisual(Location loc, SynergyType type) {
        // Special "Rainbow" or mixed particles representing combined Sins
        VisualUtil.playVisual(loc, new Vector(0, 1, 0), CustomEnchant.BLOOD_EAGLE, 0.5); // Fallback to a high-impact
                                                                                         // visual
        // In a future update, VisualUtil.java will have a dedicated playSynergy()
        // method.
    }

    public static boolean isOnCooldown(UUID uuid, SynergyType type) {
        String key = uuid.toString() + type.name();
        return synergyCooldowns.getOrDefault(key, 0L) > System.currentTimeMillis();
    }

    public static void setCooldown(UUID uuid, SynergyType type, long ms) {
        synergyCooldowns.put(UUID.fromString(uuid.toString() + type.name()), System.currentTimeMillis() + ms);
    }
}
