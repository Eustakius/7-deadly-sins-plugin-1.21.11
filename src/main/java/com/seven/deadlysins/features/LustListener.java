package com.seven.deadlysins.features;

import com.seven.deadlysins.SevenDeadlySins;
import com.seven.deadlysins.registry.CustomEnchant;
import com.seven.deadlysins.utils.PdcUtil;
import com.seven.deadlysins.utils.VisualUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LustListener implements Listener {

    private final SevenDeadlySins plugin;
    private final NamespacedKey bloodlustSpeedKey;
    private final NamespacedKey bloodlustAtkKey;
    private final Map<UUID, Integer> bloodlustStacks = new HashMap<>(); // Stacks
    private final Map<UUID, Long> bloodlustExpiry = new HashMap<>(); // Expiry

    public LustListener(SevenDeadlySins plugin) {
        this.plugin = plugin;
        this.bloodlustSpeedKey = new NamespacedKey(plugin, "bloodlust_speed_mod");
        this.bloodlustAtkKey = new NamespacedKey(plugin, "bloodlust_atk_mod");

        startTasks();
    }

    private void startTasks() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();

            for (Map.Entry<UUID, Long> entry : new HashMap<>(bloodlustExpiry).entrySet()) {
                if (now > entry.getValue()) {
                    bloodlustStacks.remove(entry.getKey());
                    bloodlustExpiry.remove(entry.getKey());
                }
            }

            // Bloodlust unequip and orphan cleanup
            for (Player p : Bukkit.getOnlinePlayers()) {
                ItemStack weapon = p.getInventory().getItemInMainHand();
                if (CustomEnchant.BLOODLUST.getLevel(weapon) == 0 || !bloodlustExpiry.containsKey(p.getUniqueId())) {
                    bloodlustStacks.remove(p.getUniqueId());
                    bloodlustExpiry.remove(p.getUniqueId());
                    removeBloodlust(p);
                }
            }
        }, 20L, 20L);
    }

    private void removeBloodlust(Player p) {
        AttributeInstance speed = p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speed != null) {
            for (AttributeModifier m : speed.getModifiers()) {
                if (m.getName().equals("bloodlust_speed_mod"))
                    speed.removeModifier(m);
            }
        }
        AttributeInstance atk = p.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (atk != null) {
            for (AttributeModifier m : atk.getModifiers()) {
                if (m.getName().equals("bloodlust_atk_mod"))
                    atk.removeModifier(m);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCombat(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof LivingEntity victim) {
            ItemStack weapon = attacker.getInventory().getItemInMainHand();

            // 62. Bloodlust
            int bloodLevel = CustomEnchant.BLOODLUST.getLevel(weapon);
            if (bloodLevel > 0) {
                int stacks = bloodlustStacks.getOrDefault(attacker.getUniqueId(), 0);
                if (stacks < 10)
                    stacks++; // Cap at 10 stacks (50%)

                bloodlustStacks.put(attacker.getUniqueId(), stacks);
                bloodlustExpiry.put(attacker.getUniqueId(), System.currentTimeMillis() + 5000L); // 5 sec expiry

                removeBloodlust(attacker); // Remove old

                AttributeInstance speed = attacker.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                if (speed != null) {
                    speed.addModifier(new AttributeModifier(bloodlustSpeedKey, 0.05 * stacks,
                            AttributeModifier.Operation.ADD_SCALAR));
                }
                AttributeInstance atk = attacker.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                if (atk != null) {
                    atk.addModifier(new AttributeModifier(bloodlustAtkKey, 0.05 * stacks,
                            AttributeModifier.Operation.ADD_SCALAR));
                }

                VisualUtil.playVisual(attacker.getLocation().add(0, 1, 0), null, CustomEnchant.BLOODLUST, 1.0);
            }

            // 63. Succubus Kiss (Axe)
            if (weapon.getType().name().contains("AXE")) {
                int kissLevel = CustomEnchant.SUCCUBUS_KISS.getLevel(weapon);
                if (kissLevel > 0 && victim instanceof Player pVictim) {
                    int steal = Math.min(pVictim.getTotalExperience(), 2 * kissLevel);
                    if (steal > 0) {
                        pVictim.giveExp(-steal);
                        attacker.setHealth(Math.min(attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
                                attacker.getHealth() + (steal * 0.5)));
                        VisualUtil.playVisual(victim.getLocation().add(0, 1, 0), null, CustomEnchant.SUCCUBUS_KISS,
                                1.0);
                    }
                }
            }

            // 64. Fatal Attraction (Sword)
            int fatalLevel = CustomEnchant.FATAL_ATTRACTION.getLevel(weapon);
            if (fatalLevel > 0) {
                org.bukkit.Location cent = attacker.getLocation();
                org.bukkit.Location visualLoc = cent.clone().add(0, 1, 0);
                VisualUtil.playVisual(visualLoc, null, CustomEnchant.FATAL_ATTRACTION, 1.0);
                cent.getWorld().getNearbyEntities(cent, 4, 4, 4).forEach(e -> {
                    if (e instanceof LivingEntity && !e.equals(attacker)) {
                        double pullPower = 0.4;
                        // Synergy: Frenzied Attraction
                        if (CustomEnchant.BERSERKERS_RAGE.getLevel(attacker.getInventory().getItemInMainHand()) > 0) {
                            pullPower = com.seven.deadlysins.logic.SynergyManager.evaluateSynergy(attacker,
                                    (LivingEntity) e,
                                    com.seven.deadlysins.logic.SynergyManager.SynergyType.FRENZIED_ATTRACTION,
                                    pullPower);
                            if (Math.random() < 0.1)
                                attacker.sendMessage("§c§lSYNERGY: §dFrenzied Attraction pulse!");
                        }

                        Vector pull = attacker.getLocation().toVector().subtract(e.getLocation().toVector()).normalize()
                                .multiply(pullPower);
                        if (e.getLocation().distanceSquared(attacker.getLocation()) > 1.0) {
                            e.setVelocity(e.getVelocity().add(pull));
                        }
                    }
                });
            }

            // 70. Intoxicating Strike (Sword)
            int intoxLevel = CustomEnchant.INTOXICATING_STRIKE.getLevel(weapon);
            if (intoxLevel > 0 && victim instanceof Player pVictim && Math.random() < 0.2 * intoxLevel) {
                org.bukkit.Location scrambled = pVictim.getLocation();
                scrambled.setYaw((float) (Math.random() * 360));
                scrambled.setPitch((float) ((Math.random() * 180) - 90));

                com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook rotationPacket = new com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook(
                        scrambled.getX(), scrambled.getY(), scrambled.getZ(),
                        scrambled.getYaw(), scrambled.getPitch(), (byte) 0, 0, true);
                PacketEvents.getAPI().getPlayerManager().sendPacket(pVictim, rotationPacket);

                VisualUtil.playVisual(pVictim.getLocation().add(0, 1, 0), null, CustomEnchant.INTOXICATING_STRIKE, 1.0);
            }
        }

        // 68. Captivation
        if (event.getEntity() instanceof Player pVictim && pVictim.isBlocking()) {
            ItemStack shield = pVictim.getInventory().getItemInOffHand();
            if (shield.getType() != Material.SHIELD)
                shield = pVictim.getInventory().getItemInMainHand();

            int capLevel = CustomEnchant.CAPTIVATION.getLevel(shield);
            if (capLevel > 0 && event.getDamager() instanceof LivingEntity attacker) {
                Vector dirToTarget = pVictim.getLocation().toVector().subtract(attacker.getLocation().toVector())
                        .normalize();
                if (dirToTarget.dot(attacker.getLocation().getDirection()) > 0.5) { // Looking at shield
                    event.setDamage(event.getDamage() * 0.6); // -40% damage
                    VisualUtil.playVisual(attacker.getLocation().add(0, 1.5, 0), null, CustomEnchant.CAPTIVATION, 1.0);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player shooter) {
            ItemStack bow = arrow.getWeapon() != null ? arrow.getWeapon() : shooter.getInventory().getItemInMainHand();

            // 61. Siren's Song
            int sirenLevel = CustomEnchant.SIRENS_SONG.getLevel(bow);
            if (sirenLevel > 0 && event.getHitEntity() instanceof Mob mob) {
                LivingEntity nearest = null;
                double dist = Double.MAX_VALUE;
                for (Entity e : mob.getWorld().getNearbyEntities(mob.getLocation(), 15, 15, 15)) {
                    if (e instanceof Mob hostile && !e.equals(mob) && !e.equals(shooter)) {
                        double d = e.getLocation().distanceSquared(mob.getLocation());
                        if (d < dist) {
                            dist = d;
                            nearest = hostile;
                        }
                    }
                }
                if (nearest != null) {
                    mob.setTarget(nearest);
                    VisualUtil.playVisual(mob.getLocation().add(0, 2, 0), null, CustomEnchant.SIRENS_SONG, 1.0);
                }
            }

            // 67. Betrayal
            int betrayalLevel = CustomEnchant.BETRAYAL.getLevel(bow);
            if (betrayalLevel > 0 && event.getHitEntity() instanceof Mob mob && arrow.getPierceLevel() > 0) {
                // If pierce, the same logic roughly applies. Force them to hit nearby.
                mob.getWorld().getNearbyEntities(mob.getLocation(), 10, 10, 10).forEach(e -> {
                    if (e instanceof Mob other && !e.equals(mob)) {
                        mob.setTarget(other);
                        other.setTarget(mob);
                    }
                });
                VisualUtil.playVisual(mob.getLocation().add(0, 2, 0), null, CustomEnchant.BETRAYAL, 1.0);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLethalDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getFinalDamage() >= player.getHealth()) {

                // 66. Masquerade
                ItemStack helmet = player.getInventory().getHelmet();
                int masLevel = CustomEnchant.MASQUERADE.getLevel(helmet);
                if (masLevel > 0 && !PdcUtil.isOnCooldown(player, new NamespacedKey(plugin, "masquerade_cd"))) {
                    PdcUtil.setCooldown(player, new NamespacedKey(plugin, "masquerade_cd"), 60000L); // 1m cd
                    event.setCancelled(true);
                    player.setHealth(Math.max(1.0, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0));

                    org.bukkit.Location origin = player.getLocation();
                    org.bukkit.Location tele = origin.clone().add((Math.random() - 0.5) * 10, 0,
                            (Math.random() - 0.5) * 10);
                    tele.setY(origin.getWorld().getHighestBlockYAt(tele) + 1);
                    player.teleport(tele);

                    VisualUtil.playVisual(origin, null, CustomEnchant.MASQUERADE, 1.0);
                    origin.getWorld().playSound(origin, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1f, 1f);
                    return;
                }
            }

            // 65. Illusionist's Veil
            ItemStack chest = player.getInventory().getChestplate();
            int veilLevel = CustomEnchant.ILLUSIONISTS_VEIL.getLevel(chest);
            if (veilLevel > 0 && event.getFinalDamage() > 5.0
                    && !PdcUtil.isOnCooldown(player, new NamespacedKey(plugin, "veil_cd"))) {
                PdcUtil.setCooldown(player, new NamespacedKey(plugin, "veil_cd"), 20000L); // 20s cd

                for (int i = 0; i < 3; i++) {
                    int entityId = PacketEvents.getAPI().getServerManager().getVersion().getProtocolVersion() * 1000
                            + (int) (Math.random() * 100000);
                    UUID randomUUID = UUID.randomUUID();
                    UserProfile profile = new UserProfile(randomUUID, player.getName());

                    com.github.retrooper.packetevents.protocol.world.Location peSpawnLoc = SpigotConversionUtil
                            .fromBukkitLocation(player.getLocation());

                    WrapperPlayServerPlayerInfo infoPacket = new WrapperPlayServerPlayerInfo(
                            WrapperPlayServerPlayerInfo.Action.ADD_PLAYER,
                            new WrapperPlayServerPlayerInfo.PlayerData(null, profile, GameMode.SURVIVAL, 0));

                    WrapperPlayServerSpawnPlayer spawnPacket = new WrapperPlayServerSpawnPlayer(entityId, randomUUID,
                            peSpawnLoc);

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        PacketEvents.getAPI().getPlayerManager().sendPacket(online, infoPacket);
                        PacketEvents.getAPI().getPlayerManager().sendPacket(online, spawnPacket);
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        WrapperPlayServerPlayerInfo removePacket = new WrapperPlayServerPlayerInfo(
                                WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER,
                                new WrapperPlayServerPlayerInfo.PlayerData(null, profile, null, 0));

                        com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities destroyPacket = new com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities(
                                entityId);

                        for (Player online : Bukkit.getOnlinePlayers()) {
                            PacketEvents.getAPI().getPlayerManager().sendPacket(online, removePacket);
                            PacketEvents.getAPI().getPlayerManager().sendPacket(online, destroyPacket);
                        }
                    }, 60L); // 3 sec lifespan
                }
                VisualUtil.playVisual(player.getLocation().add(0, 1, 0), null, CustomEnchant.ILLUSIONISTS_VEIL, 1.0);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPetDamage(EntityDamageEvent event) {
        // 69. Lover's Sacrifice
        if (event.getEntity() instanceof Tameable pet && pet.isTamed() && pet.getOwner() instanceof Player owner) {
            if (event.getFinalDamage() >= ((LivingEntity) pet).getHealth()) {
                if (owner.isOnline() && owner.getWorld() == event.getEntity().getWorld()
                        && owner.getLocation().distanceSquared(pet.getLocation()) < 400) { // 20 blocks
                    ItemStack legs = owner.getInventory().getLeggings();
                    if (CustomEnchant.LOVERS_SACRIFICE.getLevel(legs) > 0) {
                        event.setDamage(0); // Save pet
                        owner.damage(event.getFinalDamage()); // Transfer to owner

                        VisualUtil.playVisual(owner.getLocation(), null, CustomEnchant.LOVERS_SACRIFICE, 1.0);
                        owner.sendMessage("§cYou intercepted a lethal blow meant for your pet!");
                    }
                }
            }
        }
    }
}
