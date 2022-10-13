package me.lewd.rightclickexplosion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public final class Main extends JavaPlugin implements Listener {

    private boolean active = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onRightclick(PlayerInteractEvent e) {
        if (active) return;

        active = true;
        Player player = e.getPlayer();
        World world = player.getWorld();
        Location location = player.getLocation();

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if(itemInMainHand.getType().equals(Material.AIR)) return;

        world.createExplosion(location, 1, true, true);
        itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
        player.damage(ThreadLocalRandom.current().nextDouble(1, 3 + 1));

        getServer().getScheduler().runTaskLater(this, () -> {
            active = false;
        }, 20L*2);
    }
}

// https://www.reddit.com/r/admincraft/comments/y2bkka/comment/is4w1tl/?utm_source=share&utm_medium=web2x&context=3