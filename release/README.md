# 7 Deadly Sins - Custom Enchantments Plugin 🍎🐍💰

Welcome to the **7 Deadly Sins** plugin! This plugin introduces **70 unique and incredibly advanced custom enchantments** built perfectly for Paper 1.21.11, structured around the iconic Seven Deadly Sins: Wrath, Pride, Greed, Envy, Gluttony, Sloth, and Lust.

## 🚀 Installation Guide

Installing the plugin is incredibly simple as it is completely standalone!
1. Download `SevenDeadlySins.jar` from the `release` folder.
2. Drag and drop the `.jar` file into your Minecraft server's `plugins` folder.
3. Restart or reload the server.
4. Finished! 

## ⚙️ Commands & Usage
Admin commands to easily distribute and test the custom enchantments:
- `/7sins give <player> <ENCHANTMENT_NAME> <LEVEL>` - Applies the custom enchantment directly to the item the player is currently holding.
- `/7sins book <player> <ENCHANTMENT_NAME> <LEVEL>` - Gives the player a "Lost Tome", a custom enchanted book containing the enchantment.

*Both commands fully support Tab-Completion!*

### Combining & Finding in Survival
- **Exploration:** "Lost Tomes" have a rare chance to spawn naturally as loot inside dangerous structure chests across the world (Ancient Cities, End Cities, Strongholds, etc).
- **Anvils:** You can seamlessly combine "Lost Tomes" with your armor or weapons using a standard Vanilla Anvil. It costs regular Minecraft EXP points correctly!

---

## ⚔️ Wrath (Aggression, Vengeance, War)
Theme: Punishing enemies, explosive damage, and berserker mechanics.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 1 | Blood Eagle | Axe | Executes targets below 15% HP. Bypasses totem of undying. | Crimson ash exploding into wing shapes. |
| 2 | Berserker's Rage | Sword | As your HP drops, damage scales up logarithmically. | Red drip particles falling from the player. |
| 3 | Siege Breaker | Pickaxe/Axe | Deals triple damage to shields and instantly breaks armor durability. | Anvil break sound with iron dust particles. |
| 4 | Hellfire Trebuchet | Bow | Arrows spawn a 3x3 persistent fire zone (using schedulers, not real fire blocks) that burns entities. | Lingering flame and lava pop particles. |
| 5 | Warlord's Cry | Helmet | Killing a mob grants temporary Strength to all allied players within a 15-block radius. | Sonic boom / sweep attack particles. |
| 6 | Duelist's Spite | Sword | Consecutive hits on the same target multiply damage. Resets if you hit a different entity. | Sparks flying on impact. |
| 7 | Impaler | Crossbow | Pins target to the ground (cancels movement vectors) for 2 seconds. | Iron bar/barrier particles wrapping the target. |
| 8 | Scorched Earth | Boots | Sprinting leaves a trail of temporary "hot" blocks that burn pursuers via PlayerMoveEvent. | Campfire smoke and flame particles. |
| 9 | Vengeance Strike | Chestplate | Absorbs the last hit taken and adds 200% of that damage to your next melee swing. | Pulsing angry villager particles. |
| 10 | Mutilation | Sword | Applies a custom bleeding DoT that decreases target's healing received by 50%. | Redstone dust particles trailing the target. |

## 👑 Pride (Ego, Dueling, Royalty)
Theme: Isolation, single-combat dominance, and punishing those who dare attack you.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 11 | King's Resurgence | Chestplate | Fatal blow leaves you at 1 HP with 3s invulnerability. 10-minute internal cooldown via NBT. | Totem particles circling the player. |
| 12 | Regal Presence | Leggings | Passive aura that constantly pushes non-boss mobs 3 blocks away from you. | Cloud particles expanding outward. |
| 13 | Solitary Monarch | Sword | Grants +50% attack speed and damage, but only if no allies are within 20 blocks. | Glowing golden aura (Glow effect). |
| 14 | Champion's Challenge | Bow | Hitting a mob rewrites its AI pathfinding to target ONLY you, ignoring other players. | End rod particles beaming to the target. |
| 15 | Crown of Thorns | Helmet | Attackers take pure true damage (ignores armor) equal to 10% of their own max HP. | Sharp crit particles. |
| 16 | Arrogant Parry | Shield | 15% chance to auto-parry melee attacks, staggering the attacker (Slowness X). | Flash of white ash particles. |
| 17 | Sovereign's Reach | Spear/Sword | Uses raytracing to increase melee attack hit-registration range by 2.5 blocks. | Sweeping edge particles scaled up 2x. |
| 18 | Gilded Execution | Sword | Kills have a chance to drop gold nuggets directly into your inventory. | Gold block break particles. |
| 19 | Unbowed | Boots | Complete immunity to knockback and the Slowness potion effect. | Slime particles on hit. |
| 20 | Royal Decree | Crossbow | Fully charged arrows call down a cosmetic lightning bolt that deals massive AoE shock damage. | Electric spark particles. |

