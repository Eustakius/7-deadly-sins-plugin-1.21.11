package com.seven.deadlysins.features;

import com.seven.deadlysins.SevenDeadlySins;
import com.seven.deadlysins.registry.CustomEnchant;
import org.bukkit.Material;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Warden;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AcquisitionListener implements Listener {

    private final Random random = new Random();

    public AcquisitionListener(SevenDeadlySins plugin) {
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null)
            return;

        CustomEnchant[] enchants = CustomEnchant.values();
        CustomEnchant selected = enchants[random.nextInt(enchants.length)];
        int level = 1;

        if (event.getEntity() instanceof Warden) {
            // Guaranteed High-Level Drop
            level = 3 + random.nextInt(Math.max(1, selected.getMaxLevel() - 2));
            level = Math.min(level, selected.getMaxLevel());
            event.getDrops().add(CustomEnchant.createBook(selected, level));
        } else if (event.getEntity() instanceof Wither) {
            // Guaranteed Mid-Level Drop
            level = 2 + (random.nextBoolean() ? 1 : 0);
            level = Math.min(level, selected.getMaxLevel());
            event.getDrops().add(CustomEnchant.createBook(selected, level));
        } else if (event.getEntity() instanceof ElderGuardian) {
            // Guaranteed Low-Level Drop
            level = 1 + (random.nextBoolean() ? 1 : 0);
            level = Math.min(level, selected.getMaxLevel());
            event.getDrops().add(CustomEnchant.createBook(selected, level));
        }
    }

    @EventHandler
    public void onVillagerTrade(VillagerAcquireTradeEvent event) {
        if (event.getEntity().getType() != EntityType.VILLAGER)
            return;

        org.bukkit.entity.Villager villager = (org.bukkit.entity.Villager) event.getEntity();
        if (villager.getProfession() != org.bukkit.entity.Villager.Profession.LIBRARIAN)
            return;

        // Only at Master level (5)
        if (villager.getVillagerLevel() < 5)
            return;

        if (random.nextDouble() < 0.5) {
            CustomEnchant[] enchants = CustomEnchant.values();
            CustomEnchant selected = enchants[random.nextInt(enchants.length)];

            ItemStack tome = CustomEnchant.createBook(selected, 1);

            // 64 Emeralds + 1 Heart of the Sea for 1 Lost Tome
            List<ItemStack> ingredients = new ArrayList<>();
            ingredients.add(new ItemStack(Material.EMERALD, 64));
            ingredients.add(new ItemStack(Material.HEART_OF_THE_SEA, 1));

            MerchantRecipe recipe = new MerchantRecipe(tome, 1); // 1 max use initially to make it rare
            recipe.setIngredients(ingredients);
            recipe.setExperienceReward(true);
            recipe.setVillagerExperience(50);

            event.setRecipe(recipe);
        }
    }
}
