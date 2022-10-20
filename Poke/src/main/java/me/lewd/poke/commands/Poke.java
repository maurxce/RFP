package me.lewd.poke.commands;

import me.lewd.poke.Main;
import me.lewd.poke.utils.ChatUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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

    private ChatUtils chatUtils = new ChatUtils();
    private FileConfiguration config = Main.instance.getConfig();
    private FileConfiguration conf = Main.instance.getDevConf();
    private NitriteCollection pokesCollection = Main.instance.getDatabase().getPokesCollection();
    private NitriteCollection pokeAmountCollection = Main.instance.getDatabase().getUserPokeAmountCollection();

    @Override
    public boolean onCommand(CommandSender cmdSender, Command command, String label, String[] args) {
        if (!(cmdSender instanceof Player)) return true;
        if (args.length == 0) return true;

        String senderName = cmdSender.getName();
        Player sender = (Player) cmdSender;

        if (!sender.hasPermission("poke.poke")) return true;

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        Audience audience = (Audience) target;

        if (target == sender) return true;

        if (target != null && target.isOnline()) {
           Component message = chatUtils.deserialize(formatPokeMessage(senderName));

           Component response = Component.text()
                   .append(chatUtils.getPrefixComponent())
                   .append(message).build();

           audience.sendMessage(response);
            target.playSound(target.getLocation(), getSound(), 1f, 1f);
            return true;
        }

        updateCollection(senderName, targetName);
        return true;
    }

    private String formatPokeMessage(String senderName) {
        String primary = conf.getString("colors.primary");
        String secondary = conf.getString("colors.secondary");

        String message = config.getString("messages.onPoke")
                .replace("{player}", String.format("<%s>%s</%s>", primary, senderName, primary));

        return String.format("<%s>%s", secondary, message);
    }

    private Sound getSound() {
        String sound = config.getString("pokeSound");

        if (sound.isEmpty()) sound = "ENTITY_EXPERIENCE_ORB_PICKUP";
        return Sound.valueOf(sound);
    }

    private void updateCollection(String sender, String target) {
        Filter filter = Filters.and(
                Filters.eq("sender", sender),
                Filters.eq("target", target)
        );
        Document doc = Document.createDocument("sender", sender)
                .put("target", target);
        UpdateOptions options = UpdateOptions.updateOptions(true);

        pokesCollection.update(filter, doc, options);
    }
}
