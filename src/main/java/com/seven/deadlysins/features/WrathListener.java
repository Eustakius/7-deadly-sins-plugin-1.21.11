package com.seven.deadlysins.features;

import com.seven.deadlysins.SevenDeadlySins;
import com.seven.deadlysins.registry.CustomEnchant;
import com.seven.deadlysins.utils.ParticleUtil;
import com.seven.deadlysins.utils.PdcUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WrathListener implements Listener {

    private final SevenDeadlySins plugin;

    // Memory structures for complex tracking (Scorched Earth, Mutilation)
    private final Map<UUID, Long> mutilatedEntities = new HashMap<>(); // UUID -> Expiry Timestamp
    private final Map<UUID, UUID> duelistTarget = new HashMap<>(); // Attacker UUID -> Target UUID
    private final Map<UUID, Integer> duelistStacks = new HashMap<>(); // Attacker UUID -> Stacks

    public WrathListener(SevenDeadlySins plugin) {
        this.plugin = plugin;
        startBackgroundTasks();
    }

    private void startBackgroundTasks() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long now = System.currentTimeMillis();
            mutilatedEntities.entrySet().removeIf(entry -> now > entry.getValue());
        }, 20L, 20L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMeleeDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity target))
            return;

        Player attacker = null;
        if (event.getDamager() instanceof Player p) {
            attacker = p;
        }

        if (attacker != null) {
            ItemStack weapon = attacker.getInventory().getItemInMainHand();
            if (weapon == null || weapon.getType().isAir())
                return;

            // 1. Blood Eagle (Axe) - Execution bypass totem
            int bloodEagleLevel = CustomEnchant.BLOOD_EAGLE.getLevel(weapon);
            if (bloodEagleLevel > 0) {
                double targetMaxHp = target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double threshold = targetMaxHp * 0.15; // 15% HP

                if (target.getHealth() <= threshold) {
                    executeTarget(target);
                    event.setDamage(0);
                    return;
                }
            }

            // 2. Berserker's Rage (Sword)
            int berserkerLevel = CustomEnchant.BERSERKERS_RAGE.getLevel(weapon);
            if (berserkerLevel > 0) {
                double maxHp = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double currentHp = attacker.getHealth();
                double missingHp = maxHp - currentHp;
                if (missingHp > 0) {
                    double multiplier = 1.0 + Math.log10(1 + (missingHp * berserkerLevel * 0.5));
                    event.setDamage(event.getDamage() * multiplier);
                    target.getWorld().spawnParticle(Particle.DUST, attacker.getLocation().add(0, 1, 0), 10, 0.3, 0.3,
                            0.3, new Particle.DustOptions(org.bukkit.Color.RED, 1.0f));
                }
            }

            // 3. Siege Breaker
            int siegeLevel = CustomEnchant.SIEGE_BREAKER.getLevel(weapon);
            if (siegeLevel > 0) {
                if (target instanceof Player pTarget && pTarget.isBlocking()) {
                    event.setDamage(event.getDamage() * 3.0);
                    target.getWorld().playSound(target.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 1f);
                    ParticleUtil.spawnCircle(target.getLocation().add(0, 1, 0), Particle.CRIT, 1.5, 20);

                    // Break shield durability massively
                    ItemStack shield = pTarget.getInventory().getItemInOffHand();
                    if (shield.getType() == Material.SHIELD) {
                        shield.damage(100 * siegeLevel, pTarget);
                    }
                }

                // Degrade target armor instantly
                if (target.getEquipment() != null) {
                    for (ItemStack armor : target.getEquipment().getArmorContents()) {
                        if (armor != null && !armor.getType().isAir()) {
                            armor.damage(5 * siegeLevel, target);
                        }
                    }
                }
            }

            // 6. Duelist's Spite
            int duelistLevel = CustomEnchant.DUELISTS_SPITE.getLevel(weapon);
            if (duelistLevel > 0) {
                UUID aId = attacker.getUniqueId();
                UUID tId = target.getUniqueId();

                if (tId.equals(duelistTarget.get(aId))) {
                    int stacks = duelistStacks.getOrDefault(aId, 0) + 1;
                    duelistStacks.put(aId, Math.min(stacks, 10)); // Cap stacks

                    double multi = 1.0 + (stacks * 0.1 * duelistLevel);
                    event.setDamage(event.getDamage() * multi);

                    target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 5 * stacks, 0.5,
                            0.5, 0.5, 0.1);
                } else {
                    duelistTarget.put(aId, tId);
                    duelistStacks.put(aId, 0); // Reset
                }
            }

            // 9. Vengeance Strike (Chestplate) - Attacker consumed
            ItemStack chest = attacker.getInventory().getChestplate();
            int vengeanceAttackerLvl = CustomEnchant.VENGEANCE_STRIKE.getLevel(chest);
            if (vengeanceAttackerLvl > 0) {
                NamespacedKey storedDamageKey = new NamespacedKey(plugin, "vengeance_stored");
                if (attacker.getPersistentDataContainer().has(storedDamageKey, PersistentDataType.DOUBLE)) {
                    double storedDamage = attacker.getPersistentDataContainer().get(storedDamageKey,
                            PersistentDataType.DOUBLE);
                    attacker.getPersistentDataContainer().remove(storedDamageKey);

                    event.setDamage(event.getDamage() + (storedDamage * 2.0));
                    target.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, attacker.getLocation().add(0, 2, 0), 3,
                            0.5, 0.5, 0.5);
                }
            }

            // 10. Mutilation
            int mutilationLevel = CustomEnchant.MUTILATION.getLevel(weapon);
            if (mutilationLevel > 0) {
                mutilatedEntities.put(target.getUniqueId(), System.currentTimeMillis() + (mutilationLevel * 3000L)); // 9s
                                                                                                                     // max
                // Start DoT loop
                Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                    if (target.isDead() || !mutilatedEntities.containsKey(target.getUniqueId())) {
                        task.cancel();
                        return;
                    }
                    target.damage(1.0); // True damage DoT
                    target.getWorld().spawnParticle(Particle.DUST, target.getLocation().add(0, 1, 0), 3, 0.2, 0.2, 0.2,
                            new Particle.DustOptions(org.bukkit.Color.RED, 1.0f));
                }, 10L, 20L); // every second
            }
        }

        // Apply Vengeance Strike receiver logic
        if (target instanceof Player victim) {
            ItemStack chest = victim.getInventory().getChestplate();
            int vengeanceLevel = CustomEnchant.VENGEANCE_STRIKE.getLevel(chest);
            if (vengeanceLevel > 0) {
                NamespacedKey storedDamageKey = new NamespacedKey(plugin, "vengeance_stored");
                victim.getPersistentDataContainer().set(storedDamageKey, PersistentDataType.DOUBLE,
                        event.getFinalDamage());
            }
        }
    }

    private void executeTarget(LivingEntity target) {
        // Strip out totems of undying to bypass them
        if (target instanceof Player p) {
            PlayerInventory inv = p.getInventory();
            if (inv.getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) {
                inv.setItemInMainHand(null);
            }
            if (inv.getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
                inv.setItemInOffHand(null);
            }
        } else {
            if (target.getEquipment() != null) {
                if (target.getEquipment().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) {
                    target.getEquipment().setItemInMainHand(null);
                }
                if (target.getEquipment().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
                    target.getEquipment().setItemInOffHand(null);
                }
            }
        }

        target.setHealth(0.0);
        ParticleUtil.drawBloodEagleWings(target.getLocation());
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.5f);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow))
            return;
        if (!(arrow.getShooter() instanceof Player shooter))
            return;

        ItemStack bow = arrow.getWeapon(); // In 1.21 we can get the weapon that shot this
        if (bow == null && shooter.getInventory().getItemInMainHand().getType() == Material.BOW) {
            bow = shooter.getInventory().getItemInMainHand(); // Fallback
        }
        if (bow == null && shooter.getInventory().getItemInMainHand().getType() == Material.CROSSBOW) {
            bow = shooter.getInventory().getItemInMainHand(); // Fallback
        }

        // 4. Hellfire Trebuchet
        int hellfireLevel = CustomEnchant.HELLFIRE_TREBUCHET.getLevel(bow);
        if (hellfireLevel > 0) {
            Location hitLoc = event.getHitEntity() != null ? event.getHitEntity().getLocation()
                    : (event.getHitBlock() != null ? event.getHitBlock().getLocation() : null);
            if (hitLoc != null) {
                Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                    // Stop after 5 seconds (100 ticks)
                    if (task.getTaskId() > 100)
                        task.cancel(); // basic kill condition placeholder
                    // Draw fire
                    ParticleUtil.drawHellfireZone(hitLoc, 1.5);
                    // Damage nearby
                    hitLoc.getWorld().getNearbyEntities(hitLoc, 1.5, 1.5, 1.5).forEach(e -> {
                        if (e instanceof LivingEntity le && !e.equals(shooter)) {
                            le.setFireTicks(40);
                            le.damage(1.0, shooter);
                        }
                    });
                }, 0L, 10L);
            }
        }

        // 7. Impaler
        int impalerLevel = CustomEnchant.IMPALER.getLevel(bow);
        if (impalerLevel > 0 && event.getHitEntity() instanceof LivingEntity target) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 255, false, false, true));
            target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 40, -128, false, false, true)); // Prevent
                                                                                                                 // jumping

            // Visual iron bars
            Location startLoc = target.getLocation();
            target.getWorld().spawnParticle(Particle.BLOCK, startLoc.add(0, 1, 0), 50, 0.5, 1, 0.5,
                    Bukkit.createBlockData(Material.IRON_BARS));
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        // 5. Warlord's Cry
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            ItemStack helmet = killer.getInventory().getHelmet();
            int warlordLevel = CustomEnchant.WARLORDS_CRY.getLevel(helmet);

            if (warlordLevel > 0) {
                killer.getWorld().getNearbyEntities(killer.getLocation(), 15, 15, 15).forEach(e -> {
                    if (e instanceof Player ally) { // Simplistic allied check
                        ally.addPotionEffect(
                                new PotionEffect(PotionEffectType.STRENGTH, 200 * warlordLevel, warlordLevel - 1));
                    }
                });
                killer.getWorld().playSound(killer.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1f, 1f);
                killer.getWorld().spawnParticle(Particle.SONIC_BOOM, killer.getLocation().add(0, 1, 0), 1);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.isSprinting())
            return;

        // 8. Scorched Earth
        ItemStack boots = player.getInventory().getBoots();
        int scorchedLevel = CustomEnchant.SCORCHED_EARTH.getLevel(boots);
        if (scorchedLevel > 0) {
            Location loc = player.getLocation();
            if (Math.random() > 0.6) {
                loc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 1, 0, 0.1, 0, 0);
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 2, 0.2, 0.1, 0.2, 0.02);
            }

            // Apply fire to nearby non-player entities directly behind them
            loc.getWorld().getNearbyEntities(loc, 1.5, 1.5, 1.5).forEach(e -> {
                if (e instanceof LivingEntity le && !e.equals(player)) {
                    le.setFireTicks(60);
                }
            });
        }
    }

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent event) {
        // 10. Mutilation anti-heal
        if (mutilatedEntities.containsKey(event.getEntity().getUniqueId())) {
            event.setAmount(event.getAmount() * 0.5); // 50% healing reduction
        }
    }
}
