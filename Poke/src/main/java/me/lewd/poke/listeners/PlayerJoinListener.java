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

    ChatUtils chatUtils = new ChatUtils();
    FileConfiguration conf = Main.instance.getDevConf();
    private NitriteCollection coll = Main.instance.getDatabase().getCollection("pokes");

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Audience audience = (Audience) player;

        Filter filter = Filters.eq("target", player.getName());
        int pokeAmount = coll.find(filter).size();
        if (pokeAmount <= 0 ) return;

        String primary = conf.getString("colors.primary");
        String secondary = conf.getString("colors.secondary");

        Component message = chatUtils.deserialize(
                chatUtils.getPrefixUnserialized()
                        + String.format("<%s>You have been poked <%s>", secondary, primary)
                        + pokeAmount + String.format(" <%s>time(s) while you were gone", secondary)
        );
        Component response = Component.text()
                .append(message)
                .clickEvent(ClickEvent.runCommand("/pokes"))
                .build();

        audience.sendMessage(response);
    }
}
