package com.seven.deadlysins.features;

import com.seven.deadlysins.SevenDeadlySins;
import com.seven.deadlysins.registry.CustomEnchant;
import com.seven.deadlysins.utils.VisualUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SlothListener implements Listener {

    private final SevenDeadlySins plugin;
    private final NamespacedKey lethargyModKey;
    private final NamespacedKey inertiaModKey;

    // Memory Tracking
    private final Map<UUID, Long> blockStartTimes = new HashMap<>();
    private final Map<UUID, Long> playerStandTimes = new HashMap<>(); // UUID -> Time Started Standing Still
    private final Map<UUID, Location> lastLocations = new HashMap<>();

    // Procrastination DoT
    private final Map<UUID, Double> delayedDamage = new HashMap<>();

    public SlothListener(SevenDeadlySins plugin) {
        this.plugin = plugin;
        this.lethargyModKey = new NamespacedKey(plugin, "lethargy_fatigue");
        this.inertiaModKey = new NamespacedKey(plugin, "inertia_resist");

        startTasks();
    }

    private void startTasks() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();

            for (Player player : Bukkit.getOnlinePlayers()) {
                Location loc = player.getLocation();
                Location lastLoc = lastLocations.get(player.getUniqueId());

                // Track moving vs standing still
                if (lastLoc != null && loc.getWorld() == lastLoc.getWorld() && loc.distanceSquared(lastLoc) < 0.05) {
                    if (!playerStandTimes.containsKey(player.getUniqueId())) {
                        playerStandTimes.put(player.getUniqueId(), now);
                    }
                } else {
                    playerStandTimes.remove(player.getUniqueId());
                }
                lastLocations.put(player.getUniqueId(), loc);

                // 58. Inertia Mitigation Scaling
                ItemStack leggings = player.getInventory().getLeggings();
                int inertiaLevel = CustomEnchant.INERTIA.getLevel(leggings);

                AttributeInstance armor = player.getAttribute(Attribute.GENERIC_ARMOR);
                if (armor != null) {
                    for (AttributeModifier mod : armor.getModifiers()) {
                        if (mod.getName().equals("inertia_resist"))
                            armor.removeModifier(mod);
                    }

                    if (inertiaLevel > 0 && playerStandTimes.containsKey(player.getUniqueId())) {
                        long stoodMs = now - playerStandTimes.get(player.getUniqueId());
                        double seconds = stoodMs / 1000.0;
                        if (seconds > 1.0) {
                            double bonus = Math.min(20.0, seconds * 5.0 * inertiaLevel); // Caps out mitigation fast
                            armor.addModifier(new AttributeModifier(inertiaModKey, bonus,
                                    AttributeModifier.Operation.ADD_NUMBER));
                            if (Math.random() < 0.1) {
                                VisualUtil.playVisual(player.getLocation().add(0, 0.5, 0), null, CustomEnchant.INERTIA,
                                        1.0);
                            }
                        }
                    }
                }

                // 59. Procrastination DoT Apply
                if (delayedDamage.containsKey(player.getUniqueId())) {
                    double pool = delayedDamage.get(player.getUniqueId());
                    if (pool > 0) {
                        double tickDamage = Math.max(1.0, pool * 0.1); // Deal 10% of remaining pool or 1HP min
                        if (player.getHealth() > tickDamage && !player.isDead()) {
                            player.damage(tickDamage);
                            pool -= tickDamage;
                        } else if (player.getHealth() <= tickDamage) {
                            // DoT cannot kill you with Procrastination; leaves at 1 HP.
                            pool = 0;
                        }

                        if (pool <= 0)
                            delayedDamage.remove(player.getUniqueId());
                        else
                            delayedDamage.put(player.getUniqueId(), pool);

                        VisualUtil.playVisual(player.getLocation().add(0, 2, 0), null, CustomEnchant.PROCRASTINATION,
                                1.0);
                    }
                }
            }
        }, 10L, 10L); // Half a second
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemStack offitem = player.getInventory().getItemInOffHand();

            // 60. Time Dilation Parries
            if ((item != null && item.getType() == Material.SHIELD)
                    || (offitem != null && offitem.getType() == Material.SHIELD)) {
                blockStartTimes.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player target) {

            // 59. Procrastination
            ItemStack helmet = target.getInventory().getHelmet();
            int procrasLevel = CustomEnchant.PROCRASTINATION.getLevel(helmet);
            if (procrasLevel > 0 && event.getFinalDamage() > 6.0
                    && event.getCause() != EntityDamageEvent.DamageCause.POISON
                    && event.getCause() != EntityDamageEvent.DamageCause.CUSTOM) {
                // Delay burst damage
                double pool = delayedDamage.getOrDefault(target.getUniqueId(), 0.0);
                delayedDamage.put(target.getUniqueId(), pool + event.getFinalDamage());
                event.setDamage(0); // Cancel immediate impact

                target.getWorld().playSound(target.getLocation(), Sound.BLOCK_SAND_PLACE, 1f, 0.5f);
                target.sendMessage("§eYour injury has been delayed...");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCombat(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity victim) {

            // 54. Apathy / 60. Time Dilation (Receiver Parries)
            if (victim instanceof Player pVictim && pVictim.isBlocking()) {
                ItemStack shield = pVictim.getInventory().getItemInOffHand();
                if (shield.getType() != Material.SHIELD)
                    shield = pVictim.getInventory().getItemInMainHand();

                int apathyLevel = CustomEnchant.APATHY.getLevel(shield);
                if (apathyLevel > 0 && event.getDamager() instanceof LivingEntity attacker) {
                    attacker.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                    VisualUtil.playVisual(pVictim.getLocation().add(0, 1, 0), null, CustomEnchant.APATHY, 1.0);
                    // Nullify knockback handled by the system since blocking natively does it
                    // mostly,
                    // but we can ensure it by removing velocity post tick.
                    Bukkit.getScheduler().runTaskLater(plugin,
                            () -> pVictim.setVelocity(new Vector(0, pVictim.getVelocity().getY(), 0)), 1L);
                }

                int timeLevel = CustomEnchant.TIME_DILATION.getLevel(shield);
                if (timeLevel > 0 && blockStartTimes.containsKey(pVictim.getUniqueId())) {
                    long blockTime = System.currentTimeMillis() - blockStartTimes.get(pVictim.getUniqueId());
                    if (blockTime < 300) { // Perfect parry window (0.3s)
                        event.setCancelled(true);
                        pVictim.getWorld().playSound(pVictim.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 2f);
                        VisualUtil.playVisual(pVictim.getLocation().add(0, 1, 0), null, CustomEnchant.TIME_DILATION,
                                1.0);

                        if (event.getDamager() instanceof LivingEntity attacker) {
                            // Freeze attacker
                            attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 10)); // Slow 10
                                                                                                           // freezes
                                                                                                           // mostly
                            attacker.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 40, 10));
                        }
                    }
                }
            }

            Player attacker = event.getDamager() instanceof Player p ? p : null;
            if (attacker != null) {
                ItemStack weapon = attacker.getInventory().getItemInMainHand();

                // 51. Lethargy
                int lethargyLevel = CustomEnchant.LETHARGY.getLevel(weapon);
                if (lethargyLevel > 0 && victim instanceof Player pVictim) {
                    AttributeInstance atkSpd = pVictim.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                    if (atkSpd != null) {
                        // Apply -50% attack speed fatigue
                        AttributeModifier bad = new AttributeModifier(lethargyModKey, -0.5,
                                AttributeModifier.Operation.ADD_SCALAR);
                        boolean hasMod = false;
                        for (AttributeModifier m : atkSpd.getModifiers()) {
                            if (m.getName().equals("lethargy_fatigue")) {
                                hasMod = true;
                                break;
                            }
                        }
                        if (!hasMod)
                            atkSpd.addModifier(bad);

                        // Remove after 3 seconds
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            for (AttributeModifier m : atkSpd.getModifiers()) {
                                if (m.getName().equals("lethargy_fatigue"))
                                    atkSpd.removeModifier(m);
                            }
                        }, 60L);

                        VisualUtil.playVisual(victim.getLocation().add(0, 1, 0), null, CustomEnchant.LETHARGY, 1.0);
                    }
                }

                // 55. Heavy Burden
                int burdenLevel = CustomEnchant.HEAVY_BURDEN.getLevel(weapon);
                if (burdenLevel > 0) {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 60, -128)); // Prevent jump 3s
                    Vector v = victim.getVelocity();
                    victim.setVelocity(v.setY(v.getY() - 0.5)); // Smite downwards
                    VisualUtil.playVisual(victim.getLocation().add(0, 2, 0), null, CustomEnchant.HEAVY_BURDEN, 1.0);
                }

                // 57. Somnolence
                int somnoLevel = CustomEnchant.SOMNOLENCE.getLevel(weapon);
                if (somnoLevel > 0) {
                    boolean isCrit = attacker.getVelocity().getY() < 0 && !attacker.isOnGround()
                            && !attacker.isInsideVehicle(); // Basic crit check
                    if (isCrit && Math.random() < 0.2) {
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 10)); // Stun
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 40, 10)); // Prevent
                                                                                                           // interact
                                                                                                           // effectively
                        VisualUtil.playVisual(victim.getLocation().add(0, 2, 0), null, CustomEnchant.SOMNOLENCE, 1.0);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player shooter) {
            ItemStack bow = arrow.getWeapon() != null ? arrow.getWeapon() : shooter.getInventory().getItemInMainHand();

            // 53. Yawning Chasm
            int chasmLevel = CustomEnchant.YAWNING_CHASM.getLevel(bow);
            if (chasmLevel > 0) {
                Location hitLoc = event.getHitEntity() != null ? event.getHitEntity().getLocation()
                        : (event.getHitBlock() != null ? event.getHitBlock().getLocation() : arrow.getLocation());

                Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                    if (task.getTaskId() % 60 == 0)
                        task.cancel(); // 3s lifespan
                    VisualUtil.playVisual(hitLoc, null, CustomEnchant.YAWNING_CHASM, 1.0);
                    hitLoc.getWorld().getNearbyEntities(hitLoc, 4, 4, 4).forEach(e -> {
                        if (e instanceof LivingEntity le && !e.equals(shooter)) {
                            // Reduce velocity drastically
                            Vector v = le.getVelocity();
                            le.setVelocity(v.multiply(0.1));

                            // Visual cue
                            VisualUtil.playVisual(le.getLocation().add(0, 1, 0), null, CustomEnchant.YAWNING_CHASM,
                                    1.0);
                        }
                    });
                }, 0L, 5L); // tick quickly
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // 56. Sluggard's Pace
        ItemStack boots = player.getInventory().getBoots();
        int sluggardLevel = CustomEnchant.SLUGGARDS_PACE.getLevel(boots);
        if (sluggardLevel > 0 && player.isSprinting() && event.getFrom().distanceSquared(event.getTo()) > 0) {
            Location loc = player.getLocation();
            VisualUtil.playVisual(loc, null, CustomEnchant.SLUGGARDS_PACE, 1.0);

            loc.getWorld().getNearbyEntities(loc, 1.5, 1.5, 1.5).forEach(e -> {
                if (e instanceof LivingEntity le && !e.equals(player)) {
                    le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 3)); // Slowness IV
                    // Synergy: Status Chilled
                    NamespacedKey chillKey = new NamespacedKey(plugin, "status_chilled");
                    com.seven.deadlysins.utils.PdcUtil.setCooldown(le, chillKey, 40 * 50L); // 2 seconds
                }
            });
        }
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        // 52. Hibernation
        if (event.isSneaking()) {
            ItemStack chest = player.getInventory().getChestplate();
            int hibLevel = CustomEnchant.HIBERNATION.getLevel(chest);
            if (hibLevel > 0) {
                // To keep this clean without massively rewriting the world or risking stuck
                // players:
                // We just grant Resistance V and spawn barrier block particles around them
                // after 3s.
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.isSneaking() && player.isOnline()
                            && CustomEnchant.HIBERNATION.getLevel(player.getInventory().getChestplate()) > 0) { // Still
                                                                                                                // sneaking
                                                                                                                // 3s
                                                                                                                // later
                                                                                                                // with
                                                                                                                // chestplate
                        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 4)); // Immune to
                                                                                                       // damage
                                                                                                       // basically
                        VisualUtil.playVisual(player.getLocation(), null, CustomEnchant.HIBERNATION, 1.0);
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SNOW_PLACE, 1f, 1f);
                    }
                }, 60L);
            }
        }
    }
}
