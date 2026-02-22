# Changelog v1.0.1

## Bug Fixes & Stability
- **Global Passive Cleanup**: Fixed a critical bug where custom enchantment passives (like bonus Max HP, Attack Speed, or permanent Potion Buffs) would permanently linger on a player if the enchanted item broke, was dropped, or unequipped. 
- **Graceful Degradation**: Built a highly robust asynchronous/synchronous polling system across all 7 Sin Listeners that safely drops modified attributes without risking accidental deletion of standard Vanilla attributes or normal potion effects (like drinking a Swiftness potion).
- **Paper 1.21 API Compliance**: Resolved several deprecation warnings in the `AnvilListener` relating to `AnvilInventory.getRenameText()` and `setRepairCost()`, standardizing them locally via the new `AnvilView` cast wrapper interface.

## Visual Enhancements 
- **Cinematic Particles**: Overhauled the particle rendering system entirely. Completely replaced flat circle or generic particle spawns with new trigonometric "Cinematic" layouts (Spirals, Expanding Rings, Directed Beams, Auras, and Slashes).
- Upgraded the following effects to use Cinematic Particles:
  - **Wrath**: *Berserker's Rage* (Aura), *Siege Breaker* (Expanding Ring), *Duelist's Spite* (Slash)
  - **Envy**: *False Idol* (Implosion), *Mimicry* (Spiral)
  - **Gluttony**: *Black Hole* (Aura), *Leeching Plague* (Beam)
  - **Sloth**: *Hibernation* (Expanding Ring)
  - **Lust**: *Fatal Attraction* (Spiral), *Intoxicating Strike* (Aura)
  - **Pride**: *King's Resurgence* (Totem Aura)
  - **Greed**: *Dragon's Hoard* (Dragon Breath Aura)
