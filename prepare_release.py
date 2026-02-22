import shutil
import os

# Paths
root_dir = "d:/plugins/7DeadlySins"
release_dir = os.path.join(root_dir, "release")
jar_path = os.path.join(root_dir, "build/libs/SevenDeadlySins-1.0-SNAPSHOT.jar")
readme_out_root = os.path.join(root_dir, "README.md")
readme_out_release = os.path.join(release_dir, "README.md")
enchants_list_path = "C:/Users/A S U S/.gemini/antigravity/brain/2ebe8bab-63f1-4eb3-bd35-68a7393f0c06/enchantments_list.md"

# 1. Create release folder
if not os.path.exists(release_dir):
    os.makedirs(release_dir)

# 2. Copy JAR to release folder
if os.path.exists(jar_path):
    shutil.copy(jar_path, os.path.join(release_dir, "SevenDeadlySins.jar"))

# 3. Create the README content
readme_header = """# 7 Deadly Sins - Custom Enchantments Plugin 🍎🐍💰

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

"""

# Read the enchantments list generated previously
enchants_content = ""
if os.path.exists(enchants_list_path):
    with open(enchants_list_path, 'r', encoding='utf-8') as f:
        # Skip the first title line since we write our own
        lines = f.readlines()
        enchants_content = "".join(lines[2:]) # Skip `# 7 Deadly Sins Custom Enchantments (1.21.11)` and empty line

full_readme = readme_header + enchants_content

# Write to root README
with open(readme_out_root, 'w', encoding='utf-8') as f:
    f.write(full_readme)

# Write to release README
with open(readme_out_release, 'w', encoding='utf-8') as f:
    f.write(full_readme)

print("Release folder prepared and README.md generated successfully!")
