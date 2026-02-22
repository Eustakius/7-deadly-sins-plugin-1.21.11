package com.seven.deadlysins.registry;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.ArrayList;
import java.util.List;

public enum CustomEnchant {
        // ⚔️ Wrath
        BLOOD_EAGLE("Blood Eagle", 1, "Executes targets below 15% HP. Bypasses totem of undying."),
        BERSERKERS_RAGE("Berserker's Rage", 3, "As your HP drops, damage scales up logarithmically."),
        SIEGE_BREAKER("Siege Breaker", 3, "Deals triple damage to shields and instantly breaks armor durability."),
        HELLFIRE_TREBUCHET("Hellfire Trebuchet", 1,
                        "Arrows spawn a 3x3 persistent fire zone (using schedulers, not real fire blocks) that burns entities."),
        WARLORDS_CRY("Warlord's Cry", 3,
                        "Killing a mob grants temporary Strength to all allied players within a 15-block radius."),
        DUELISTS_SPITE("Duelist's Spite", 5,
                        "Consecutive hits on the same target multiply damage. Resets if you hit a different entity."),
        IMPALER("Impaler", 3, "Pins target to the ground (cancels movement vectors) for 2 seconds."),
        SCORCHED_EARTH("Scorched Earth", 3,
                        "Sprinting leaves a trail of temporary \"hot\" blocks that burn pursuers via PlayerMoveEvent."),
        VENGEANCE_STRIKE("Vengeance Strike", 3,
                        "Absorbs the last hit taken and adds 200% of that damage to your next melee swing."),
        MUTILATION("Mutilation", 3, "Applies a custom bleeding DoT that decreases target's healing received by 50%."),

        // 👑 Pride
        KINGS_RESURGENCE("King's Resurgence", 1,
                        "Fatal blow leaves you at 1 HP with 3s invulnerability. 10-minute internal cooldown via NBT."),
        REGAL_PRESENCE("Regal Presence", 3,
                        "Passive aura that constantly pushes non-boss mobs 3 blocks away from you."),
        SOLITARY_MONARCH("Solitary Monarch", 3,
                        "Grants +50% attack speed and damage, but only if no allies are within 20 blocks."),
        CHAMPIONS_CHALLENGE("Champion's Challenge", 1,
                        "Hitting a mob rewrites its AI pathfinding to target ONLY you, ignoring other players."),
        CROWN_OF_THORNS("Crown of Thorns", 3,
                        "Attackers take pure true damage (ignores armor) equal to 10% of their own max HP."),
        ARROGANT_PARRY("Arrogant Parry", 3,
                        "15% chance to auto-parry melee attacks, staggering the attacker (Slowness X)."),
        SOVEREIGNS_REACH("Sovereign's Reach", 3,
                        "Uses raytracing to increase melee attack hit-registration range by 2.5 blocks."),
        GILDED_EXECUTION("Gilded Execution", 3,
                        "Kills have a chance to drop gold nuggets directly into your inventory."),
        UNBOWED("Unbowed", 1, "Complete immunity to knockback and the Slowness potion effect."),
        ROYAL_DECREE("Royal Decree", 3,
                        "Fully charged arrows call down a cosmetic lightning bolt that deals massive AoE shock damage."),

        // 💰 Greed
        HIGHWAYMANS_TOLL("Highwayman's Toll", 3,
                        "Hitting a player drains a small amount of their XP directly into your XP bar."),
        MIDAS_TOUCH("Midas Touch", 1, "Ores auto-smelt and drop yields multiplied by your current XP level."),
        PLUNDERERS_STRIKE("Plunderer's Strike", 3,
                        "5% chance to disarm a mob/player, dropping their held item onto the ground."),
        HOARDERS_VITALITY("Hoarder's Vitality", 3,
                        "Max HP dynamically increases based on the amount of gold/emeralds in your inventory."),
        MERCENARYS_FORTUNE("Mercenary's Fortune", 3,
                        "Deals bonus damage to targets wearing high-tier armor (Diamond/Netherite)."),
        DRAGONS_HOARD("Dragon's Hoard", 3,
                        "Taking fatal damage consumes rare ores from your inventory instead of letting you die."),
        EXTORTION("Extortion", 3,
                        "Arrows that hit players apply a custom tag that disables their ability to open inventories for 5s."),
        PICKPOCKET("Pickpocket", 3,
                        "Sneak-attacking a player has a 1% chance to steal a random item from their hotbar."),
        TREASURE_HUNTER("Treasure Hunter", 1,
                        "Sneaking points a particle beam toward the nearest un-looted chest in the chunk."),
        USURY("Usury", 3, "Replanting crops costs durability, but yields double the crop drops upon next harvest."),

