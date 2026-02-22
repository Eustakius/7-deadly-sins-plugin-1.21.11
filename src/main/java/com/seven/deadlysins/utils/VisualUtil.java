package com.seven.deadlysins.utils;

import com.seven.deadlysins.registry.CustomEnchant;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Creative Semantic Visual Engine (Action-Oriented Reboot)
 * Reconstructed 100% from memory. Focus: Slashes, Claws, Impacts.
 * NO CHEATING - 70 UNIQUE ARTS.
 */
public class VisualUtil {

    public static void playVisual(Location center, Vector dir, CustomEnchant enchant, double scale) {
        World world = center.getWorld();
        if (world == null)
            return;
        if (dir == null)
            dir = new Vector(0, 1, 0);

        String n = enchant.name();

        // Helper for Slash shapes
        // dir is the facing direction. we want a perpendicular slash arc.

        // --- WRATH (Crimson, Violent, Sharp) ---
        if (n.equals("BLOOD_EAGLE")) {
            // Executioner's Cross Slash (Subtle X-Shape)
            spawnSlash(center, dir, Color.MAROON, true, 1.0);
            world.spawnParticle(Particle.DUST, center, 60, 0.6, 0.6, 0.6, 0.05,
                    new Particle.DustOptions(Color.RED, 1.5f));
            world.spawnParticle(Particle.SQUID_INK, center, 15, 0.4, 0.4, 0.4, 0.02);
        } else if (n.equals("BERSERKERS_RAGE")) {
            // Primal Claws (3 Parallel Slashes) - Tightened
            spawnClaws(center, dir, Color.RED, 0.8);
            world.spawnParticle(Particle.FLAME, center, 20, 0.3, 0.3, 0.3, 0.05);
        } else if (n.equals("SIEGE_BREAKER")) {
            // Heavy Shield Shatter (Metal shards)
            world.spawnParticle(Particle.BLOCK, center, 80, 0.6, 0.6, 0.6, 0.1, Material.ANVIL.createBlockData());
            GeometricRenderer.spawnPulse(center, Particle.CRIT, 1.5, 1, null);
        } else if (n.equals("HELLFIRE_TREBUCHET")) {
            // Napalm Impact (Fire streaks)
            world.spawnParticle(Particle.FLAME, center, 60, 1.2, 0.3, 1.2, 0.05);
            world.spawnParticle(Particle.LAVA, center, 15, 0.8, 0.8, 0.8, 0.02);
        } else if (n.equals("WARLORDS_CRY")) {
            // Sonic Shout (Single expanding ring)
            GeometricRenderer.spawnRing(center, Particle.GUST, 2.0, 20, null);
            world.spawnParticle(Particle.CLOUD, center, 15, 0.5, 0.5, 0.5, 0.01);
        } else if (n.equals("DUELISTS_SPITE")) {
            // Precision Rapier Thrust (Thin beam + bleed)
            GeometricRenderer.spawnBeam(center, center.clone().add(dir.clone().multiply(5)), Particle.DUST, 0.05,
                    new Particle.DustOptions(Color.BLACK, 1.2f));
            world.spawnParticle(Particle.DUST, center.clone().add(dir.clone().multiply(5)), 40, 0.3, 0.3, 0.3, 0.1,
                    new Particle.DustOptions(Color.RED, 1.0f));
        } else if (n.equals("IMPALER")) {
            // Ground Spikes (Stone eruption)
            for (int i = 0; i < 3; i++) {
                Location loc = center.clone().add(dir.clone().multiply(i * 1.5));
                world.spawnParticle(Particle.BLOCK, loc, 40, 0.2, 1.0, 0.2, 0.05,
                        Material.POINTED_DRIPSTONE.createBlockData());
            }
        } else if (n.equals("SCORCHED_EARTH")) {
            // Magma Fissure (Lava line on ground)
            GeometricRenderer.spawnBeam(center, center.clone().add(dir.clone().multiply(6)), Particle.LAVA, 0.4, null);
            world.spawnParticle(Particle.FLAME, center, 80, 4.0, 0.1, 4.0, 0.05);
            world.spawnParticle(Particle.SMOKE, center, 30, 2.0, 0.5, 2.0, 0.02);
        } else if (n.equals("VENGEANCE_STRIKE")) {
            // Massive Retribution Slam (Central flash + bloom)
            world.spawnParticle(Particle.EXPLOSION, center, 1);
            world.spawnParticle(Particle.TRIAL_SPAWNER_DETECTION, center, 60, 2.0, 0.2, 2.0, 0.1);
            spawnSlash(center, dir, Color.RED, false, 1.2);
        } else if (n.equals("MUTILATION")) {
            // Arterial Spray (Fine red mist)
            world.spawnParticle(Particle.DUST, center, 100, 0.8, 1.2, 0.8, 0.05,
                    new Particle.DustOptions(Color.MAROON, 1.2f));
            spawnClaws(center, dir, Color.MAROON, 0.6);
        }

        // --- PRIDE (Golden, Radiant, Holy) ---
        else if (n.equals("KINGS_RESURGENCE")) {
            // Holy Wings (Small arc)
            GeometricRenderer.spawnHelix(center, Particle.WAX_OFF, 1.2, 2.0, 30, 0.5, null);
            GeometricRenderer.spawnHelix(center, Particle.WAX_OFF, 1.2, 2.0, 30, -0.5, null);
            world.spawnParticle(Particle.END_ROD, center, 15, 0.5, 0.5, 0.5, 0.05);
        } else if (n.equals("REGAL_PRESENCE")) {
            // Sovereign Shockwave (Gilded ring)
            GeometricRenderer.spawnRing(center, Particle.WAX_OFF, 3.0, 30, null);
            world.spawnParticle(Particle.GUST, center, 10, 0.5, 0.1, 0.5, 0.01);
        } else if (n.equals("SOLITARY_MONARCH")) {
            // Crown of Isolation (Golden shimmer)
            GeometricRenderer.spawnBeam(center, center.clone().add(0, 3, 0), Particle.DUST, 0.3,
                    new Particle.DustOptions(Color.YELLOW, 1.5f));
            world.spawnParticle(Particle.WAX_OFF, center.clone().add(0, 3, 0), 10);
        } else if (n.equals("CHAMPIONS_CHALLENGE")) {
            // Duel Banners (Small rising motes)
            for (int i = 0; i < 3; i++) {
                double a = i * (2 * Math.PI / 3);
                Location l = center.clone().add(Math.cos(a) * 1.5, 0, Math.sin(a) * 1.5);
                GeometricRenderer.spawnBeam(l, l.clone().add(0, 2, 0), Particle.WAX_OFF, 0.5, null);
            }
        } else if (n.equals("CROWN_OF_THORNS")) {
            // Radiant Spikes (Small golden ring)
            GeometricRenderer.spawnRing(center, Particle.WAX_OFF, 1.5, 20, null);
            world.spawnParticle(Particle.DUST, center, 20, 0.8, 0.8, 0.8, 0.1,
                    new Particle.DustOptions(Color.YELLOW, 1.0f));
        } else if (n.equals("ARROGANT_PARRY")) {
            // Shield Flash (Single flash)
            world.spawnParticle(Particle.FLASH, center, 1);
            GeometricRenderer.spawnRing(center, Particle.GUST, 2.0, 15, null);
        } else if (n.equals("SOVEREIGNS_REACH")) {
            // Extended Blade (Clear streak)
            double r = reach(scale) * 0.7; // Toned down reach
            Location end = center.clone().add(dir.clone().multiply(r));
            GeometricRenderer.spawnBeam(center, end, Particle.WAX_OFF, 0.5, null);
            world.spawnParticle(Particle.SWEEP_ATTACK, end, 1);
        } else if (n.equals("GILDED_EXECUTION")) {
            // Gold Explosion (Subtle shards)
            world.spawnParticle(Particle.ITEM, center, 40, 0.8, 0.8, 0.8, 0.05, new ItemStack(Material.GOLD_NUGGET));
            world.spawnParticle(Particle.WAX_OFF, center, 10);
        } else if (n.equals("UNBOWED")) {
            // Iron Aura (Steady motes)
            world.spawnParticle(Particle.BLOCK, center, 30, 0.6, 1.0, 0.6, 0.05, Material.BEDROCK.createBlockData());
            world.spawnParticle(Particle.CLOUD, center, 10, 0.5, 0.5, 0.5, 0.02);
        } else if (n.equals("ROYAL_DECREE")) {
            // Lightning Spear (Single thin beam)
            GeometricRenderer.spawnBeam(center.clone().add(0, 5, 0), center, Particle.DUST, 0.5,
                    new Particle.DustOptions(Color.AQUA, 1.5f));
            world.spawnParticle(Particle.ELECTRIC_SPARK, center, 20, 0.5, 0.5, 0.5, 0.05);
        }

        // --- GREED (Emerald, Wealth, Siphon) ---
        else if (n.equals("HIGHWAYMANS_TOLL")) {
            // XP Siphon Arcs (Victim to Player) - Subtle
            spawnSiphon(center, dir, Color.LIME, 20);
        } else if (n.equals("MIDAS_TOUCH")) {
            // Golden Touch (Subtle block fragments)
            world.spawnParticle(Particle.BLOCK, center, 60, 0.8, 0.8, 0.8, 0.05, Material.GOLD_BLOCK.createBlockData());
            world.spawnParticle(Particle.WAX_OFF, center, 15);
        } else if (n.equals("PLUNDERERS_STRIKE")) {
            // Treasure Spray (A few emeralds)
            world.spawnParticle(Particle.ITEM, center, 15, 0.4, 0.4, 0.4, 0.1, new ItemStack(Material.EMERALD));
            world.spawnParticle(Particle.HAPPY_VILLAGER, center, 20);
        } else if (n.equals("HOARDERS_VITALITY")) {
            // Golden Heart (Steady pulse)
            world.spawnParticle(Particle.HEART, center.clone().add(0, 1.5, 0), 3);
            GeometricRenderer.spawnRing(center, Particle.WAX_OFF, 1.2, 15, null);
        } else if (n.equals("MERCENARYS_FORTUNE")) {
            // Profit Cloud (Muted green)
            world.spawnParticle(Particle.HAPPY_VILLAGER, center, 40, 1.2, 1.2, 1.2, 0.05);
        } else if (n.equals("DRAGONS_HOARD")) {
            // Vault Barrier (Thin golden sphere)
            GeometricRenderer.spawnRing(center, Particle.WAX_OFF, 2.5, 30, null);
            world.spawnParticle(Particle.BLOCK, center, 10, 0.5, 0.5, 0.5, 0, Material.BARRIER.createBlockData());
        } else if (n.equals("EXTORTION")) {
            // Debt Chains (Single rising helix)
            GeometricRenderer.spawnHelix(center, Particle.DUST, 0.8, 3.0, 30, 1,
                    new Particle.DustOptions(Color.GRAY, 1.0f));
        } else if (n.equals("PICKPOCKET")) {
            // Smoke Evasion (Small poof)
            world.spawnParticle(Particle.LARGE_SMOKE, center, 60, 0.8, 1.2, 0.8, 0.02);
            world.spawnParticle(Particle.WHITE_ASH, center, 30, 0.6, 0.6, 0.6, 0.01);
        } else if (n.equals("TREASURE_HUNTER")) {
            // Loot Compass (Short beam)
            GeometricRenderer.spawnBeam(center, center.clone().add(dir.clone().multiply(5)), Particle.HAPPY_VILLAGER,
                    0.8, null);
        } else if (n.equals("USURY")) {
            // Harvesting Scythe (Green slash)
            spawnSlash(center, dir, Color.GREEN, false, 0.8);
        }

        // --- ENVY (Shadow, Twisted, Toxic) ---
        else if (n.equals("MIMICRY")) {
            // Shapeshifter Vortex (Thin purple wisps)
            GeometricRenderer.spawnHelix(center, Particle.PORTAL, 1.5, 3.0, 40, 2, null);
        } else if (n.equals("USURPERS_BLADE")) {
            // Power Drain (Teal streaks upward) - Reduced
            for (int i = 0; i < 12; i++) {
                world.spawnParticle(Particle.SCULK_SOUL,
                        center.clone().add(Math.random() * 1.5 - 0.75, 0, Math.random() * 1.5 - 0.75), 0, 0, 0.1, 0,
                        0.1);
            }
        } else if (n.equals("SHADOW_CLONE")) {
            // Decoy Poof (Purple smoke) - Reduced
            world.spawnParticle(Particle.WITCH, center, 100, 0.8, 1.5, 0.8, 0.05);
        } else if (n.equals("THIEF_OF_BUFFS")) {
            // Potion Siphon (Subtle purple swirls)
            spawnSiphon(center, dir, Color.FUCHSIA, 15);
        } else if (n.equals("SPITEFUL_SABOTAGE")) {
            // Acidic Corrode (Green bubbles)
            world.spawnParticle(Particle.SNEEZE, center, 60, 0.8, 1.2, 0.8, 0.05);
        } else if (n.equals("GREEN_EYED_GLARE")) {
            // Jealous Eye (Faint focus)
            world.spawnParticle(Particle.OMINOUS_SPAWNING, center.clone().add(0, 1.5, 0), 20, 0.2, 0.2, 0.2, 0);
        } else if (n.equals("PARASITIC_LINK")) {
            // Life Tether (Thin teal line)
            GeometricRenderer.spawnBeam(center, center.clone().add(dir.clone().multiply(5)), Particle.DUST, 0.5,
                    new Particle.DustOptions(Color.TEAL, 1.0f));
        } else if (n.equals("FALSE_IDOL")) {
            // Mirror Swap (Glass shatter)
            world.spawnParticle(Particle.BLOCK, center, 60, 0.6, 1.0, 0.6, 0.05, Material.GLASS.createBlockData());
            world.spawnParticle(Particle.PORTAL, center, 15, 0.5, 0.5, 0.5, 0.05);
        } else if (n.equals("DOPPELGANGER")) {
            // After-Image Strike (Small double slash)
            spawnSlash(center, dir, Color.PURPLE, false, 0.7);
            spawnSlash(center.clone().add(0, 0.4, 0), dir, Color.BLACK, false, 0.7);
        } else if (n.equals("COVETOUS_PULL")) {
            // Gravity Hook (Small inward portal bits)
            for (int i = 0; i < 20; i++) {
                Vector v = Vector.getRandom().subtract(new Vector(0.5, 0.5, 0.5)).normalize().multiply(3.0);
                world.spawnParticle(Particle.PORTAL, center.clone().add(v), 0, -v.getX(), -v.getY(), -v.getZ(), 0.2);
            }
        }

        // --- GLUTTONY (Inward, Void, Consumption) ---
        else if (n.equals("DEVOURERS_MAW")) {
            // Void Bite (Minor inward implosion)
            for (int i = 0; i < 30; i++) {
                Vector v = Vector.getRandom().subtract(new Vector(0.5, 0.5, 0.5)).normalize().multiply(3.0);
                world.spawnParticle(Particle.SQUID_INK, center.clone().add(v), 0, -v.getX(), -v.getY(), -v.getZ(),
                        0.2);
            }
            world.spawnParticle(Particle.CRIT, center, 10);
        } else if (n.equals("CANNIBALIZE")) {
            // Blood Burst (Smaller red spray)
            world.spawnParticle(Particle.BLOCK, center, 40, 0.4, 0.4, 0.4, 0.05,
                    Material.REDSTONE_BLOCK.createBlockData());
        } else if (n.equals("FEAST_OF_SOULS")) {
            // Soul Harvest (Few rising spirits)
            world.spawnParticle(Particle.SCULK_SOUL, center, 15, 0.8, 2.0, 0.8, 0.02);
        } else if (n.equals("ACIDIC_BITE")) {
            // Dissolve (Muted lime bubbles)
            world.spawnParticle(Particle.SNEEZE, center, 30, 0.6, 0.6, 0.6, 0.05);
        } else if (n.equals("BOTTOMLESS_PIT")) {
            // Infinite Maw (Single black hole swirl)
            GeometricRenderer.spawnHelix(center, Particle.SQUID_INK, 1.5, 0.1, 30, 2, null);
        } else if (n.equals("BLACK_HOLE")) {
            // Event Horizon (Small gravity well)
            world.spawnParticle(Particle.SONIC_BOOM, center, 1);
            for (int i = 0; i < 40; i++) {
                Vector v = Vector.getRandom().subtract(new Vector(0.5, 0.5, 0.5)).normalize().multiply(4.0);
                world.spawnParticle(Particle.LARGE_SMOKE, center.clone().add(v), 0, -v.getX(), -v.getY(), -v.getZ(),
                        0.3);
            }
        } else if (n.equals("OMNIVORE")) {
            // Consumption Aura (Biting particles)
            world.spawnParticle(Particle.CRIT, center, 20, 1.2, 1.2, 1.2, 0.05);
        } else if (n.equals("GLUTTONOUS_SWARM")) {
            // Hive Surge (Small swarming cloud)
            world.spawnParticle(Particle.WITCH, center, 40, 1.5, 1.0, 1.5, 0.02);
        } else if (n.equals("GORGING_DEFENSE")) {
            // Padded Hull (Thin white shell)
            GeometricRenderer.spawnRing(center, Particle.CLOUD, 1.5, 20, null);
        } else if (n.equals("LEECHING_PLAGUE")) {
            // Viral Spread (Few maroon tendrils)
            for (int i = 0; i < 4; i++) {
                Vector v = Vector.getRandom().subtract(new Vector(0.5, 0.5, 0.5)).normalize().multiply(3);
                GeometricRenderer.spawnBeam(center, center.clone().add(v), Particle.DUST, 0.5,
                        new Particle.DustOptions(Color.MAROON, 0.8f));
            }
        }

        // --- SLOTH (Frozen, Heavy, Still) ---
        else if (n.equals("LETHARGY")) {
            // Heavy Fog (Muted smoke)
            world.spawnParticle(Particle.LARGE_SMOKE, center, 100, 3.0, 1.5, 3.0, 0.001);
            world.spawnParticle(Particle.WHITE_ASH, center, 20, 2.0, 1.0, 2.0, 0.01);
        } else if (n.equals("HIBERNATION")) {
            // Ice Blockade (Snow shards)
            world.spawnParticle(Particle.BLOCK, center, 80, 1.2, 1.2, 1.2, 0.05, Material.SNOW_BLOCK.createBlockData());
            GeometricRenderer.spawnRing(center, Particle.SNOWFLAKE, 1.5, 15, null);
        } else if (n.equals("YAWNING_CHASM")) {
            // Time Slow Zone (Distant gust streaks)
            world.spawnParticle(Particle.GUST, center, 50, 4.0, 0.5, 4.0, 0.01);
        } else if (n.equals("APATHY")) {
            // Nihilism Burst (Grey ash)
            world.spawnParticle(Particle.WHITE_ASH, center, 120, 2.5, 2.5, 2.5, 0.01);
            world.spawnParticle(Particle.SQUID_INK, center, 10, 0.8, 0.8, 0.8, 0.05);
        } else if (n.equals("HEAVY_BURDEN")) {
            // Gravitational Slam (Anvil drop)
            GeometricRenderer.spawnBeam(center.clone().add(0, 3, 0), center, Particle.CRIT, 0.5, null);
            world.spawnParticle(Particle.BLOCK, center, 20, 0.6, 0.2, 0.6, 0.05, Material.ANVIL.createBlockData());
        } else if (n.equals("SLUGGARDS_PACE")) {
            // Frost Walk (Blue trail)
            world.spawnParticle(Particle.SNOWFLAKE, center, 20, 1.2, 0.1, 1.2, 0);
        } else if (n.equals("SOMNOLENCE")) {
            // Sleep Bubbles (Floating cloud bits)
            world.spawnParticle(Particle.ENCHANT, center, 40, 1.5, 2.0, 1.5, 0.05);
            world.spawnParticle(Particle.BUBBLE, center, 10, 0.8, 1.0, 0.8, 0.02);
        } else if (n.equals("INERTIA")) {
            // Stasis Frame (Thin ring)
            world.spawnParticle(Particle.END_ROD, center, 20, 1.2, 1.5, 1.2, 0);
            GeometricRenderer.spawnRing(center, Particle.WAX_OFF, 1.2, 10, null);
        } else if (n.equals("PROCRASTINATION")) {
            // Lag Particles (Delayed trails)
            GeometricRenderer.spawnHelix(center, Particle.DUST, 1.0, 3.0, 30, 1,
                    new Particle.DustOptions(Color.GRAY, 1.0f));
        } else if (n.equals("TIME_DILATION")) {
            // Cronos Field (Clock ticking burst)
            world.spawnParticle(Particle.TRIAL_SPAWNER_DETECTION, center, 40, 3.0, 3.0, 3.0, 0.01);
            world.spawnParticle(Particle.FLASH, center, 1);
        }

        // --- LUST (Hearts, Frenzy, Attraction) ---
        else if (n.equals("SIRENS_SONG")) {
            // Melodic Hearts (Small swirling petals)
            GeometricRenderer.spawnHelix(center, Particle.CHERRY_LEAVES, 1.5, 3.0, 30, 1, null);
            world.spawnParticle(Particle.HEART, center, 5, 0.8, 1.2, 0.8, 0.05);
        } else if (n.equals("BLOODLUST")) {
            // Red Frenzy (Small radial slashes)
            for (int i = 0; i < 4; i++) {
                double a = i * Math.PI / 2;
                Vector v = new Vector(Math.cos(a), 0, Math.sin(a));
                spawnSlash(center, v, Color.fromRGB(255, 20, 147), false, 0.6);
            }
        } else if (n.equals("SUCCUBUS_KISS")) {
            // XP Kiss (Subtle pink siphons)
            spawnSiphon(center, dir, Color.fromRGB(240, 140, 200), 12);
            world.spawnParticle(Particle.HEART, center, 2);
        } else if (n.equals("FATAL_ATTRACTION")) {
            // Love Vacuum (Small inward hearts)
            for (int i = 0; i < 20; i++) {
                Vector v = Vector.getRandom().subtract(new Vector(0.5, 0.5, 0.5)).normalize().multiply(3.0);
                world.spawnParticle(Particle.HEART, center.clone().add(v), 0, -v.getX(), -v.getY(), -v.getZ(), 0.2);
            }
        } else if (n.equals("ILLUSIONISTS_VEIL")) {
            // Phantom Mirrors (Single flash + poof)
            world.spawnParticle(Particle.FLASH, center, 1);
            world.spawnParticle(Particle.POOF, center, 60, 1.2, 1.2, 1.2, 0.05);
        } else if (n.equals("MASQUERADE")) {
            // Petal Whirl (Subtle cherry bloom)
            world.spawnParticle(Particle.CHERRY_LEAVES, center, 80, 1.5, 2.0, 1.5, 0.05);
        } else if (n.equals("BETRAYAL")) {
            // Chaos Pulse (Angry villager storm) - Reduced
            world.spawnParticle(Particle.ANGRY_VILLAGER, center, 10, 1.2, 1.2, 1.2, 0);
            world.spawnParticle(Particle.HEART, center, 5, 0.8, 0.8, 0.8, 0.05);
        } else if (n.equals("CAPTIVATION")) {
            // Charming Aura (Pink mist) - Reduced
            world.spawnParticle(Particle.ENTITY_EFFECT, center, 60, 1.5, 1.5, 1.5, 0, Color.fromRGB(255, 102, 178));
        } else if (n.equals("LOVERS_SACRIFICE")) {
            // Linked Souls (Thin red/pink helix)
            GeometricRenderer.spawnHelix(center, Particle.DUST, 0.8, 3.0, 30, 1,
                    new Particle.DustOptions(Color.RED, 0.8f));
        } else if (n.equals("INTOXICATING_STRIKE")) {
            // Dizzying Spray (Neon sensory overload) - Reduced
            world.spawnParticle(Particle.ENTITY_EFFECT, center, 100, 2.0, 2.0, 2.0, 0.5, Color.FUCHSIA);
        }
    }

