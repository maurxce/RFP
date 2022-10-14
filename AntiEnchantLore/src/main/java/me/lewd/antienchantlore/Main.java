package me.lewd.antienchantlore;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    // enchantment table
    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        ItemStack item = e.getItem();
        List<String> itemLore = item.getItemMeta().getLore();
        List<String> disallowedLore = config.getStringList("disallowedLore");

        if (itemLore == null) return;

        itemLore.forEach(row -> {
            disallowedLore.forEach(disallowed -> {
                if (row.equalsIgnoreCase(disallowed)) e.setCancelled(true);
            });
        });
    }

    // anvil
    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        ItemStack firstItem = e.getInventory().getItem(0);
        ItemStack secondItem = e.getInventory().getItem(1);

        if (firstItem == null) return;
        if (secondItem == null) return;

        List<String> itemLore = firstItem.getItemMeta().getLore();
        List<String> disallowedLore = config.getStringList("disallowedLore");

        if (itemLore == null) return;
        if (!secondItem.getType().equals(Material.ENCHANTED_BOOK)) return;

        itemLore.forEach(row -> {
            disallowedLore.forEach(disallowed -> {
                if (row.equalsIgnoreCase(disallowed)) e.setResult(null);
            });
        });
    }
}
