package me.lewd.ruinedportals;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StructureSearchResult;

import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin implements Listener {

    private FileConfiguration config = new YamlConfiguration();

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
    public void onPortalCreation(PortalCreateEvent e) {
        Player player = (Player) e.getEntity();
        int radius = config.getInt("allowedRadius");
        StructureSearchResult nearestStructure = player.getWorld().locateNearestStructure(player.getLocation(), StructureType.RUINED_PORTAL, radius, false);

        double playerX = player.getLocation().getX();
        double playerZ = player.getLocation().getZ();
        double portalX = nearestStructure.getLocation().getX();
        double portalZ = nearestStructure.getLocation().getZ();

        boolean inRangeX = (playerX - radius <= portalX) && (playerX + radius >= portalX);
        boolean inRangeZ = (playerZ - radius <= portalZ) && (playerZ + radius >= portalZ);
        if (!(inRangeX && inRangeZ)) e.setCancelled(true);
    }
}

// https://www.reddit.com/r/admincraft/comments/y2bkka/comment/is4tvls/?utm_source=share&utm_medium=web2x&context=3