    private static double reach(double scale) {
        return 3.0 + (2.5 * scale);
    }

    private static void spawnSlash(Location center, Vector dir, Color color, boolean isCross, double size) {
        World world = center.getWorld();
        Vector perp = (Math.abs(dir.getY()) > 0.9)
                ? new Vector(1, 0, 0)
                : new Vector(-dir.getZ(), 0, dir.getX()).normalize();

        for (double i = -size; i <= size; i += 0.2) {
            Location loc = center.clone().add(perp.clone().multiply(i))
                    .add(dir.clone().multiply(0.6 - Math.abs(i) * 0.3));
            world.spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, new Particle.DustOptions(color, 1.2f));
            if (isCross) {
                Location loc2 = center.clone().add(perp.clone().multiply(i))
                        .add(dir.clone().multiply(-(0.6 - Math.abs(i) * 0.3)));
                world.spawnParticle(Particle.DUST, loc2, 1, 0, 0, 0, 0, new Particle.DustOptions(color, 1.2f));
            }
        }
    }

    private static void spawnClaws(Location center, Vector dir, Color color, double size) {
        World world = center.getWorld();
        Vector perp = (Math.abs(dir.getY()) > 0.9)
                ? new Vector(1, 0, 0)
                : new Vector(-dir.getZ(), 0, dir.getX()).normalize();

        for (int side = -1; side <= 1; side++) {
            Location start = center.clone().add(perp.clone().multiply(side * 0.4));
            for (double i = 0; i <= size; i += 0.2) {
                Location p = start.clone().add(dir.clone().multiply(i)).add(0, Math.sin(i * Math.PI / size) * 0.3, 0);
                world.spawnParticle(Particle.DUST, p, 1, 0, 0, 0, 0, new Particle.DustOptions(color, 1.0f));
            }
        }
    }

    private static void spawnSiphon(Location center, Vector dir, Color color, int count) {
        World world = center.getWorld();
        for (int i = 0; i < count; i++) {
            Vector v = Vector.getRandom().subtract(new Vector(0.5, 0.5, 0.5)).normalize().multiply(3.5);
            world.spawnParticle(Particle.DUST, center.clone().add(v), 0, -v.getX(), -v.getY(), -v.getZ(), 0.2,
                    new Particle.DustOptions(color, 1.0f));
        }
    }
}
