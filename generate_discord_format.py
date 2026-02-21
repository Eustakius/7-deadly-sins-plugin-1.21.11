import re

input_file = "C:/Users/A S U S/.gemini/antigravity/brain/2ebe8bab-63f1-4eb3-bd35-68a7393f0c06/enchantments_list.md"
output_file = "C:/Users/A S U S/.gemini/antigravity/brain/2ebe8bab-63f1-4eb3-bd35-68a7393f0c06/discord_enchantments.txt"

discord_output = []

with open(input_file, 'r', encoding='utf-8') as f:
    lines = f.readlines()

for line in lines:
    line = line.strip()
    
    if line.startswith("## "):
        header = line.replace("## ", "")
        discord_output.append(f"\n# {header}")
        discord_output.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    elif line.startswith("Theme: "):
        discord_output.append(f"*{line}*\n")
    elif line.startswith("|") and not line.startswith("| #") and not line.startswith("|---"):
        parts = [p.strip() for p in line.split("|")]
        if len(parts) >= 6:
            ench_id = parts[1]
            ench_name = parts[2]
            ench_item = parts[3]
            ench_desc = parts[4]
            ench_particle = parts[5]
            
            discord_output.append(f"**{ench_id}. {ench_name}** `[{ench_item}]`")
            discord_output.append(f"> {ench_desc}")
            discord_output.append(f"> ✧ *{ench_particle}*\n")

with open(output_file, 'w', encoding='utf-8') as f:
    f.write("\n".join(discord_output).strip())

print("Successfully generated discord formatted enchantments list.")
