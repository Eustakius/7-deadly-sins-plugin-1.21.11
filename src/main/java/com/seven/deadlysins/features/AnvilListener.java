package com.seven.deadlysins.features;

import com.seven.deadlysins.registry.CustomEnchant;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class AnvilListener implements Listener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();
        ItemStack first = inv.getItem(0);
        ItemStack second = inv.getItem(1);

        if (first == null || first.getType().isAir() || second == null || second.getType().isAir()) {
            return;
        }

        // We only want to apply enchants to tools/weapons/armor, not random blocks
        if (first.getType().isBlock() && first.getType() != Material.SHIELD)
            return;

        Map<CustomEnchant, Integer> enchantsToTransfer = new HashMap<>();

        // Scan the second item (the sacrifice) for custom enchantments
        for (CustomEnchant enchant : CustomEnchant.values()) {
            int sacrificeLevel = enchant.getLevel(second);
            if (sacrificeLevel > 0) {
                int baseLevel = enchant.getLevel(first);
                int newLevel = sacrificeLevel;

                // Anvil combining math
                if (baseLevel == sacrificeLevel) {
                    newLevel = Math.min(baseLevel + 1, enchant.getMaxLevel());
                } else if (baseLevel > sacrificeLevel) {
                    newLevel = baseLevel;
                }

                enchantsToTransfer.put(enchant, newLevel);
            }
        }

        if (enchantsToTransfer.isEmpty())
            return;

        // Clone the first item so we can add our custom enchants to the final result
        // natively
        ItemStack result = event.getResult();
        if (result == null || result.getType().isAir()) {
            result = first.clone();
        }

        // Apply all custom enchants
        int customCost = 0;
        for (Map.Entry<CustomEnchant, Integer> entry : enchantsToTransfer.entrySet()) {
            entry.getKey().apply(result, entry.getValue());
            customCost += (entry.getValue() * 4); // 4 levels of XP cost per custom enchant level
        }

        // Fix repairing mechanic naming (Vanilla Anvil behavior)
        if (inv.getRenameText() != null && !inv.getRenameText().isEmpty()) {
            ItemMeta resMeta = result.getItemMeta();
            if (resMeta != null) {
                resMeta.setDisplayName(inv.getRenameText());
                result.setItemMeta(resMeta);
                customCost += 1; // standard rename cost
            }
        }

        // Only override if the result actually changed
        event.setResult(result);

        // Increase the repair cost slightly to charge the player XP for applying custom
        // enchants
        int finalCost = inv.getRepairCost() + customCost;
        if (finalCost < 1)
            finalCost = 1;

        // Schedule to set the cost AFTER the event handles its native event
        // cancellation stuff
        final int fc = finalCost;
        org.bukkit.Bukkit.getScheduler().runTask(com.seven.deadlysins.SevenDeadlySins.getInstance(), () -> {
            if (event.getView().getPlayer() != null) {
                inv.setRepairCost(fc);
            }
        });
    }
}
