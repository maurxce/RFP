package me.lewd.poke;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;


public final class Main extends JavaPlugin implements Listener {

    public MongoClient client;
    public MongoDatabase db;
    public MongoCollection<Document> collection;
    public static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        // connect to db
        client = new MongoClient();
        db = client.getDatabase("poke");
        collection = db.getCollection("pokes");

        getCommand("poke").setExecutor(new Poke());
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        BasicDBObject query = new BasicDBObject();
        query.put("target", player.getName());

        FindIterable<Document> cursor = collection.find(query);
        for (Document doc : cursor) {
            String sender = doc.getString("sender");
            player.sendMessage(sender + " poked you!");

            // delete entry
            Document filter = new Document();
            filter.append("sender", sender);
            filter.append("target", player.getName());

            collection.deleteOne(filter);
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}

// https://www.reddit.com/r/admincraft/comments/y2bkka/comment/is39dy4/?utm_source=share&utm_medium=web2x&context=3