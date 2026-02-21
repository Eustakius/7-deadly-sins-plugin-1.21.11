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
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.Collection;

public class GreedListener implements Listener {

    private final SevenDeadlySins plugin;
    private final NamespacedKey hoarderModKey;
    private final NamespacedKey extortionKey;

    public GreedListener(SevenDeadlySins plugin) {
        this.plugin = plugin;
        this.hoarderModKey = new NamespacedKey(plugin, "hoarders_vit");
        this.extortionKey = new NamespacedKey(plugin, "extortion_lock");

        startHoarderTask();
    }

    private void startHoarderTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {

                // 24. Hoarder's Vitality
                ItemStack chestplate = player.getInventory().getChestplate();
                int hoarderLevel = CustomEnchant.HOARDERS_VITALITY.getLevel(chestplate);

                AttributeInstance maxHp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (maxHp != null) {
                    // Remove old mod
                    for (AttributeModifier mod : maxHp.getModifiers()) {
                        if (mod.getKey().equals(hoarderModKey)) {
                            maxHp.removeModifier(mod);
                        }
                    }

                    if (hoarderLevel > 0) {
                        int count = 0;
                        for (ItemStack item : player.getInventory().getContents()) {
                            if (item != null && (item.getType() == Material.GOLD_INGOT
                                    || item.getType() == Material.GOLD_BLOCK || item.getType() == Material.EMERALD
                                    || item.getType() == Material.EMERALD_BLOCK)) {
                                count += item.getAmount() * (item.getType().name().contains("BLOCK") ? 9 : 1);
                            }
                        }

                        // Limit to extra +20 HP (10 hearts) max, 0.1 HP per valuable
                        double bonus = Math.min(20.0, count * 0.1 * hoarderLevel);
                        maxHp.addModifier(
                                new AttributeModifier(hoarderModKey, bonus, AttributeModifier.Operation.ADD_NUMBER));

                        if (Math.random() < 0.05 && bonus > 0) {
                            player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 1, 0.5,
                                    0.5, 0.5);
                        } // Custom recolor to yellow would require ParticleBuilder/API which was left out
                          // of standard imports for simplicity
                    } else if (player.getHealth() > maxHp.getValue()) {
                        player.setHealth(maxHp.getValue());
                    }
                }

