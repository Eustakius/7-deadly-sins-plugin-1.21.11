package com.seven.deadlysins.commands;

import com.seven.deadlysins.registry.CustomEnchant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SinsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("7sins.admin")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§cUsage: /7sins <give|book> <player> <enchantment> [level]");
            return true;
        }

        if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("book")) {
            boolean isBook = args[0].equalsIgnoreCase("book");

            if (args.length < 3) {
                sender.sendMessage("§cUsage: /7sins " + args[0].toLowerCase() + " <player> <enchantment> [level]");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found.");
                return true;
            }

            String enchantName = args[2].toUpperCase();
            CustomEnchant enchant;
            try {
                enchant = CustomEnchant.valueOf(enchantName);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cInvalid enchantment. Use Tab completion.");
                return true;
            }

            int level = 1;
            if (args.length >= 4) {
                try {
                    level = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cLevel must be a number.");
                    return true;
                }
            }

            if (isBook) {
                ItemStack book = new ItemStack(org.bukkit.Material.ENCHANTED_BOOK);
                org.bukkit.inventory.meta.ItemMeta meta = book.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("§6Lost Tome: §e" + enchant.getDisplayName());
                    meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
                    meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                    book.setItemMeta(meta);
                }
                enchant.apply(book, level);

                target.getInventory().addItem(book);
                sender.sendMessage(
                        "§aGave " + target.getName() + " a " + enchant.getDisplayName() + " " + level + " book.");
                target.sendMessage("§aYou received a " + enchant.getDisplayName() + " " + level + " book.");
            } else {
                ItemStack item = target.getInventory().getItemInMainHand();
                if (item.getType().isAir()) {
                    sender.sendMessage("§cThe player must be holding an item to enchant.");
                    return true;
                }

                enchant.apply(item, level);
                sender.sendMessage("§aSuccessfully applied " + enchant.getDisplayName() + " " + level + " to "
                        + target.getName() + "'s held item.");
                target.sendMessage(
                        "§aYour held item was enchanted with " + enchant.getDisplayName() + " " + level + ".");
            }
            return true;
        }

        sender.sendMessage("§cUnknown subcommand.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("7sins.admin"))
            return new ArrayList<>();

        if (args.length == 1) {
            return Arrays.asList("give", "book");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("book"))) {
            return null; // Return null implies online player names in Bukkit API
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("book"))) {
            String partial = args[2].toUpperCase();
            return Arrays.stream(CustomEnchant.values())
                    .map(Enum::name)
                    .filter(name -> name.startsWith(partial))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
