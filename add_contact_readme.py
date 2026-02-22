import os

paths = ["d:/plugins/7DeadlySins/README.md", "d:/plugins/7DeadlySins/release/README.md"]
addition = "\n\n## 📞 Contact\nIf you have any questions, issues, or want to discuss this plugin further, feel free to contact me directly on Discord:\n**`jjiu_.`**\n"

for p in paths:
    if os.path.exists(p):
        with open(p, "a", encoding="utf-8") as f:
            f.write(addition)

print("Contact info added successfully.")