                // 29. Treasure Hunter
                ItemStack boots = player.getInventory().getBoots();
                if (CustomEnchant.TREASURE_HUNTER.getLevel(boots) > 0 && player.isSneaking()) {
                    Block nearestChest = null;
                    double closestDist = Double.MAX_VALUE;

                    // Note: scanning entire chunk in main thread is slow, ideal is async but
                    // simplified here
                    for (BlockState state : player.getLocation().getChunk().getTileEntities()) {
                        if (state.getType() == Material.CHEST || state.getType() == Material.TRAPPED_CHEST) {
                            double d = state.getLocation().distanceSquared(player.getLocation());
                            if (d < closestDist) {
                                closestDist = d;
                                nearestChest = state.getBlock();
                            }
                        }
                    }

                    if (nearestChest != null) {
                        Vector dir = nearestChest.getLocation().toVector().subtract(player.getLocation().toVector())
                                .normalize();
                        Location particleLoc = player.getEyeLocation().add(dir.multiply(1.5));
                        player.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
                    }
                }
            }
        }, 20L, 20L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCombat(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity target))
            return;

        Player attacker = event.getDamager() instanceof Player p ? p : null;
        if (attacker != null) {
            ItemStack weapon = attacker.getInventory().getItemInMainHand();

            // 21. Highwayman's Toll
            int highwayLevel = CustomEnchant.HIGHWAYMANS_TOLL.getLevel(weapon);
            if (highwayLevel > 0 && target instanceof Player pTarget) {
                if (pTarget.getTotalExperience() > 0) {
                    int stealAmt = Math.min(pTarget.getTotalExperience(), 5 * highwayLevel);
                    pTarget.giveExp(-stealAmt);
                    attacker.giveExp(stealAmt);
                    target.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, target.getLocation().add(0, 1, 0), 5, 0.5,
                            0.5, 0.5, 0);
                }
            }

            // 23. Plunderer's Strike
            int plunderLevel = CustomEnchant.PLUNDERERS_STRIKE.getLevel(weapon);
            if (plunderLevel > 0 && Math.random() < 0.05 * plunderLevel) {
                ItemStack targetItem = target.getEquipment().getItemInMainHand();
                if (targetItem != null && !targetItem.getType().isAir()) {
                    target.getWorld().dropItemNaturally(target.getLocation(), targetItem.clone());
                    target.getEquipment().setItemInMainHand(null);
                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
                    target.getWorld().spawnParticle(Particle.ENCHANT, target.getLocation().add(0, 1, 0), 10, 0.5, 0.5,
                            0.5, 0.1);
                }
            }

            // 25. Mercenary's Fortune
            int mercenaryLevel = CustomEnchant.MERCENARYS_FORTUNE.getLevel(weapon);
            if (mercenaryLevel > 0 && target.getEquipment() != null) {
                boolean hasHighTier = false;
                for (ItemStack armor : target.getEquipment().getArmorContents()) {
                    if (armor != null && (armor.getType().name().contains("DIAMOND")
                            || armor.getType().name().contains("NETHERITE"))) {
                        hasHighTier = true;
                        break;
                    }
                }
                if (hasHighTier) {
                    event.setDamage(event.getDamage() + (3.0 * mercenaryLevel));
                    target.getWorld().spawnParticle(Particle.BLOCK, target.getLocation().add(0, 1, 0), 5, 0.3, 0.3, 0.3,
                            Bukkit.createBlockData(Material.EMERALD_BLOCK));
                }
            }

            // 28. Pickpocket
            int pickpocketLevel = CustomEnchant.PICKPOCKET.getLevel(weapon);
            if (pickpocketLevel > 0 && attacker.isSneaking() && target instanceof Player pTarget) {
                // Must be striking from behind roughly
                Vector dirToTarget = target.getLocation().toVector().subtract(attacker.getLocation().toVector())
                        .normalize();
                if (dirToTarget.dot(target.getLocation().getDirection()) > 0.5
                        && Math.random() < 0.01 * pickpocketLevel) { // 1%
                    int slot = (int) (Math.random() * 9);
                    ItemStack stolen = pTarget.getInventory().getItem(slot);
                    if (stolen != null && !stolen.getType().isAir()) {
                        pTarget.getInventory().setItem(slot, null);
                        // Try to add to attacker
                        if (attacker.getInventory().addItem(stolen).isEmpty()) {
                            attacker.sendMessage("§eStole " + stolen.getType() + " from " + pTarget.getName() + "!");
                            pTarget.sendMessage("§cYou were pickpocketed!");
                            target.getWorld().spawnParticle(Particle.SMOKE, target.getLocation().add(0, 1, 0), 10, 0.2,
                                    0.2, 0.2, 0.05);
                        } else {
                            // Give it back if inventory full
                            pTarget.getInventory().setItem(slot, stolen);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFatalHit(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            // 26. Dragon's Hoard
            if (event.getFinalDamage() >= player.getHealth()) {
                ItemStack leggings = player.getInventory().getLeggings();
                if (CustomEnchant.DRAGONS_HOARD.getLevel(leggings) > 0) {
                    PlayerInventory inv = player.getInventory();
                    boolean saved = false;
                    Material[] ores = { Material.NETHERITE_INGOT, Material.DIAMOND, Material.EMERALD };
                    for (Material m : ores) {
                        int index = inv.first(m);
                        if (index != -1) {
                            ItemStack item = inv.getItem(index);
                            if (item != null) {
                                item.setAmount(item.getAmount() - 1);
                                saved = true;
                                break;
                            }
                        }
                    }
                    if (saved) {
                        event.setCancelled(true);
                        player.setHealth(
                                Math.max(1.0, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.2));
                        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, player.getLocation().add(0, 1, 0), 30,
                                0.5, 0.5, 0.5, 0.1);
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 2f);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player shooter) {
            ItemStack bow = arrow.getWeapon() != null ? arrow.getWeapon() : shooter.getInventory().getItemInMainHand();

            // 27. Extortion
            int extLevel = CustomEnchant.EXTORTION.getLevel(bow);
            if (extLevel > 0 && event.getHitEntity() instanceof Player target) {
                PdcUtil.setCooldown(target, extortionKey, 5000L); // 5s lockdown
                target.getWorld().spawnParticle(Particle.BLOCK, target.getLocation().add(0, 2, 0), 10, 0.3, 0.3, 0.3,
                        Bukkit.createBlockData(Material.BARRIER));
                target.sendMessage("§cYour inventory has been locked by Extortion!");
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player player) {
            if (PdcUtil.isOnCooldown(player, extortionKey)) {
                event.setCancelled(true);
                player.sendMessage("§cYour inventory is locked!");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        // 22. Midas Touch
        int midasLevel = CustomEnchant.MIDAS_TOUCH.getLevel(tool);
        if (midasLevel > 0 && event.getBlock().getType().name().contains("ORE")) {
            event.setDropItems(false);
            Material drop = getSmelted(event.getBlock().getType());
            int yield = 1 + (player.getLevel() / 30);

            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                    new ItemStack(drop, yield));
            event.getBlock().getWorld().spawnParticle(Particle.END_ROD,
                    event.getBlock().getLocation().add(0.5, 0.5, 0.5), 10, 0.5, 0.5, 0.5, 0.1);
        }

        // 30. Usury
        int usuryLevel = CustomEnchant.USURY.getLevel(tool);
        if (usuryLevel > 0 && event.getBlock().getBlockData() instanceof Ageable ageable) {
            if (ageable.getAge() == ageable.getMaximumAge()) {
                event.setDropItems(false);
                Collection<ItemStack> drops = event.getBlock().getDrops(tool);
                for (ItemStack drop : drops) {
                    drop.setAmount(drop.getAmount() * 2);
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
                }
                tool.damage(5, player); // Costs durability
                event.getBlock().getWorld().spawnParticle(Particle.HAPPY_VILLAGER, event.getBlock().getLocation(), 5,
                        0.5, 0.5, 0.5);
            }
        }
    }

    // Simple util for Midas
    private Material getSmelted(Material ore) {
        return switch (ore) {
            case IRON_ORE, DEEPSLATE_IRON_ORE, RAW_IRON_BLOCK -> Material.IRON_INGOT;
            case GOLD_ORE, DEEPSLATE_GOLD_ORE, RAW_GOLD_BLOCK -> Material.GOLD_INGOT;
            case COPPER_ORE, DEEPSLATE_COPPER_ORE, RAW_COPPER_BLOCK -> Material.COPPER_INGOT;
            default -> {
                // Return original material yield for non-smeltable ores
                if (ore.name().contains("DIAMOND"))
                    yield Material.DIAMOND;
                if (ore.name().contains("EMERALD"))
                    yield Material.EMERALD;
                if (ore.name().contains("COAL"))
                    yield Material.COAL;
                if (ore.name().contains("REDSTONE"))
                    yield Material.REDSTONE;
                if (ore.name().contains("LAPIS"))
                    yield Material.LAPIS_LAZULI;
                yield ore;
            }
        };
    }
}
