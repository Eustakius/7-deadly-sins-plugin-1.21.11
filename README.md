# 7 Deadly Sins - Custom Enchantments Plugin 🍎🐍💰

Welcome to the **7 Deadly Sins** plugin! This plugin introduces **70 unique and thematic custom enchantments** built natively for Paper 1.21.1, structured around the iconic Seven Deadly Sins: **Wrath, Pride, Greed, Envy, Gluttony, Sloth, and Lust**.

## ✨ What's New: The Subtle Artistry Overhaul
Our latest version introduces the **Refined Visual Engine**, redesigning every single enchantment to be subtle, clean, and grounded.
- **Optimized Particles:** Strictly 50-200 particles per effect for maximum performance and clarity.
- **Thematic Grounding:** Heavy use of `Particle.BLOCK` and `Particle.ITEM` to ground effects in the environment.
- **Combat Clarity:** Clean animations that won't overwhelm your HUD during intense fights.

---

## 🚀 Installation Guide

Installing the plugin is incredibly simple as it is completely standalone!
1. Download `SevenDeadlySins.jar` from the `release` folder.
2. Ensure you have the following **Core Dependencies** installed on your server:
   - [**PlaceholderAPI**](https://www.spigotmc.org/resources/placeholderapi.6245/) (Required for dynamic text)
   - [**PacketEvents**](https://github.com/retrooper/packetevents) (Required for advanced visual effects & camera manipulation)
3. Drag and drop the `.jar` file into your Minecraft server's `plugins` folder.
4. Restart or reload the server.
5. Finished! 

## ⚙️ Commands & Usage
Admin commands to easily distribute and test the custom enchantments:
- `/7sins give <player> <ENCHANTMENT_NAME> <LEVEL>` - Applies the custom enchantment directly to the item the player is currently holding.
- `/7sins book <player> <ENCHANTMENT_NAME> <LEVEL>` - Gives the player a "Lost Tome", a custom enchanted book containing the enchantment.

*Both commands fully support Tab-Completion!*

### Combining & Finding in Survival
- **Exploration:** "Lost Tomes" have a rare chance to spawn naturally as loot inside dangerous structure chests across the world (Ancient Cities, End Cities, Strongholds, etc).
- **Anvils:** You can seamlessly combine "Lost Tomes" with your armor or weapons using a standard Vanilla Anvil. It costs regular Minecraft EXP points correctly!

---

## ![Wrath](https://img.shields.io/badge/WRATH-%23990000?style=for-the-badge&logo=target&logoColor=white) (Aggression, Vengeance, War)
Theme: Punishing enemies, explosive damage, and berserker mechanics.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 1 | Blood Eagle | Axe | Executes <15% HP. Bypasses totems. | Subtle Maroon X-Slash + Fine Mist. |
| 2 | Berserker's Rage | Sword | Damage scales as HP drops. | Tight Crimson Claws + Muted Flames. |
| 3 | Siege Breaker | Pickaxe/Axe | Instantly breaks armor/shields. | Anvil Shatter + Small Crit Pulse. |
| 4 | Hellfire Trebuchet | Bow | Creates persistent fire zones. | Thin Flame Helix + Lava Embers. |
| 5 | Warlord's Cry | Helmet | Buff allies on kill. | Single Gust Ring + Cloud Dust. |
| 6 | Duelist's Spite | Sword | Consecutive hits multiply damage. | Thin Beam + Fine Red Mist. |
| 7 | Impaler | Crossbow | Pins target to the ground. | Triple Dripstone Spires + Ominous Glow. |
| 8 | Scorched Earth | Boots | Sprinting leaves fire trails. | Faint Lava Pulse + Ground Embers. |
| 9 | Vengeance Strike | Chestplate | Counter-attack with stored damage. | Central Flash + Red Sweep Slash. |
| 10 | Mutilation | Sword | Applies **Bleed** (Anti-heal 50%). | Fine Maroon Mist + Red Claws. |

## ![Pride](https://img.shields.io/badge/PRIDE-%23FFD700?style=for-the-badge&logo=crown&logoColor=black) (Ego, Dueling, Royalty)
Theme: Isolation, single-combat dominance, and punishing those who dare attack you.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 11 | King's Resurgence | Chestplate | Fatal blow leaves user at 1 HP. | Dual Wax-Off Helix + End Rod Motes. |
| 12 | Regal Presence | Leggings | Pushes non-boss mobs away. | Gilded Wax-Off Ring + Faint Gust. |
| 13 | Solitary Monarch | Sword | Buffs when alone. | Short Yellow Beam + Golden Shimmer. |
| 14 | Champion's Challenge | Bow | Forces mob to target you alone. | Rising Wax-Off Motes + End Rod Beams. |
| 15 | Crown of Thorns | Helmet | Thorns deal % max HP true damage. | Small Golden Ring + Yellow Dust. |
| 16 | Arrogant Parry | Shield | Chance to auto-parry melee attacks. | Single Flash + Gust Ring. |
| 17 | Sovereign's Reach | Spear/Sword | Increases hit-registration range. | Clean End Rod Beam + Sweep Streak. |
| 18 | Gilded Execution | Sword | Kills drop gold nuggets. | Subtle Gold Shards + Wax-Off Spark. |
| 19 | Unbowed | Boots | Knockback/Slowness immunity. | Bedrock Shards + Faint Cloud Aura. |
| 20 | Royal Decree | Crossbow | Calls down cosmetic shock lightning. | Aqua Dust Beam + Electric Sparks. |

## ![Greed](https://img.shields.io/badge/GREED-%2300FF00?style=for-the-badge&logo=money&logoColor=black) (Loot, Thievery, Economy)
Theme: Stealing, hoarding, and economy-based scaling.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 21 | Highwayman's Toll | Sword | Drains XP from targets. | Subtle Lime Siphon Arcs. |
| 22 | Midas Touch | Pickaxe | Ores auto-smelt & yield scales. | Gold Block Fragments + Wax-Off Glow. |
| 23 | Plunderer's Strike | Axe | Chance to disarm enemy. | Emerald Item Shards + Happy Villager. |
| 24 | Hoarder's Vitality | Chestplate | HP scales with gold/emeralds. | Steady Heart Pulse + Wax-Off Ring. |
| 25 | Mercenary's Fortune | Sword | Bonus damage to high-tier armor. | Muted Green Dust + Villager Motes. |
| 26 | Dragon's Hoard | Leggings | Saves from death using ores. | Thin Wax-Off Ring + Barrier Fragments. |
| 27 | Extortion | Bow | Arrows lock target inventory. | Single Gray Dust Helix + Smoke Motes. |
| 28 | Pickpocket | Sword | Sneak hits steal items. | Small Smoke Poof + White Ash. |
| 29 | Treasure Hunter | Boots | Point to nearest loot chest. | Short Villager Beam + Happy Sparks. |
| 30 | Usury | Hoe | Doubled crop yields. | Subtle Green Slash + Happy Villager. |

## ![Envy](https://img.shields.io/badge/ENVY-%23800080?style=for-the-badge&logo=eye&logoColor=white) (Mimicry, Sabotage, Shadows)
Theme: Copying others, stealing buffs, and crippling the strong.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 31 | Mimicry | Sword | Copies enemy enchantments. | Thin Purple Portal Helix. |
| 32 | Usurper's Blade | Axe | Bonus damage to stronger foes. | Muted Sculk Soul Streaks. |
| 33 | Shadow Clone | Bow | Arrows spawn distracting decoys. | Witch Magic Poof + Purple Motes. |
| 34 | Thief of Buffs | Sword | Steals positive potion effects. | Subtle Fuchsia Siphon Swirls. |
| 35 | Spiteful Sabotage | Pickaxe | Rapidly degrades enemy armor. | Green Sneeze Bubbles + Fine Smoke. |
| 36 | Green-Eyed Glare | Helmet | Reveals target HP and armor. | Faint Ominous Eye Glow + White Ash. |
| 37 | Parasitic Link | Chestplate | Deflects 50% damage to nearby mobs. | Thin Teal Beam + Sculk Souls. |
| 38 | False Idol | Shield | Teleport on block leaving dummy. | Glass Shatter + Small Portal Poof. |
| 39 | Doppelganger | Sword | Attack is duplicated shortly after. | Double Purple/Black Slashes. |
| 40 | Covetous Pull | Fishing Rod | Steals armor pieces with hook. | Small Inward Portal Bits. |

## ![Gluttony](https://img.shields.io/badge/GLUTTONY-%23111111?style=for-the-badge&logo=apple&logoColor=white) (Consumption, Devouring, Health)
Theme: Eating blocks/items, vampirism, and scaling with hunger.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 41 | Devourer's Maw | Sword | Restore hunger on kill. | Minor Inward Void + Crit Sparks. |
| 42 | Cannibalize | Chestplate | Sacrifice durability for health. | Small Redstone Block Burst. |
| 43 | Feast of Souls | Axe | Gains max HP on kill. | Rising Sculk Soul Spirits. |
| 44 | Acidic Bite | Sword | Corrosive DoT that ignores armor. | Muted Lime Bubbles + Sneeze. |
| 45 | Bottomless Pit | Shield | Consumes projectiles for hunger. | Single Ink Swirl + Smoke Motes. |
| 46 | Black Hole | Bow | Arrows create a massive gravity well. | Sonic Boom + Concentrated Ink Void. |
| 47 | Omnivore | Pickaxe | Mining hard blocks heals you. | Block Break Bits + Faint Cloud. |
| 48 | Gluttonous Swarm | Leggings | Spawns silverfish on damage. | Small Swarming Witch Cloud. |
| 49 | Gorging Defense | Helmet | Hunger level buffs toughness. | Thin White Cloud Ring. |
| 50 | Leeching Plague | Bow | AoE lifesteal on hit. | Fine Maroon Tendrils + Soul Motes. |

## ![Sloth](https://img.shields.io/badge/SLOTH-%2300FFFF?style=for-the-badge&logo=clock&logoColor=black) (Time Manipulation, Lethargy, Heavy Armor)
Theme: Slowing the game down, gravity manipulation, and immovable defense.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 51 | Lethargy | Sword | Slows enemy attack speed. | Muted Smoke Fog + White Ash Drifts. |
| 52 | Hibernation | Chestplate | Encase yourself in barrier cage. | Snow Block Shards + Flake Ring. |
| 53 | Yawning Chasm | Bow | Field that slows all movement. | Slow Gust Streaks + Faint Cloud. |
| 54 | Apathy | Shield | Nullifies KB and blinds attacker. | Heavy Ash Pulse + Ink Motes. |
| 55 | Heavy Burden | Axe | Prevents jumping/critical hits. | Anvil Drop + Downward Crit Beam. |
| 56 | Sluggard's Pace | Boots | Leaves a **Frost** trail. | Snowflake Drifts + Faint Blue Trail. |
| 57 | Somnolence | Sword | Chance to "stun" target. | Enchant Motes + Bubble Bits. |
| 58 | Inertia | Leggings | Mitigation buffs when standing still. | End Rod Shimmer + Thin Wax Ring. |
| 59 | Procrastination | Helmet | Incoming damage is delayed. | Single Gray Helix + Smoke Motes. |
| 60 | Time Dilation | Shield | Freeze attacker on perfect parry. | Clock-Ticking Trial Detection + Flash. |

## ![Lust](https://img.shields.io/badge/LUST-%23FF66B2?style=for-the-badge&logo=heart&logoColor=white) (Charm, Bloodlust, Illusion)
Theme: Mind control, attraction, manipulation, and aggro misdirection.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 61 | Siren's Song | Bow | Forces mobs to attack each other. | Swirling Cherry Petals + Soft Hearts. |
| 62 | Bloodlust | Sword | Stacks speed on hits. | Small Magenta Slashes + Flame Motes. |
| 63 | Succubus Kiss | Axe | Lifesteal that drains enemy XP. | Subtle Pink Siphon + Hearts. |
| 64 | Fatal Attraction | Sword | Swings create a vacuum effect. | Small Inward Hearts + Minor Poof. |
| 65 | Illusionist's Veil | Chestplate | Spawns 3 fake players on damage. | Single Flash + Cloud Poof. |
| 66 | Masquerade | Helmet | Invisibility + teleport on lethal. | Subtle Cherry Bloom + Witch Poof. |
| 67 | Betrayal | Crossbow | Piercing arrows cause mob infighting. | Angry Villager Motes + Hearts. |
| 68 | Captivation | Shield | Reduces damage from viewers. | Faint Magenta Mist + Hearts. |
| 69 | Lover's Sacrifice | Leggings | Protects pets from lethal damage. | Thin Red/Pink Shared Helix. |
| 70 | Intoxicating Strike | Sword | Hits randomly scramble target camera. | Magenta Sensory Aura + Fuchsia Dust. |

## 📞 Contact
If you have any questions, issues, or want to discuss this plugin further, feel free to contact me directly on Discord:
**`jjiu_.`**