## 💰 Greed (Loot, Thievery, Economy)
Theme: Stealing, hoarding, and economy-based scaling.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 21 | Highwayman's Toll | Sword | Hitting a player drains a small amount of their XP directly into your XP bar. | XP orb particles flowing to you. |
| 22 | Midas Touch | Pickaxe | Ores auto-smelt and drop yields multiplied by your current XP level. | Gold sparkle particles. |
| 23 | Plunderer's Strike | Axe | 5% chance to disarm a mob/player, dropping their held item onto the ground. | Enchantment table letters flying away. |
| 24 | Hoarder's Vitality | Chestplate | Max HP dynamically increases based on the amount of gold/emeralds in your inventory. | Heart particles colored yellow. |
| 25 | Mercenary's Fortune | Sword | Deals bonus damage to targets wearing high-tier armor (Diamond/Netherite). | Emerald block break particles. |
| 26 | Dragon's Hoard | Leggings | Taking fatal damage consumes rare ores from your inventory instead of letting you die. | Dragon breath particles. |
| 27 | Extortion | Bow | Arrows that hit players apply a custom tag that disables their ability to open inventories for 5s. | Barrier particles over the target's head. |
| 28 | Pickpocket | Sword | Sneak-attacking a player has a 1% chance to steal a random item from their hotbar. | Smoke particles. |
| 29 | Treasure Hunter | Boots | Sneaking points a particle beam toward the nearest un-looted chest in the chunk. | Subtle, fast-moving spark particles. |
| 30 | Usury | Hoe | Replanting crops costs durability, but yields double the crop drops upon next harvest. | Bonemeal particles. |

## 🐍 Envy (Mimicry, Sabotage, Shadows)
Theme: Copying others, stealing buffs, and crippling the strong.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 31 | Mimicry | Sword | Temporarily copies the highest level enchantment from the last weapon that hit you. | Portal particles. |
| 32 | Usurper's Blade | Axe | Deals +30% damage to players who have more max health than you. | Shadow/Squid ink particles. |
| 33 | Shadow Clone | Bow | Arrows spawn an invisible Armor Stand dressed in your armor to distract mob AI. | Witch magic particles. |
| 34 | Thief of Buffs | Sword | Hitting a target strips one of their positive potion effects and applies it to you. | Swirling green spell particles. |
| 35 | Spiteful Sabotage | Pickaxe | Hitting an enemy rapidly degrades their armor durability (2x normal rate). | Iron breaking particles. |
| 36 | Green-Eyed Glare | Helmet | Looking directly at a player sends you an action-bar message with their exact HP and armor durability. | Eye-shaped custom particle vectors. |
| 37 | Parasitic Link | Chestplate | You take 50% less damage; the deflected 50% is transferred to the nearest entity to you. | Tether of purple dust particles. |
| 38 | False Idol | Shield | Blocking an attack teleports you 3 blocks backward, leaving a dummy entity that takes the hit. | Poof/Explosion particles. |
| 39 | Doppelganger | Sword | 10% chance your attack is duplicated a split-second later for free. | Ghostly white sweep particles. |
| 40 | Covetous Pull | Fishing Rod | Hooking a player pulls one random piece of their armor into your inventory (high cooldown). | Reverse water splash particles. |

