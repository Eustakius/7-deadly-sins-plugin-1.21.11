import re

java_file = 'd:/plugins/7DeadlySins/src/main/java/com/seven/deadlysins/registry/CustomEnchant.java'
with open(java_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Remove the description field and getter
content = content.replace(
    'private final String description;\n\n    CustomEnchant(String displayName, int maxLevel, String description) {\n        this.description = description;',
    'CustomEnchant(String displayName, int maxLevel) {'
).replace(
    'public String getDescription() {\n        return description;\n    }\n\n    public String getDisplayName() {',
    'public String getDisplayName() {'
)

# Strip out the third parameter string from the enum declarations
# Pattern looks for ENUM_NAME("Display Name", level, "description"). Note that description might contain escaped quotes \"
# Better regex: matching until the last parenthesis before comma or semicolon.
# Actually, since descriptions don't contain ) followed by , we can be greedy up to the close parenthesis.
# Let's match: ([A-Z_]+)\("([^"]+)",\s*(\d+),\s*".*?"\)
pattern = r'([A-Z_]+)\("([^"]+)",\s*(\d+),\s*".*?"\)'
content = re.sub(pattern, r'\1("\2", \3)', content)

# Restore the apply() method's lore logic to original
old_lore_code = """        List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
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

new_lore_code = """        List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
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

content = content.replace(old_lore_code, new_lore_code)

with open(java_file, 'w', encoding='utf-8') as f:
    f.write(content)
print("Reverted CustomEnchant.java successfully!")
