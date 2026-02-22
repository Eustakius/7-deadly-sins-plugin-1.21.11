package com.seven.deadlysins.features;

import com.seven.deadlysins.SevenDeadlySins;
import com.seven.deadlysins.registry.CustomEnchant;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
            ItemStack tome = createCustomEnchantBook(selected, level);

            // 6. Inject it into the chest's loot drops
            List<ItemStack> drops = event.getLoot();
            drops.add(tome);
            event.setLoot(drops);
        }
    }

    private double getChanceForLootTable(String path) {
        // High Tier / Dangerous structures (5% chance)
        if (path.contains("ancient_city") ||
                path.contains("end_city_treasure") ||
                path.contains("bastion_treasure") ||
                path.contains("stronghold_library") ||
                path.contains("woodland_mansion")) {
            return 0.05; // 5%
        }

        // Mid Tier structures (2% chance)
        if (path.contains("nether_bridge") ||
                path.contains("bastion_other") ||
                path.contains("bastion_bridge") ||
                path.contains("simple_dungeon") ||
                path.contains("desert_pyramid") ||
                path.contains("igloo_chest") ||
                path.contains("jungle_temple")) {
            return 0.02; // 2%
        }

        // Low Tier / Common structures (0.5% chance)
        if (path.contains("village") ||
                path.contains("abandoned_mineshaft") ||
                path.contains("shipwreck") ||
                path.contains("ruined_portal")) {
            return 0.005; // 0.5%
        }

        return 0.0; // No chance for random/other loot tables
    }

    private ItemStack createCustomEnchantBook(CustomEnchant enchant, int level) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Lost Tome: §e" + enchant.getDisplayName());
            // Add a dummy vanilla enchantment just to ensure the glowing effect
            // is natively preserved consistently by the client
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            book.setItemMeta(meta);
        }

        // Use our standard API to inject the PDC and Lore
        enchant.apply(book, level);

        return book;
    }
}
