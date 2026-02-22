package com.seven.deadlysins.features;

import com.seven.deadlysins.SevenDeadlySins;
import com.seven.deadlysins.registry.CustomEnchant;
import com.seven.deadlysins.utils.PdcUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GluttonyListener implements Listener {

    private final SevenDeadlySins plugin;
    private final NamespacedKey feastKey;
    private final NamespacedKey feastModKey;
    private final NamespacedKey acidicModKey;
    private final NamespacedKey gorgingModKey;

    private final Map<UUID, Long> acidicBiteTargets = new HashMap<>();

    public GluttonyListener(SevenDeadlySins plugin) {
        this.plugin = plugin;
        this.feastKey = new NamespacedKey(plugin, "feast_of_souls_stacks");
        this.feastModKey = new NamespacedKey(plugin, "feast_max_hp");
        this.acidicModKey = new NamespacedKey(plugin, "acidic_bite_hp_down");
        this.gorgingModKey = new NamespacedKey(plugin, "gorging_toughness");

        startTasks();
    }

    private void startTasks() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Acidic Bite DoT loop
            long now = System.currentTimeMillis();
            for (Map.Entry<UUID, Long> entry : new HashMap<>(acidicBiteTargets).entrySet()) {
                if (now > entry.getValue()) {
                    acidicBiteTargets.remove(entry.getKey());
                    Entity e = Bukkit.getEntity(entry.getKey());
                    if (e instanceof LivingEntity le) {
                        AttributeInstance maxHp = le.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        if (maxHp != null) {
                            for (AttributeModifier mod : maxHp.getModifiers()) {
                                if (mod.getKey().equals(acidicModKey))
                                    maxHp.removeModifier(mod);
                            }
                        }
                    }
                    continue;
                }

                Entity e = Bukkit.getEntity(entry.getKey());
                if (e instanceof LivingEntity le && !le.isDead()) {
                    le.damage(1.0); // True damage
                    le.getWorld().spawnParticle(Particle.ITEM_SLIME, le.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2,
                            0);
                }
            }

            // Gorging Defense & Feast of Souls Cleanup Task
            for (Player player : Bukkit.getOnlinePlayers()) {

                // Feast of Souls Cleanup
                ItemStack weapon = player.getInventory().getItemInMainHand();
                if (CustomEnchant.FEAST_OF_SOULS.getLevel(weapon) == 0) {
                    PdcUtil.remove(player, feastKey);
                    AttributeInstance feastMaxHp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    if (feastMaxHp != null) {
                        for (AttributeModifier mod : feastMaxHp.getModifiers()) {
                            if (mod.getKey().equals(feastModKey))
                                feastMaxHp.removeModifier(mod);
                        }
                    }
                }

                ItemStack helmet = player.getInventory().getHelmet();
                int gorgingLevel = CustomEnchant.GORGING_DEFENSE.getLevel(helmet);

                AttributeInstance toughness = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
                if (toughness != null) {
                    for (AttributeModifier mod : toughness.getModifiers()) {
                        if (mod.getKey().equals(gorgingModKey))
                            toughness.removeModifier(mod);
                    }

                    if (gorgingLevel > 0) {
                        double missingHunger = 20.0 - player.getFoodLevel();
                        double modifierValue = (20.0 - missingHunger) * 0.5 * gorgingLevel; // Up to +30 toughness if
                                                                                            // full hunger level 3

                        if (modifierValue > 0) {
                            toughness.addModifier(new AttributeModifier(gorgingModKey, modifierValue,
                                    AttributeModifier.Operation.ADD_NUMBER));
                        }
                    }
                }
            }
        }, 20L, 20L); // every second
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();

            // 41. Devourer's Maw
            ItemStack sword = killer.getInventory().getItemInMainHand();
            int devourLevel = CustomEnchant.DEVOURERS_MAW.getLevel(sword);
            if (devourLevel > 0) {
                killer.setFoodLevel(Math.min(20, killer.getFoodLevel() + (3 * devourLevel)));
                killer.setSaturation(killer.getSaturation() + (3 * devourLevel));
                killer.getWorld().spawnParticle(Particle.BLOCK, killer.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3,
                        Bukkit.createBlockData(Material.BEEF));
                killer.getWorld().playSound(killer.getLocation(), Sound.ENTITY_PLAYER_BURP, 1f, 1f);
            }

            // 43. Feast of Souls
            ItemStack axe = killer.getInventory().getItemInMainHand(); // Wait, 43 is Axe. In prompt: Axe
            if (axe.getType().name().contains("AXE")) {
                int feastLevel = CustomEnchant.FEAST_OF_SOULS.getLevel(axe);
                if (feastLevel > 0) {
                    int stacks = PdcUtil.getInt(killer, feastKey, 0);
                    if (stacks < 10) { // Max +10 hearts (20 HP)
                        stacks++;
                        PdcUtil.setInt(killer, feastKey, stacks);

                        AttributeInstance maxHp = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        if (maxHp != null) {
                            for (AttributeModifier mod : maxHp.getModifiers()) {
                                if (mod.getKey().equals(feastModKey))
                                    maxHp.removeModifier(mod);
                            }
                            maxHp.addModifier(new AttributeModifier(feastModKey, stacks * 2.0,
                                    AttributeModifier.Operation.ADD_NUMBER));
                        }
                        killer.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, killer.getLocation().add(0, 1, 0), 10,
                                0.3, 0.3, 0.3, 0.05);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Reset Feast of Souls
        Player player = event.getPlayer();
        PdcUtil.remove(player, feastKey);
        AttributeInstance maxHp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHp != null) {
            for (AttributeModifier mod : maxHp.getModifiers()) {
                if (mod.getKey().equals(feastModKey))
                    maxHp.removeModifier(mod);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && event.getPlayer().isSneaking()) {
            Player player = event.getPlayer();

            // 42. Cannibalize
            ItemStack chestplate = player.getInventory().getChestplate();
            int cannibalizeLevel = CustomEnchant.CANNIBALIZE.getLevel(chestplate);

            if (cannibalizeLevel > 0
                    && player.getHealth() < player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                // Check durability
                int maxDura = chestplate.getType().getMaxDurability();
                if (maxDura > 0) {
                    if (chestplate.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable damageable) {
                        int currentDamage = damageable.hasDamage() ? damageable.getDamage() : 0;
                        if (currentDamage + 100 <= maxDura) {
                            chestplate.damage(100, player);
                            player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
                                    player.getHealth() + 8.0)); // 4 hearts

                            player.getWorld().spawnParticle(Particle.ITEM_SLIME, player.getLocation().add(0, 1, 0), 20,
                                    0.5,
                                    0.5, 0.5, 0);
                            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SLIME_SQUISH, 1f, 1f);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player target) {

            // 45. Bottomless Pit
            if (event.getDamager() instanceof Arrow && target.isBlocking()) {
                ItemStack shield = target.getInventory().getItemInOffHand();
                if (shield.getType() != Material.SHIELD)
                    shield = target.getInventory().getItemInMainHand();

                int pitLevel = CustomEnchant.BOTTOMLESS_PIT.getLevel(shield);
                if (pitLevel > 0) {
                    event.setCancelled(true);
                    target.setSaturation((float) (target.getSaturation() + event.getDamage()));
                    target.getWorld().spawnParticle(Particle.PORTAL, target.getLocation().add(0, 1, 0), 15, 0.3, 0.3,
                            0.3, 0.5);
                    return;
                }
            }

            // 48. Gluttonous Swarm
            ItemStack leggings = target.getInventory().getLeggings();
            int swarmLevel = CustomEnchant.GLUTTONOUS_SWARM.getLevel(leggings);
            if (swarmLevel > 0 && event.getDamager() instanceof LivingEntity attacker) {
                if (Math.random() < (0.2 * swarmLevel)) {
                    Silverfish fish = target.getWorld().spawn(target.getLocation(), Silverfish.class, f -> {
                        f.customName(net.kyori.adventure.text.Component.text("§7Gluttonous Swarm"));
                        f.setTarget(attacker);
                    });

                    target.getWorld().spawnParticle(Particle.BLOCK, target.getLocation(), 10, 0.5, 0.5, 0.5,
                            Bukkit.createBlockData(Material.INFESTED_STONE));
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (!fish.isDead())
                            fish.remove();
                    }, 100L); // 5 sec lifespan
                }
            }
        }

        if (event.getDamager() instanceof Player attacker) {
            ItemStack weapon = attacker.getInventory().getItemInMainHand();

            // 44. Acidic Bite
            int acidicLevel = CustomEnchant.ACIDIC_BITE.getLevel(weapon);
            if (acidicLevel > 0 && event.getEntity() instanceof LivingEntity victim) {
                acidicBiteTargets.put(victim.getUniqueId(), System.currentTimeMillis() + 5000L); // 5 seconds

                AttributeInstance maxHp = victim.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (maxHp != null) {
                    boolean hasMod = false;
                    for (AttributeModifier mod : maxHp.getModifiers()) {
                        if (mod.getKey().equals(acidicModKey))
                            hasMod = true;
                    }
                    if (!hasMod) {
                        maxHp.addModifier(
                                new AttributeModifier(acidicModKey, -4.0, AttributeModifier.Operation.ADD_NUMBER)); // Max
                                                                                                                    // hp
                                                                                                                    // drops
                                                                                                                    // 2
                                                                                                                    // hearts
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player shooter) {
            ItemStack bow = arrow.getWeapon() != null ? arrow.getWeapon() : shooter.getInventory().getItemInMainHand();

            // 46. Black Hole
            int blackHoleLevel = CustomEnchant.BLACK_HOLE.getLevel(bow);
            if (blackHoleLevel > 0) {
                Location hitLoc = event.getHitEntity() != null ? event.getHitEntity().getLocation()
                        : (event.getHitBlock() != null ? event.getHitBlock().getLocation() : arrow.getLocation());

                Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                    if (task.getTaskId() % 100 == 0)
                        task.cancel(); // roughly 5 seconds
                    hitLoc.getWorld().spawnParticle(Particle.PORTAL, hitLoc, 10, 0.5, 0.5, 0.5, 0.1);
                    hitLoc.getWorld().getNearbyEntities(hitLoc, 5, 5, 5).forEach(e -> {
                        if (e instanceof LivingEntity && !e.equals(shooter)) {
                            Vector pull = hitLoc.toVector().subtract(e.getLocation().toVector()).normalize()
                                    .multiply(0.2);
                            if (e.getLocation().distanceSquared(hitLoc) > 1.0) {
                                e.setVelocity(e.getVelocity().add(pull));
                            }
                        }
                    });
                }, 0L, 2L);
            }

            // 50. Leeching Plague
            int leechLevel = CustomEnchant.LEECHING_PLAGUE.getLevel(bow);
            if (leechLevel > 0) {
                Location hitLoc = event.getHitEntity() != null ? event.getHitEntity().getLocation()
                        : (event.getHitBlock() != null ? event.getHitBlock().getLocation() : arrow.getLocation());

                double totalStolen = 0;
                for (Entity e : hitLoc.getWorld().getNearbyEntities(hitLoc, 5, 5, 5)) {
                    if (e instanceof LivingEntity le && !e.equals(shooter)) {
                        le.damage(1.0, shooter);
                        totalStolen += 1.0;

                        // Particle beam back to shooter
                        Vector dir = shooter.getEyeLocation().toVector().subtract(le.getLocation().toVector());
                        double dist = dir.length();
                        dir.normalize().multiply(0.5);
                        Location beamLoc = le.getLocation().add(0, 1, 0).clone();
                        for (double i = 0; i < dist; i += 0.5) {
                            beamLoc.add(dir);
                            le.getWorld().spawnParticle(Particle.DUST, beamLoc, 1, 0, 0, 0, 0,
                                    new Particle.DustOptions(org.bukkit.Color.RED, 0.5f));
                        }
                    }
                }

                if (totalStolen > 0) {
                    shooter.setHealth(Math.min(shooter.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
                            shooter.getHealth() + totalStolen));
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack pick = player.getInventory().getItemInMainHand();

        // 47. Omnivore
        int omniLevel = CustomEnchant.OMNIVORE.getLevel(pick);
        if (omniLevel > 0) {
            String bName = event.getBlock().getType().name();
            if (bName.contains("OBSIDIAN") || bName.contains("DEEPSLATE")) {
                player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
                        player.getHealth() + 0.5));
                event.getBlock().getWorld().spawnParticle(Particle.BLOCK,
                        event.getBlock().getLocation().add(0.5, 0.5, 0.5), 10, 0.2, 0.2, 0.2,
                        Bukkit.createBlockData(event.getBlock().getType()));
            }
        }
    }
}
