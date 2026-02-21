package com.seven.deadlysins;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;

public final class SevenDeadlySins extends JavaPlugin {

    private static SevenDeadlySins instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("7 Deadly Sins custom enchantments plugin is starting...");
        getServer().getPluginManager().registerEvents(new com.seven.deadlysins.features.WrathListener(this), this);
        getServer().getPluginManager().registerEvents(new com.seven.deadlysins.features.PrideListener(this), this);
        getServer().getPluginManager().registerEvents(new com.seven.deadlysins.features.GreedListener(this), this);
        getServer().getPluginManager().registerEvents(new com.seven.deadlysins.features.EnvyListener(this), this);
        getServer().getPluginManager().registerEvents(new com.seven.deadlysins.features.GluttonyListener(this), this);
        getServer().getPluginManager().registerEvents(new com.seven.deadlysins.features.SlothListener(this), this);
        getServer().getPluginManager().registerEvents(new com.seven.deadlysins.features.LustListener(this), this);
        getServer().getPluginManager().registerEvents(new com.seven.deadlysins.features.LootGenerationListener(this),
                this);
        getServer().getPluginManager().registerEvents(new com.seven.deadlysins.features.AnvilListener(), this);

        com.seven.deadlysins.commands.SinsCommand cmd = new com.seven.deadlysins.commands.SinsCommand();
        PluginCommand pluginCommand = getCommand("7sins");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(cmd);
            pluginCommand.setTabCompleter(cmd);
        } else {
            getLogger().severe("Command '7sins' could not be registered! Is it in plugin.yml?");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("7 Deadly Sins disabled.");
    }

    public static SevenDeadlySins getInstance() {
        return instance;
    }
}
