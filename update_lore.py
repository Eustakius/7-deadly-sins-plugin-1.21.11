import re

# Read the enchantments_list.md to build a dictionary of lore
lore_dict = {}
with open('C:/Users/A S U S/.gemini/antigravity/brain/2ebe8bab-63f1-4eb3-bd35-68a7393f0c06/enchantments_list.md', 'r', encoding='utf-8') as f:
    for line in f:
        if line.startswith('|') and not line.startswith('| #'):
            parts = [p.strip() for p in line.split('|')]
            if len(parts) >= 5:
                # parts[0] is empty, [1] is ID, [2] is Name, [3] is Item, [4] is advanced mechanic
                name = parts[2]
                mechanic = parts[4]
                lore_dict[name] = mechanic

# Read CustomEnchant.java and add descriptions to the enum declarations
java_file = 'd:/plugins/7DeadlySins/src/main/java/com/seven/deadlysins/registry/CustomEnchant.java'
with open(java_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Make the enum take a third string parameter for the description
content = content.replace(
    'CustomEnchant(String displayName, int maxLevel) {',
    'private final String description;\n\n    CustomEnchant(String displayName, int maxLevel, String description) {\n        this.description = description;'
).replace(
    'public String getDisplayName() {',
    'public String getDescription() {\n        return description;\n    }\n\n    public String getDisplayName() {'
)

# Replace each enum constant with the new format containing the description
for name, description in lore_dict.items():
    # Need to match the enum declaration like BERSERKERS_RAGE("Berserker's Rage", 3),
    # And safely inject the description
    # Double quotes inside the description need to be escaped
    safe_desc = description.replace('"', '\\"')
    
    # We use a regex to find the exact line
    # Escape special characters in name like '
    safe_name = re.escape(name)
    pattern = r'([A-Z_]+)\("' + safe_name + r'",\s*(\d+)\)'
    
    def replacer(match):
        enum_name = match.group(1)
        level = match.group(2)
        return f'{enum_name}("{name}", {level}, "{safe_desc}")'
    
    content = re.sub(pattern, replacer, content)

# Now, we need to modify the apply() method to add the description to the item's lore
# First, remove the old plain text component code and replace it with word-wrapped lore
old_lore_code = """        List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
        if (lore != null) {
            PlainTextComponentSerializer plain = PlainTextComponentSerializer.plainText();
            lore.removeIf(c -> plain.serialize(c).contains(displayName));

            String roman = toRoman(finalLevel);
            String levelStr = (maxLevel == 1 && finalLevel == 1) ? "" : " " + roman;

            Component enchLore = Component.text(displayName + levelStr)
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false);

            // Insert custom enchantments at the top of the lore
            lore.add(0, enchLore);
            meta.lore(lore);
        }"""

new_lore_code = """        List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
        if (lore != null) {
            PlainTextComponentSerializer plain = PlainTextComponentSerializer.plainText();
            // Remove previous instances of this enchantment and its lore
            lore.removeIf(c -> plain.serialize(c).contains(displayName) || plain.serialize(c).startsWith("  §7"));

            String roman = toRoman(finalLevel);
            String levelStr = (maxLevel == 1 && finalLevel == 1) ? "" : " " + roman;

            List<Component> toAdd = new ArrayList<>();
            
            // Name Header
            toAdd.add(Component.text(displayName + levelStr)
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false));
                    
            // Word wrapper for description (max 40 chars per line roughly)
            String[] words = description.split(" ");
            StringBuilder line = new StringBuilder("  §7"); // indented gray text
            for (String word : words) {
                if (line.length() + word.length() > 40) {
                    toAdd.add(Component.text(line.toString()).decoration(TextDecoration.ITALIC, false));
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
        }"""

content = content.replace(old_lore_code, new_lore_code)

with open(java_file, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated CustomEnchant.java with detailed lore descriptions!")
