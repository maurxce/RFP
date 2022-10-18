package me.lewd.poke;

import me.lewd.poke.commands.Info;
import me.lewd.poke.commands.List;
import me.lewd.poke.commands.Poke;
import me.lewd.poke.listeners.PlayerJoinListener;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.dizitart.no2.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public final class Main extends JavaPlugin {

    public static Main instance;

    private FileConfiguration conf = new YamlConfiguration();
    private Nitrite db;
    private NitriteCollection coll;

    @Override
    public void onEnable() {
        instance = this;

        initializeFiles();
        initializeDatabase();

        registerCommands();
        registerEvents();
    }

    private void initializeFiles() {
        File dataDir = new File(getDataFolder(), "data");
        InputStreamReader confFile = new InputStreamReader(getClassLoader().getResourceAsStream("conf.yml"));

        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        if (!dataDir.exists()) dataDir.mkdirs();

        try {
            conf.load(confFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void initializeDatabase() {
        db = Nitrite.builder()
                .filePath(getDataFolder().getAbsolutePath() + "/data/pokes.db")
                .openOrCreate();

        coll = db.getCollection("pokes");
    }

    private void registerCommands() {
        getCommand("poke").setExecutor(new Poke());
        getCommand("pokes").setExecutor(new List());
        getCommand("info").setExecutor(new Info());
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    public FileConfiguration getDevConf() { return conf; }
    public Nitrite getDatabase() { return db; }

    public String getAuthor() { return getDescription().getAuthors().get(0); }
    public String getVersion() { return getDescription().getVersion(); }

    @Override
    public void onDisable() {
        instance = null;

        closeDatabase();
    }

    private void closeDatabase() {
        if (db.hasUnsavedChanges()) db.commit();
        if (!db.isClosed()) db.close();
    }
}

// https://www.reddit.com/r/admincraft/comments/y2bkka/comment/is39dy4/?utm_source=share&utm_medium=web2x&context=3