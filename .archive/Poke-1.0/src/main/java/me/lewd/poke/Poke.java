package me.lewd.poke;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Poke implements CommandExecutor {

    MongoCollection<Document> collection = Main.instance.collection;

    @Override
    public boolean onCommand(CommandSender cmdSender, Command command, String label, String[] args) {
        if (!(cmdSender instanceof Player)) return true;
        if (args.length == 0) return true;

        String senderName = cmdSender.getName();
        Player sender = (Player) cmdSender;

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == sender) return true;

        if (target != null && target.isOnline()) {
            target.sendMessage(senderName + " poked you!");
            return true;
        }

        // only create entry if it doesn't exist yet
        Document doc = new Document();
        doc.append("sender", senderName);
        doc.append("target", targetName);

        Bson update = Updates.combine(
                Updates.set("sender", senderName),
                Updates.set("target", targetName)
        );

        UpdateOptions options = new UpdateOptions().upsert(true);

        collection.updateOne(doc, update, options);
        return true;
    }
}
