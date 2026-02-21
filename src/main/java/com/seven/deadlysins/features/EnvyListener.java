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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class EnvyListener implements Listener {

    private final SevenDeadlySins plugin;
    private final NamespacedKey falseIdolKey;
    private final NamespacedKey covetousKey;
    private final NamespacedKey mimicKey;

    public EnvyListener(SevenDeadlySins plugin) {
        this.plugin = plugin;
        this.falseIdolKey = new NamespacedKey(plugin, "false_idol_cd");
        this.covetousKey = new NamespacedKey(plugin, "covetous_pull_cd");
        this.mimicKey = new NamespacedKey(plugin, "mimic_enchant");

        startGlareTask();
    }

    private void startGlareTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                // 36. Green-Eyed Glare
                ItemStack helmet = player.getInventory().getHelmet();
                int glareLevel = CustomEnchant.GREEN_EYED_GLARE.getLevel(helmet);
                if (glareLevel > 0) {
                    Location eyeLoc = player.getEyeLocation();
                    RayTraceResult result = player.getWorld().rayTraceEntities(eyeLoc, eyeLoc.getDirection(), 15.0, 0.5,
                            e -> e instanceof Player && !e.equals(player));

                    if (result != null && result.getHitEntity() instanceof Player target) {
                        double hp = Math.round(target.getHealth() * 10.0) / 10.0;
                        int armor = 0;
                        if (target.getAttribute(Attribute.GENERIC_ARMOR) != null) {
                            armor = (int) target.getAttribute(Attribute.GENERIC_ARMOR).getValue();
                        }

                        player.sendActionBar(net.kyori.adventure.text.Component
                                .text("§a" + target.getName() + " §8| §cHP: " + hp + " §8| §bArmor: " + armor));

                        // Spawn eye particle vectors (subtle ender signal / spell near their head)
                        if (Math.random() < 0.2) {
                            Location eyeCenter = target.getEyeLocation().add(0, 0.5, 0);
                            player.getWorld().spawnParticle(Particle.MYCELIUM, eyeCenter, 5, 0.2, 0.1, 0.2, 0);
                        }
                    }
                }
            }
        }, 10L, 10L); // 2 times a second
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCombat(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity victim) {

            // 38. False Idol
            if (victim instanceof Player pVictim && pVictim.isBlocking()) {
                ItemStack shield = pVictim.getInventory().getItemInOffHand();
                if (shield.getType() != Material.SHIELD)
                    shield = pVictim.getInventory().getItemInMainHand();

                int idolLevel = CustomEnchant.FALSE_IDOL.getLevel(shield);
                if (idolLevel > 0 && !PdcUtil.isOnCooldown(pVictim, falseIdolKey)) {
                    event.setCancelled(true);
                    PdcUtil.setCooldown(pVictim, falseIdolKey, 10000L); // 10s cooldown

                    Location origin = pVictim.getLocation().clone();
                    Vector knockback = origin.getDirection().multiply(-1.5).setY(0.2);

                    pVictim.teleport(origin.clone().add(knockback));
                    pVictim.getWorld().playSound(origin, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                    pVictim.getWorld().spawnParticle(Particle.EXPLOSION, origin.add(0, 1, 0), 2);

                    // Spawn Dummy
                    ArmorStand dummy = pVictim.getWorld().spawn(origin, ArmorStand.class, as -> {
                        as.setVisible(false);
                        as.setCustomNameVisible(true);
                        as.customName(net.kyori.adventure.text.Component.text("§cFalse Idol"));
                        if (pVictim.getEquipment() != null) {
                            as.getEquipment().setArmorContents(pVictim.getEquipment().getArmorContents());

                            // Head
                            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                            SkullMeta meta = (SkullMeta) head.getItemMeta();
                            meta.setOwningPlayer(pVictim);
                            head.setItemMeta(meta);
                            as.getEquipment().setHelmet(head);
                        }
                    });

                    Bukkit.getScheduler().runTaskLater(plugin, dummy::remove, 60L); // 3 seconds dummy life
                    return; // Avoid further processing
                }
            }

            Player attacker = event.getDamager() instanceof Player p ? p : null;

            // 31. Mimicry (Attacked)
            if (attacker != null) {
                // If the attacker hits a player with Mimicry
                if (victim instanceof Player pVictim) {
                    ItemStack victimSword = pVictim.getInventory().getItemInMainHand();
                    if (CustomEnchant.MIMICRY.getLevel(victimSword) > 0) {
                        ItemStack attackerWeapon = attacker.getInventory().getItemInMainHand();
                        int highestLvl = 0;
                        CustomEnchant highestEnchant = null;

                        for (CustomEnchant ce : CustomEnchant.values()) {
                            int lvl = ce.getLevel(attackerWeapon);
                            if (lvl > highestLvl) {
                                highestLvl = lvl;
                                highestEnchant = ce;
                            }
                        }

                        if (highestEnchant != null) {
                            pVictim.sendMessage("§aMimicked: " + highestEnchant.getDisplayName());
                            pVictim.getPersistentDataContainer().set(mimicKey, PersistentDataType.STRING,
                                    highestEnchant.name());
                            // Clear mimicry after 10 seconds
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                pVictim.getPersistentDataContainer().remove(mimicKey);
                            }, 200L);
                            pVictim.getWorld().spawnParticle(Particle.PORTAL, pVictim.getLocation().add(0, 1, 0), 20,
                                    0.5, 0.5, 0.5, 0.2);
                        }
                    }
                }

                ItemStack weapon = attacker.getInventory().getItemInMainHand();

                // 32. Usurper's Blade
                int usurperLevel = CustomEnchant.USURPERS_BLADE.getLevel(weapon);
                if (usurperLevel > 0) {
                    double myMaxHp = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                    double theirMaxHp = victim.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null
                            ? victim.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()
                            : 0;
                    if (theirMaxHp > myMaxHp) {
                        event.setDamage(event.getDamage() * 1.30); // Deals +30% damage
                        victim.getWorld().spawnParticle(Particle.SQUID_INK, victim.getLocation().add(0, 1, 0), 10, 0.3,
                                0.3, 0.3, 0);
                    }
                }

                // 34. Thief of Buffs
                int thiefLevel = CustomEnchant.THIEF_OF_BUFFS.getLevel(weapon);
                if (thiefLevel > 0) {
                    for (PotionEffect effect : victim.getActivePotionEffects()) {
                        PotionEffectType type = effect.getType();
                        // simplistic positive effect check
                        if (isPositiveEffect(type)) {
                            victim.removePotionEffect(type);
                            attacker.addPotionEffect(effect);
                            attacker.getWorld().spawnParticle(Particle.ENTITY_EFFECT,
                                    attacker.getLocation().add(0, 1, 0),
                                    10, 0.5, 0.5, 0.5, 1, org.bukkit.Color.fromRGB(0, 255, 0)); // Green spell
                            break; // Just steal one
                        }
                    }
                }

                // 35. Spiteful Sabotage
                int sabotageLevel = CustomEnchant.SPITEFUL_SABOTAGE.getLevel(weapon);
                if (sabotageLevel > 0) {
                    if (victim.getEquipment() != null) {
                        for (ItemStack armor : victim.getEquipment().getArmorContents()) {
                            if (armor != null && !armor.getType().isAir()) {
                                armor.damage(2 * sabotageLevel, attacker); // 2x normal rate extra damage
                                victim.getWorld().spawnParticle(Particle.BLOCK, victim.getLocation().add(0, 1, 0), 3,
                                        0.2, 0.2, 0.2, Bukkit.createBlockData(Material.IRON_BLOCK));
                            }
                        }
                    }
                }

                // 39. Doppelganger
                int doppelgangerLevel = CustomEnchant.DOPPELGANGER.getLevel(weapon);
                if (doppelgangerLevel > 0 && Math.random() < 0.1) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (!victim.isDead()) {
                            victim.damage(event.getFinalDamage()); // Direct damage copy
                            victim.getWorld().spawnParticle(Particle.SWEEP_ATTACK, victim.getLocation().add(0, 1, 0),
                                    1);
                        }
                    }, 5L); // split-second later (0.25s)
                }

                // Apply Mimicked Enchant
                if (attacker.getPersistentDataContainer().has(mimicKey, PersistentDataType.STRING)) {
                    String enchName = attacker.getPersistentDataContainer().get(mimicKey, PersistentDataType.STRING);
                    try {
                        CustomEnchant mimicked = CustomEnchant.valueOf(enchName);
                        // Hacky way to simulate the applied enchant if it's one of the ones in this
                        // event
                        // Note: A full implementation would pipe this into all listeners, but for the
                        // showcase:
                        if (mimicked == CustomEnchant.DOPPELGANGER) {
                            if (Math.random() < 0.1)
                                victim.damage(event.getFinalDamage());
                        } else if (mimicked == CustomEnchant.USURPERS_BLADE) {
                            event.setDamage(event.getDamage() * 1.30);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {

            // 37. Parasitic Link
            ItemStack chestplate = player.getInventory().getChestplate();
            int parasiteLevel = CustomEnchant.PARASITIC_LINK.getLevel(chestplate);

            if (parasiteLevel > 0) {
                double reduced = event.getDamage() * 0.5;
                event.setDamage(reduced); // Take 50% less

                // Find nearest entity to transfer damage
                LivingEntity nearest = null;
                double closestDist = Double.MAX_VALUE;
                for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), 10, 10, 10)) {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        double d = le.getLocation().distanceSquared(player.getLocation());
                        if (d < closestDist) {
                            closestDist = d;
                            nearest = le;
                        }
                    }
                }

                if (nearest != null) {
                    nearest.damage(reduced);

                    // Draw tether vector
                    Vector dir = nearest.getLocation().toVector().subtract(player.getLocation().toVector());
                    double dist = dir.length();
                    dir.normalize();
                    for (double i = 0; i < dist; i += 0.5) {
                        Location point = player.getLocation().add(0, 1, 0).add(dir.clone().multiply(i));
                        player.getWorld().spawnParticle(Particle.DUST, point, 1, 0, 0, 0, 0,
                                new Particle.DustOptions(org.bukkit.Color.PURPLE, 0.8f));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player shooter) {
            ItemStack bow = arrow.getWeapon() != null ? arrow.getWeapon() : shooter.getInventory().getItemInMainHand();

            // 33. Shadow Clone
            int cloneLevel = CustomEnchant.SHADOW_CLONE.getLevel(bow);
            if (cloneLevel > 0) {
                Location hitLoc = event.getHitEntity() != null ? event.getHitEntity().getLocation()
                        : (event.getHitBlock() != null ? event.getHitBlock().getLocation() : arrow.getLocation());

                hitLoc.getWorld().spawnParticle(Particle.WITCH, hitLoc, 20, 0.5, 0.5, 0.5, 0.1);
                ArmorStand clone = hitLoc.getWorld().spawn(hitLoc, ArmorStand.class, as -> {
                    as.setInvisible(true);
                    as.getEquipment().setArmorContents(shooter.getEquipment().getArmorContents());
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) head.getItemMeta();
                    meta.setOwningPlayer(shooter);
                    head.setItemMeta(meta);
                    as.getEquipment().setHelmet(head);
                });

                // Redirect nearby mob AI to the clone
                hitLoc.getWorld().getNearbyEntities(clone.getLocation(), 10, 10, 10).forEach(e -> {
                    if (e instanceof Mob mob) {
                        mob.setTarget(clone);
                    }
                });

                Bukkit.getScheduler().runTaskLater(plugin, clone::remove, 100L); // 5 sec
            }
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY && event.getCaught() instanceof Player target) {
            Player fisher = event.getPlayer();
            ItemStack rod = fisher.getInventory().getItemInMainHand();

            // 40. Covetous Pull
            int covetousLevel = CustomEnchant.COVETOUS_PULL.getLevel(rod);
            if (covetousLevel > 0 && !PdcUtil.isOnCooldown(fisher, covetousKey)) {
                PdcUtil.setCooldown(fisher, covetousKey, 15000L); // 15s cooldown

                List<ItemStack> armorOptions = new ArrayList<>();
                if (target.getInventory().getHelmet() != null)
                    armorOptions.add(target.getInventory().getHelmet());
                if (target.getInventory().getChestplate() != null)
                    armorOptions.add(target.getInventory().getChestplate());
                if (target.getInventory().getLeggings() != null)
                    armorOptions.add(target.getInventory().getLeggings());
                if (target.getInventory().getBoots() != null)
                    armorOptions.add(target.getInventory().getBoots());

                if (!armorOptions.isEmpty()) {
                    ItemStack stolen = armorOptions.get((int) (Math.random() * armorOptions.size()));

                    // Clear from target
                    if (stolen.equals(target.getInventory().getHelmet()))
                        target.getInventory().setHelmet(null);
                    else if (stolen.equals(target.getInventory().getChestplate()))
                        target.getInventory().setChestplate(null);
                    else if (stolen.equals(target.getInventory().getLeggings()))
                        target.getInventory().setLeggings(null);
                    else if (stolen.equals(target.getInventory().getBoots()))
                        target.getInventory().setBoots(null);

                    // Give to fisher
                    if (fisher.getInventory().addItem(stolen).isEmpty()) {
                        fisher.sendMessage("§eHooked " + target.getName() + "'s armor!");
                        target.sendMessage("§cYour armor was ripped off!");

                        Location loc = target.getLocation().add(0, 1, 0);
                        Vector dir = fisher.getLocation().toVector().subtract(loc.toVector()).normalize();
                        target.getWorld().spawnParticle(Particle.SPLASH, loc, 30, dir.getX(), dir.getY(), dir.getZ(),
                                0.5); // Reverse splash
                    } else {
                        // Inventory full, give it back or drop
                        target.getWorld().dropItemNaturally(target.getLocation(), stolen);
                    }
                }
            }
        }
    }

    private boolean isPositiveEffect(PotionEffectType type) {
        return type.equals(PotionEffectType.SPEED) || type.equals(PotionEffectType.HASTE) ||
                type.equals(PotionEffectType.STRENGTH) || type.equals(PotionEffectType.JUMP_BOOST) ||
                type.equals(PotionEffectType.REGENERATION) || type.equals(PotionEffectType.RESISTANCE) ||
                type.equals(PotionEffectType.FIRE_RESISTANCE) || type.equals(PotionEffectType.WATER_BREATHING) ||
                type.equals(PotionEffectType.INVISIBILITY) || type.equals(PotionEffectType.NIGHT_VISION) ||
                type.equals(PotionEffectType.HEALTH_BOOST) || type.equals(PotionEffectType.ABSORPTION) ||
                type.equals(PotionEffectType.SATURATION) || type.equals(PotionEffectType.SLOW_FALLING) ||
                type.equals(PotionEffectType.CONDUIT_POWER) || type.equals(PotionEffectType.DOLPHINS_GRACE);
    }
}
