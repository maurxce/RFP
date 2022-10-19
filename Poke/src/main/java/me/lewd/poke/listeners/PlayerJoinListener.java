package me.lewd.poke.listeners;

import me.lewd.poke.Main;
import me.lewd.poke.utils.ChatUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.dizitart.no2.Filter;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.filters.Filters;

public class PlayerJoinListener implements Listener {

    private ChatUtils chatUtils = new ChatUtils();
    private FileConfiguration config = Main.instance.getConfig();
    private FileConfiguration conf = Main.instance.getDevConf();
    private NitriteCollection pokesCollection = Main.instance.getDatabase().getPokesCollection();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Audience audience = (Audience) player;

        Filter filter = Filters.eq("target", player.getName());
        int pokeAmount = pokesCollection.find(filter).size();
        if (pokeAmount <= 0 ) return;

        Component message = chatUtils.deserialize(formatJoinMessage(pokeAmount));

        Component response = Component.text()
                .append(chatUtils.getPrefixComponent())
                .append(message)
                .clickEvent(ClickEvent.runCommand("/pokes"))
                .build();

        audience.sendMessage(response);
    }

    private String formatJoinMessage(int amount) {
        String primary = conf.getString("colors.primary");
        String secondary = conf.getString("colors.secondary");

        String message = config.getString("messages.onJoin")
                .replace("{amount}", String.format("<%s>%d</%s>", primary, amount, primary));

        return String.format("<%s>%s", secondary, message);
    }
}