        // 🐍 Envy
        MIMICRY("Mimicry", 1, "Temporarily copies the highest level enchantment from the last weapon that hit you."),
        USURPERS_BLADE("Usurper's Blade", 3, "Deals +30% damage to players who have more max health than you."),
        SHADOW_CLONE("Shadow Clone", 1,
                        "Arrows spawn an invisible Armor Stand dressed in your armor to distract mob AI."),
        THIEF_OF_BUFFS("Thief of Buffs", 3,
                        "Hitting a target strips one of their positive potion effects and applies it to you."),
        SPITEFUL_SABOTAGE("Spiteful Sabotage", 3,
                        "Hitting an enemy rapidly degrades their armor durability (2x normal rate)."),
        GREEN_EYED_GLARE("Green-Eyed Glare", 1,
                        "Looking directly at a player sends you an action-bar message with their exact HP and armor durability."),
        PARASITIC_LINK("Parasitic Link", 3,
                        "You take 50% less damage; the deflected 50% is transferred to the nearest entity to you."),
        FALSE_IDOL("False Idol", 3,
                        "Blocking an attack teleports you 3 blocks backward, leaving a dummy entity that takes the hit."),
        DOPPELGANGER("Doppelganger", 3, "10% chance your attack is duplicated a split-second later for free."),
        COVETOUS_PULL("Covetous Pull", 1,
                        "Hooking a player pulls one random piece of their armor into your inventory (high cooldown)."),

        // 🍖 Gluttony
        DEVOURERS_MAW("Devourer's Maw", 3, "Killing a mob instantly restores 3 hunger points and applies Saturation."),
        CANNIBALIZE("Cannibalize", 3, "Shift-right-clicking sacrifices 100 armor durability to heal you for 4 hearts."),
        FEAST_OF_SOULS("Feast of Souls", 3,
                        "Each kill adds a temporary extra heart container (up to +10). Resets on death."),
        ACIDIC_BITE("Acidic Bite", 3, "Applies a corrosive DoT that ignores armor and drops their max HP temporarily."),
        BOTTOMLESS_PIT("Bottomless Pit", 3,
                        "Blocking projectiles consumes them, turning the arrow damage into hunger saturation."),
        BLACK_HOLE("Black Hole", 1,
                        "Arrows create a gravity well (using velocity vectors) that sucks entities into the impact point."),
        OMNIVORE("Omnivore", 3, "Mining hard blocks (obsidian, deepslate) heals you for 0.5 HP per block."),
        GLUTTONOUS_SWARM("Gluttonous Swarm", 3,
                        "Taking damage spawns friendly silverfish that attack the entity that hit you."),
        GORGING_DEFENSE("Gorging Defense", 3,
                        "The fuller your hunger bar, the higher your armor toughness multiplier."),
        LEECHING_PLAGUE("Leeching Plague", 3,
                        "AoE lifesteal; arrow impact drains 1 HP from all mobs in 5 blocks and gives it to you."),

        // 🦥 Sloth
        LETHARGY("Lethargy", 3,
                        "Applies custom attack-cooldown fatigue, making the target swing their weapon 50% slower."),
        HIBERNATION("Hibernation", 1,
                        "Sneaking for 3s encases you in a barrier block cage (temporary) that blocks all damage."),
        YAWNING_CHASM("Yawning Chasm", 3,
                        "Arrows create an AoE field where all entities and projectiles move at 10% velocity."),
        APATHY("Apathy", 3, "Blocking completely nullifies knockback and applies Blindness to the attacker for 2s."),
        HEAVY_BURDEN("Heavy Burden", 3,
                        "Target's downward Y-velocity is doubled, preventing jumping or critical hits."),
        SLUGGARDS_PACE("Sluggard's Pace", 3,
                        "Leaves a trail of frost that applies Slowness IV to anyone who steps on it."),
        SOMNOLENCE("Somnolence", 3,
                        "Critical hits have a chance to \"stun\" the target (canceling PlayerMoveEvent and InteractEvent)."),
        INERTIA("Inertia", 3, "The longer you stand perfectly still, your damage mitigation scales up to 90%."),
        PROCRASTINATION("Procrastination", 3,
                        "Incoming burst damage is delayed and dealt as a slow DoT over 10 seconds instead."),
        TIME_DILATION("Time Dilation", 3,
                        "Perfect parries (blocking within 0.2s of impact) freeze the attacker in place for 2 seconds."),

