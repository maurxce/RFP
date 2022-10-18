package me.lewd.poke.commands;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.lewd.poke.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Filter;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.filters.Filters;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.UUID;

public class List implements CommandExecutor {

    FileConfiguration conf = Main.instance.getDevConf();
    private NitriteCollection coll = Main.instance.getDatabase().getCollection("pokes");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        String secondary = conf.getString("colors.secondary");

        MiniMessage mm = MiniMessage.miniMessage();
        Component message = mm.deserialize(
                String.format("<%s><bold>Your Pokes", secondary)
        );

        Gui gui = Gui.gui()
                .title(message)
                .rows(6)
                .create();

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        // pokes
        Filter filter = Filters.eq("target", player.getName());
        Cursor cursor = coll.find(filter);

        for (Document doc : cursor) {
            Object pokeSender = doc.get("sender");
            gui.addItem(senderHead(Bukkit.getPlayer(pokeSender.toString())));
        }

        // bottom row
        GuiItem placeholder = ItemBuilder.from(Material.MAGENTA_STAINED_GLASS_PANE).asGuiItem();

        gui.setItem(45, targetHead(player));
        for (int i = 46; i <= 52; i++) {
            gui.setItem(i, placeholder);
        }
        gui.setItem(53, getCheckmarkSkull());

        gui.addSlotAction(53, event -> {
            coll.remove(filter);
            gui.close(player);
        });

        gui.open(player);
        return true;
    }

    private GuiItem senderHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.setOwningPlayer(player);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + player.getName());
        meta.setLore(
                Collections.singletonList(ChatColor.GRAY + player.getName() + " poked you!")
        );

        item.setItemMeta(meta);
        return ItemBuilder.from(item).asGuiItem();
    }

    private GuiItem targetHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.setOwningPlayer(player);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + player.getName());

        item.setItemMeta(meta);
        return ItemBuilder.from(item).asGuiItem();
    }

    private GuiItem getCheckmarkSkull() {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        URL url;
        try {
            url = new URL("https://textures.minecraft.net/texture/a79a5c95ee17abfef45c8dc224189964944d560f19a44f19f8a46aef3fee4756");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        textures.setSkin(url);

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        meta.setOwnerProfile(profile);
        meta.setDisplayName(ChatColor.GREEN + "Mark as Read");
        meta.setLore(
                Collections.singletonList(ChatColor.GRAY + "This clears all your pokes")
        );

        skull.setItemMeta(meta);
        return ItemBuilder.from(skull).asGuiItem();
    }
}
