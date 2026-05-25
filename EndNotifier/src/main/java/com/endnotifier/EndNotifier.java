package com.endnotifier;

import org.bukkit.plugin.java.JavaPlugin;

public class EndNotifier extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String advancement = getConfig().getString("advancement", "minecraft:end/root");
        getLogger().info("EndNotifier enabled — watching for advancement: " + advancement);

        getServer().getPluginManager().registerEvents(
                new AdvancementListener(this), this
        );
    }

    @Override
    public void onDisable() {
        getLogger().info("EndNotifier disabled.");
    }
}
