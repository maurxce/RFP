package me.lewd.poke.commands;

import me.lewd.poke.Main;
import me.lewd.poke.utils.ChatUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.dizitart.no2.Document;
import org.dizitart.no2.Filter;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.UpdateOptions;
import org.dizitart.no2.filters.Filters;

public class Poke implements CommandExecutor {

    ChatUtils chatUtils = new ChatUtils();
    FileConfiguration conf = Main.instance.getDevConf();
    private NitriteCollection coll = Main.instance.getDatabase().getCollection("pokes");

    @Override
    public boolean onCommand(CommandSender cmdSender, Command command, String label, String[] args) {
        if (!(cmdSender instanceof Player)) return true;
        if (args.length == 0) return true;

        String senderName = cmdSender.getName();
        Player sender = (Player) cmdSender;

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        Audience audience = (Audience) target;

        if (target == sender) return true;

        if (target != null && target.isOnline()) {
            String primary = conf.getString("colors.primary");
            String secondary = conf.getString("colors.secondary");

            Component message = chatUtils.deserialize(
                    chatUtils.getPrefixUnserialized()
                            + String.format("<%s>%s <%s>poked you!", primary, senderName, secondary)
            );
            audience.sendMessage(message);
            return true;
        }

        Filter filter = Filters.and(
                Filters.eq("sender", senderName),
                Filters.eq("target", targetName)
        );
        Document doc = Document.createDocument("sender", senderName)
                .put("target", targetName);
        UpdateOptions options = UpdateOptions.updateOptions(true);

        coll.update(filter, doc, options);
        return true;
    }
}
