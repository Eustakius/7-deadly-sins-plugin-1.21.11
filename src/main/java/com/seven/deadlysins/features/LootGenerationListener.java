package com.seven.deadlysins.features;

import com.seven.deadlysins.SevenDeadlySins;
import com.seven.deadlysins.registry.CustomEnchant;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class LootGenerationListener implements Listener {

    private final SevenDeadlySins plugin;
    private final Random random = new Random();

    public LootGenerationListener(SevenDeadlySins plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        // Only trigger for chest/barrel loot tables (not entity drops or fishing)
        if (event.getInventoryHolder() == null)
            return;

        NamespacedKey lootTableKey = event.getLootTable().getKey();
        String keyPath = lootTableKey.getKey().toLowerCase();

        // 1. Determine spawn chance based on the "tier" of the loot table
        double chance = getChanceForLootTable(keyPath);

        if (chance <= 0.0)
            return;

        // 2. Roll the dice to see if a custom enchant generates
        if (random.nextDouble() <= chance) {

            // 3. Pick a random custom enchant
            CustomEnchant[] allEnchants = CustomEnchant.values();
            CustomEnchant selected = allEnchants[random.nextInt(allEnchants.length)];

            // 4. Determine level (weighted towards lower levels)
            int level = 1;
            if (selected.getMaxLevel() > 1) {
                // 60% chance for level 1, 30% for level 2, 10% for level 3
                double lvlRoll = random.nextDouble();
                if (lvlRoll > 0.90 && selected.getMaxLevel() >= 3) {
                    level = 3;
                } else if (lvlRoll > 0.60 && selected.getMaxLevel() >= 2) {
                    level = 2;
                }
            }

            // 5. Create the "Lost Tome" (Enchanted Book with our custom PDC)
            ItemStack tome = CustomEnchant.createBook(selected, level);

            // 6. Inject it into the chest's loot drops
            List<ItemStack> drops = event.getLoot();
            drops.add(tome);
            event.setLoot(drops);
        }
    }

    private double getChanceForLootTable(String path) {
        // High Tier / Dangerous structures (15% chance)
        if (path.contains("ancient_city") ||
                path.contains("end_city_treasure") ||
                path.contains("bastion_treasure") ||
                path.contains("stronghold_library") ||
                path.contains("woodland_mansion")) {
            return 0.15; // 15%
        }

        // Mid Tier structures (7% chance)
        if (path.contains("nether_bridge") ||
                path.contains("bastion_other") ||
                path.contains("bastion_bridge") ||
                path.contains("simple_dungeon") ||
                path.contains("desert_pyramid") ||
                path.contains("igloo_chest") ||
                path.contains("jungle_temple")) {
            return 0.07; // 7%
        }

        // Low Tier / Common structures (2.5% chance)
        if (path.contains("village") ||
                path.contains("abandoned_mineshaft") ||
                path.contains("shipwreck") ||
                path.contains("ruined_portal")) {
            return 0.025; // 2.5%
        }

        return 0.0; // No chance for random/other loot tables
    }

}
