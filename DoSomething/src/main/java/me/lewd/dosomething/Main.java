package me.lewd.dosomething;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public final class Main extends JavaPlugin implements Listener {

   private FileConfiguration config = new YamlConfiguration();
    private boolean active = false;
    private ArrayList<String> onlinePlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        File cfg = new File(getDataFolder(), "config.yml");
        if (!cfg.exists()) {
            cfg.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        try {
            config.load(cfg);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) throws InterruptedException {
        String prefix = config.getString("prefix") + " ";
        String trigger = config.getString("trigger");
        String word = config.getString("word");
        int seconds = config.getInt("seconds");
        String message = config.getString("message")
                .replace("{word}", word)
                .replace("{seconds}", String.valueOf(seconds));

        if (!active && e.getMessage().equalsIgnoreCase(trigger)) {

            Bukkit.getScheduler().runTask(this, () -> {
                Bukkit.broadcastMessage(
                        ChatColor.translateAlternateColorCodes('&', prefix + message)
                );

                getServer().getOnlinePlayers().forEach(p -> onlinePlayers.add(p.getName()));
                active = true;

                getServer().getScheduler().runTaskLater(this, () -> {
                    onlinePlayers.forEach(p -> getServer().getPlayer(p).setHealth(0.0));

                    onlinePlayers.clear();
                    active = false;
                }, 20L*seconds);
            });
        }

        if (active && e.getMessage().equalsIgnoreCase("ok")) {
            onlinePlayers.remove(e.getPlayer().getName());
        }
    }
}

// https://www.reddit.com/r/admincraft/comments/y2bkka/comment/is5wz5h/?utm_source=share&utm_medium=web2x&context=3