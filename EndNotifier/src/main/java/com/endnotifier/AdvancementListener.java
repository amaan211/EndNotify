package com.endnotifier;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {

    private final EndNotifier plugin;

    public AdvancementListener(EndNotifier plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        String targetAdvancement = plugin.getConfig().getString("advancement", "minecraft:end/root");
        String key = event.getAdvancement().getKey().toString();

        if (!key.equalsIgnoreCase(targetAdvancement)) {
            return;
        }

        String playerName = event.getPlayer().getName();
        plugin.getLogger().info(playerName + " entered the End! Sending email notification...");

        // Send email asynchronously so we don't block the main thread
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String host = plugin.getConfig().getString("smtp.host", "smtp.gmail.com");
                int port = plugin.getConfig().getInt("smtp.port", 587);
                String username = plugin.getConfig().getString("smtp.username", "");
                String password = plugin.getConfig().getString("smtp.password", "");
                String from = plugin.getConfig().getString("smtp.from", username);
                String to = plugin.getConfig().getString("notify-email", "");

                if (to.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    plugin.getLogger().warning("Email not configured properly — check config.yml");
                    return;
                }

                String subject = "[Minecraft] " + playerName + " entered the End!";
                String body = "Player " + playerName + " has entered the End for the first time!\n\n"
                        + "Time: " + java.time.LocalDateTime.now() + "\n"
                        + "Server: " + plugin.getServer().getName() + "\n";

                EmailSender.send(host, port, username, password, from, to, subject, body);
                plugin.getLogger().info("Email notification sent successfully to " + to);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to send email notification: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
