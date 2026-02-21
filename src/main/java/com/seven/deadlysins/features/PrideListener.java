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
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class PrideListener implements Listener {

    private final SevenDeadlySins plugin;
    private final NamespacedKey invulnKey;
    private final NamespacedKey kingsCdKey;

    private final NamespacedKey unbowedModKey;
    private final NamespacedKey solitaryModKey;

    public PrideListener(SevenDeadlySins plugin) {
        this.plugin = plugin;
        this.invulnKey = new NamespacedKey(plugin, "invulnerable_kings");
        this.kingsCdKey = new NamespacedKey(plugin, "kings_resurgence_cd");

        this.unbowedModKey = new NamespacedKey(plugin, "unbowed_kb_res");
        this.solitaryModKey = new NamespacedKey(plugin, "solitary_atk_spd");

        startAuraTask();
    }

    private void startAuraTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {

                // 12. Regal Presence (Leggings)
                ItemStack leggings = player.getInventory().getLeggings();
                int regalLevel = CustomEnchant.REGAL_PRESENCE.getLevel(leggings);
                if (regalLevel > 0) {
                    player.getWorld().getNearbyEntities(player.getLocation(), 3, 3, 3).forEach(e -> {
                        if (e instanceof Mob && !(e instanceof Boss) && !e.equals(player)) {
                            Vector push = e.getLocation().toVector().subtract(player.getLocation().toVector());
                            if (push.lengthSquared() > 0) {
                                push.normalize().multiply(0.5);
                                push.setY(0.2);
                                e.setVelocity(push);
                            }
                        }
                    });
                    if (Math.random() < 0.2) {
                        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 0.5, 0), 2, 1, 0.2,
                                1, 0.05);
                    }
                }

                // 19. Unbowed Knockback Immunity
                AttributeInstance kbResist = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
                ItemStack boots = player.getInventory().getBoots();
                if (kbResist != null) {
                    if (CustomEnchant.UNBOWED.getLevel(boots) > 0) {
                        boolean hasMod = false;
                        for (AttributeModifier mod : kbResist.getModifiers()) {
                            if (mod.getKey().equals(unbowedModKey))
                                hasMod = true;
                        }
                        if (!hasMod) {
                            kbResist.addModifier(
                                    new AttributeModifier(unbowedModKey, 1.0, AttributeModifier.Operation.ADD_NUMBER));
                        }
                        // Remove slowness
                        if (player.hasPotionEffect(PotionEffectType.SLOWNESS)) {
                            player.removePotionEffect(PotionEffectType.SLOWNESS);
                            player.getWorld().spawnParticle(Particle.ITEM_SLIME, player.getLocation().add(0, 1, 0), 10,
                                    0.5, 0.5, 0.5, 0);
                        }
                    } else {
                        for (AttributeModifier mod : kbResist.getModifiers()) {
                            if (mod.getKey().equals(unbowedModKey)) {
                                kbResist.removeModifier(mod);
                            }
                        }
                    }
                }

                // 13. Solitary Monarch (dynamic attribute)
                ItemStack sword = player.getInventory().getItemInMainHand();
                AttributeInstance atkSpeed = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                if (atkSpeed != null) {
                    int solitaryLevel = CustomEnchant.SOLITARY_MONARCH.getLevel(sword);
                    boolean isSolitary = false;
                    if (solitaryLevel > 0) {
                        isSolitary = true;
                        for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), 20, 20, 20)) {
                            if (e instanceof Player && !e.equals(player)) {
                                isSolitary = false;
                                break;
                            }
                        }
                    }

                    boolean hasSoliMod = false;
                    for (AttributeModifier mod : atkSpeed.getModifiers()) {
                        if (mod.getKey().equals(solitaryModKey))
                            hasSoliMod = true;
                    }

                    if (isSolitary && !hasSoliMod) {
                        atkSpeed.addModifier(
                                new AttributeModifier(solitaryModKey, 0.5, AttributeModifier.Operation.ADD_SCALAR));
                    } else if (!isSolitary && hasSoliMod) {
                        for (AttributeModifier mod : atkSpeed.getModifiers()) {
                            if (mod.getKey().equals(solitaryModKey))
                                atkSpeed.removeModifier(mod);
                        }
                    }
                    if (isSolitary && Math.random() < 0.1) {
                        player.getWorld().spawnParticle(Particle.GLOW, player.getLocation().add(0, 1, 0), 3, 0.5, 0.5,
                                0.5, 0);
                    }
                }
            }
        }, 5L, 5L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFatalDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        // 11. King's Resurgence Check
        if (event.getFinalDamage() >= player.getHealth()) {
            ItemStack chestplate = player.getInventory().getChestplate();
            int kingsLevel = CustomEnchant.KINGS_RESURGENCE.getLevel(chestplate);
            if (kingsLevel > 0) {
                if (!PdcUtil.isOnCooldown(player, kingsCdKey)) {
                    event.setCancelled(true);
                    player.setHealth(1.0);
                    PdcUtil.setCooldown(player, kingsCdKey, 10 * 60 * 1000L); // 10 minutes
                    PdcUtil.setCooldown(player, invulnKey, 3000L); // 3 seconds invuln

                    player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1f, 1f);
                    player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0, 1, 0), 50,
                            0.5, 0.5, 0.5, 0.1);
                    return;
                }
            }
        }

        if (PdcUtil.isOnCooldown(player, invulnKey)) {
            event.setCancelled(true); // Invulnerable state
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player target) {

            // Invincibility check again for direct entity hits to be safe
            if (PdcUtil.isOnCooldown(target, invulnKey)) {
                event.setCancelled(true);
                return;
            }

            // 15. Crown of Thorns
            ItemStack helmet = target.getInventory().getHelmet();
            int crownLevel = CustomEnchant.CROWN_OF_THORNS.getLevel(helmet);
            if (crownLevel > 0 && event.getDamager() instanceof LivingEntity attacker) {
                double attackerMaxHp = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double trueDamage = attackerMaxHp * 0.10 * crownLevel;
                // Pure true damage
                attacker.setHealth(Math.max(0, attacker.getHealth() - trueDamage));
                target.getWorld().spawnParticle(Particle.CRIT, attacker.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3,
                        0.1);
            }

            // 16. Arrogant Parry
            ItemStack mainHand = target.getInventory().getItemInMainHand();
            ItemStack offHand = target.getInventory().getItemInOffHand();
            int parryLevel = CustomEnchant.ARROGANT_PARRY.getLevel(mainHand)
                    + CustomEnchant.ARROGANT_PARRY.getLevel(offHand);
            if (parryLevel > 0 && Math.random() < (0.15 * parryLevel)) {
                event.setCancelled(true);
                target.getWorld().playSound(target.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 1.5f);
                target.getWorld().spawnParticle(Particle.ASH, target.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5,
                        0.1);

                if (event.getDamager() instanceof LivingEntity attacker) {
                    attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 9)); // Slowness 10
                                                                                                  // (amplifier 9)
                }
                return;
            }
        }

        if (event.getDamager() instanceof Player attacker) {
            ItemStack weapon = attacker.getInventory().getItemInMainHand();

            // 13. Solitary Monarch Damage Bonus
            int solitaryLevel = CustomEnchant.SOLITARY_MONARCH.getLevel(weapon);
            if (solitaryLevel > 0) {
                boolean isSolitary = true;
                for (Entity e : attacker.getWorld().getNearbyEntities(attacker.getLocation(), 20, 20, 20)) {
                    if (e instanceof Player && !e.equals(attacker)) {
                        isSolitary = false;
                        break;
                    }
                }
                if (isSolitary) {
                    event.setDamage(event.getDamage() * 1.5);
                }
            }
        }

        // 14. Champion's Challenge
        if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player shooter) {
            ItemStack bow = arrow.getWeapon() != null ? arrow.getWeapon() : shooter.getInventory().getItemInMainHand();
            int champLevel = CustomEnchant.CHAMPIONS_CHALLENGE.getLevel(bow);
            if (champLevel > 0 && event.getEntity() instanceof Mob mob) {
                mob.setTarget(shooter);
                mob.getWorld().spawnParticle(Particle.END_ROD, mob.getLocation().add(0, 2, 0), 5, 0.1, 0.1, 0.1, 0.05);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();
        ItemStack weapon = player.getInventory().getItemInMainHand();
        int sovereignLevel = CustomEnchant.SOVEREIGNS_REACH.getLevel(weapon);

        // 17. Sovereign's Reach
        if (sovereignLevel > 0) {
            Location eyeLoc = player.getEyeLocation();
            Vector dir = eyeLoc.getDirection();
            double reach = 3.0 + (2.5 * sovereignLevel); // Base 3 + bonus

            RayTraceResult result = player.getWorld().rayTraceEntities(eyeLoc, dir, reach, 0.5,
                    e -> e instanceof LivingEntity && !e.equals(player));
            if (result != null && result.getHitEntity() instanceof LivingEntity target) {
                // Ignore if it's within pure vanilla reach to prevent double hitting.
                // Pure vanilla reach is heavily ping dependent, but roughly 3.0.
                if (eyeLoc.distance(target.getLocation()) > 3.0) {
                    player.attack(target); // Force attack
                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation().add(0, 1, 0), 2, 0.5,
                            0.5, 0.5, 0);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // 18. Gilded Execution
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            ItemStack weapon = killer.getInventory().getItemInMainHand();
            int gildedLevel = CustomEnchant.GILDED_EXECUTION.getLevel(weapon);

            if (gildedLevel > 0 && Math.random() < (0.2 * gildedLevel)) {
                event.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 1 + (int) (Math.random() * 3)));
                killer.getWorld().spawnParticle(Particle.BLOCK, event.getEntity().getLocation().add(0, 0.5, 0), 10, 0.3,
                        0.3, 0.3, Bukkit.createBlockData(Material.GOLD_BLOCK));
            }
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player player) {
            // 19. Unbowed Slowness Cancel
            ItemStack boots = player.getInventory().getBoots();
            int unbowedLevel = CustomEnchant.UNBOWED.getLevel(boots);
            if (unbowedLevel > 0) {
                if (event.getNewEffect() != null && event.getNewEffect().getType().equals(PotionEffectType.SLOWNESS)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow))
            return;
        if (!(arrow.getShooter() instanceof Player shooter))
            return;

        ItemStack bow = arrow.getWeapon() != null ? arrow.getWeapon() : shooter.getInventory().getItemInMainHand();

        // 20. Royal Decree
        int decreeLevel = CustomEnchant.ROYAL_DECREE.getLevel(bow);
        if (decreeLevel > 0 && arrow.isCritical()) { // Fully charged = critical arrow usually
            Location hitLoc = event.getHitEntity() != null ? event.getHitEntity().getLocation()
                    : (event.getHitBlock() != null ? event.getHitBlock().getLocation() : arrow.getLocation());

            // Strike lightning structurally (no fire spawned natively using effect)
            hitLoc.getWorld().strikeLightningEffect(hitLoc);
            hitLoc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, hitLoc, 30, 2, 2, 2, 0.5);

            hitLoc.getWorld().getNearbyEntities(hitLoc, 5, 5, 5).forEach(e -> {
                if (e instanceof LivingEntity le && !e.equals(shooter)) {
                    le.damage(10.0 * decreeLevel, shooter);
                }
            });
        }
    }
}