        // 🥀 Lust
        SIRENS_SONG("Siren's Song", 1,
                        "Arrows rewrite mob AI (setTarget()), forcing them to attack the nearest hostile mob instead of you."),
        BLOODLUST("Bloodlust", 3, "Every consecutive hit increases your movement and attack speed by 5% (cap at 50%)."),
        SUCCUBUS_KISS("Succubus Kiss", 3,
                        "High lifesteal that specifically targets and drains the enemy's XP bar to heal you."),
        FATAL_ATTRACTION("Fatal Attraction", 3,
                        "Swings create a vacuum effect, pulling entities within 4 blocks directly into your blade."),
        ILLUSIONISTS_VEIL("Illusionist's Veil", 3,
                        "Taking damage spawns 3 fake players (NPCs via packets) that run in random directions."),
        MASQUERADE("Masquerade", 3, "Taking lethal damage makes you invisible and teleports you 5 blocks randomly."),
        BETRAYAL("Betrayal", 3, "Piercing arrows cause all hit mobs to instantly turn and aggro each other."),
        CAPTIVATION("Captivation", 3, "Mobs looking directly at your shield have their attack damage reduced by 40%."),
        LOVERS_SACRIFICE("Lover's Sacrifice", 3,
                        "Automatically intercepts and takes lethal damage meant for your tamed wolves/cats/parrots."),
        INTOXICATING_STRIKE("Intoxicating Strike", 3,
                        "Hits randomly scramble the target's camera pitch and yaw (requires custom packet manipulation).");

        private final String displayName;
        private final int maxLevel;
        private final NamespacedKey key;

        private final String description;

        CustomEnchant(String displayName, int maxLevel, String description) {
                this.description = description;
                this.displayName = displayName;
                this.maxLevel = maxLevel;
                this.key = new NamespacedKey("sevensins", this.name().toLowerCase());
        }

        public String getDescription() {
                return description;
        }

        public String getDisplayName() {
                return displayName;
        }

        public int getMaxLevel() {
                return maxLevel;
        }

        public NamespacedKey getKey() {
                return key;
        }

        /**
         * Applies this custom enchantment to an ItemStack via PDC.
         */
        public void apply(ItemStack item, int level) {
                if (item == null || item.getItemMeta() == null)
                        return;
                ItemMeta meta = item.getItemMeta();
                int finalLevel = Math.min(level, maxLevel);

                meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, finalLevel);

                List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
                if (lore != null) {
                        PlainTextComponentSerializer plain = PlainTextComponentSerializer.plainText();
                        // Remove previous instances of this enchantment and its lore
                        lore.removeIf(c -> plain.serialize(c).contains(displayName)
                                        || plain.serialize(c).startsWith("  §7"));

                        String roman = toRoman(finalLevel);
                        String levelStr = (maxLevel == 1 && finalLevel == 1) ? "" : " " + roman;

                        List<Component> toAdd = new ArrayList<>();

                        String parsedDisplayName = PlaceholderAPI.setPlaceholders(null, displayName + levelStr);
                        String parsedDescription = PlaceholderAPI.setPlaceholders(null, description);

                        // Name Header
                        toAdd.add(Component.text(parsedDisplayName)
                                        .color(NamedTextColor.GRAY)
                                        .decoration(TextDecoration.ITALIC, false));

                        // Word wrapper for description (max 40 chars per line roughly)
                        String[] words = parsedDescription.split(" ");
                        StringBuilder line = new StringBuilder("  §7"); // indented gray text
                        for (String word : words) {
                                if (line.length() + word.length() > 40) {
                                        toAdd.add(Component.text(line.toString()).decoration(TextDecoration.ITALIC,
                                                        false));
                                        line = new StringBuilder("  §7" + word + " ");
                                } else {
                                        line.append(word).append(" ");
                                }
                        }
                        if (line.length() > 4) { // more than just the prefix
                                toAdd.add(Component.text(line.toString()).decoration(TextDecoration.ITALIC, false));
                        }

                        // Insert custom enchantments at the top of the lore
                        lore.addAll(0, toAdd);
                        meta.lore(lore);
                }
                item.setItemMeta(meta);
        }

        /**
         * Gets the level of this enchantment on the item. Returns 0 if not present.
         */
        public int getLevel(ItemStack item) {
                if (item == null || !item.hasItemMeta())
                        return 0;
                return item.getItemMeta().getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, 0);
        }

        private String toRoman(int level) {
                return switch (level) {
                        case 1 -> "I";
                        case 2 -> "II";
                        case 3 -> "III";
                        case 4 -> "IV";
                        case 5 -> "V";
                        default -> String.valueOf(level);
                };
        }

        public static ItemStack createBook(CustomEnchant enchant, int level) {
                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                ItemMeta meta = book.getItemMeta();
                if (meta != null) {
                        meta.setDisplayName("§6Lost Tome: §e" + enchant.getDisplayName());
                        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        book.setItemMeta(meta);
                }
                enchant.apply(book, level);
                return book;
        }
}
