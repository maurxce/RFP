package me.lewd.damagesound;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        boolean targetIsPlayer = e.getEntity() instanceof Player;
        boolean attackerIsPlayer = e.getDamager() instanceof Player;
        if (targetIsPlayer || !attackerIsPlayer) return;

        Player attacker = (Player) e.getDamager();
        Material weaponUsed = attacker.getInventory().getItemInMainHand().getType();

        if (weaponUsed.equals(Material.IRON_SHOVEL)) {
            attacker.playSound(attacker.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
        }
    }
}

// https://www.reddit.com/r/admincraft/comments/y2bkka/comment/is39k9f/?utm_source=share&utm_medium=web2x&context=3