## 🍖 Gluttony (Consumption, Devouring, Health)
Theme: Eating blocks/items, vampirism, and scaling with hunger.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 41 | Devourer's Maw | Sword | Killing a mob instantly restores 3 hunger points and applies Saturation. | Food break particles. |
| 42 | Cannibalize | Chestplate | Shift-right-clicking sacrifices 100 armor durability to heal you for 4 hearts. | Slime particles. |
| 43 | Feast of Souls | Axe | Each kill adds a temporary extra heart container (up to +10). Resets on death. | Soul fire flames. |
| 44 | Acidic Bite | Sword | Applies a corrosive DoT that ignores armor and drops their max HP temporarily. | Poison drip particles. |
| 45 | Bottomless Pit | Shield | Blocking projectiles consumes them, turning the arrow damage into hunger saturation. | Void/Portal particles. |
| 46 | Black Hole | Bow | Arrows create a gravity well (using velocity vectors) that sucks entities into the impact point. | Swirling purple/black dust particles. |
| 47 | Omnivore | Pickaxe | Mining hard blocks (obsidian, deepslate) heals you for 0.5 HP per block. | Block crack particles of the mined block. |
| 48 | Gluttonous Swarm | Leggings | Taking damage spawns friendly silverfish that attack the entity that hit you. | Infested stone particles. |
| 49 | Gorging Defense | Helmet | The fuller your hunger bar, the higher your armor toughness multiplier. | Nausea swirl particles (very subtle). |
| 50 | Leeching Plague | Bow | AoE lifesteal; arrow impact drains 1 HP from all mobs in 5 blocks and gives it to you. | Red dust vectors flying back to you. |

## 🦥 Sloth (Time Manipulation, Lethargy, Heavy Armor)
Theme: Slowing the game down, gravity manipulation, and immovable defense.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 51 | Lethargy | Sword | Applies custom attack-cooldown fatigue, making the target swing their weapon 50% slower. | Cobweb particles. |
| 52 | Hibernation | Chestplate | Sneaking for 3s encases you in a barrier block cage (temporary) that blocks all damage. | Snowflake particles. |
| 53 | Yawning Chasm | Bow | Arrows create an AoE field where all entities and projectiles move at 10% velocity. | Blue dust particles in a sphere. |
| 54 | Apathy | Shield | Blocking completely nullifies knockback and applies Blindness to the attacker for 2s. | Dark gray cloud particles. |
| 55 | Heavy Burden | Axe | Target's downward Y-velocity is doubled, preventing jumping or critical hits. | Falling dust particles. |
| 56 | Sluggard's Pace | Boots | Leaves a trail of frost that applies Slowness IV to anyone who steps on it. | Snow shovel particles. |
| 57 | Somnolence | Sword | Critical hits have a chance to "stun" the target (canceling PlayerMoveEvent and InteractEvent). | Zzz (Sleep) particles. |
| 58 | Inertia | Leggings | The longer you stand perfectly still, your damage mitigation scales up to 90%. | Soul sand bubble particles. |
| 59 | Procrastination | Helmet | Incoming burst damage is delayed and dealt as a slow DoT over 10 seconds instead. | Clock/Tick particles (falling sand). |
| 60 | Time Dilation | Shield | Perfect parries (blocking within 0.2s of impact) freeze the attacker in place for 2 seconds. | Cyan glowing aura. |

## 🥀 Lust (Charm, Bloodlust, Illusion)
Theme: Mind control, attraction, manipulation, and aggro misdirection.

| # | Enchantment Name | Item | Advanced Mechanic | Custom Particle |
|---|---|---|---|---|
| 61 | Siren's Song | Bow | Arrows rewrite mob AI (setTarget()), forcing them to attack the nearest hostile mob instead of you. | Musical note particles. |
| 62 | Bloodlust | Sword | Every consecutive hit increases your movement and attack speed by 5% (cap at 50%). | Cherry blossom/pink falling particles. |
| 63 | Succubus Kiss | Axe | High lifesteal that specifically targets and drains the enemy's XP bar to heal you. | Heart particles. |
| 64 | Fatal Attraction | Sword | Swings create a vacuum effect, pulling entities within 4 blocks directly into your blade. | Magenta dust vectors pulling inward. |
| 65 | Illusionist's Veil | Chestplate | Taking damage spawns 3 fake players (NPCs via packets) that run in random directions. | Campfire smoke particles. |
| 66 | Masquerade | Helmet | Taking lethal damage makes you invisible and teleports you 5 blocks randomly. | Poof particles. |
| 67 | Betrayal | Crossbow | Piercing arrows cause all hit mobs to instantly turn and aggro each other. | Angry villager particles on hit mobs. |
| 68 | Captivation | Shield | Mobs looking directly at your shield have their attack damage reduced by 40%. | Glow squid ink particles. |
| 69 | Lover's Sacrifice | Leggings | Automatically intercepts and takes lethal damage meant for your tamed wolves/cats/parrots. | Damage indicator hearts. |
| 70 | Intoxicating Strike | Sword | Hits randomly scramble the target's camera pitch and yaw (requires custom packet manipulation). | Nausea spirals. |
