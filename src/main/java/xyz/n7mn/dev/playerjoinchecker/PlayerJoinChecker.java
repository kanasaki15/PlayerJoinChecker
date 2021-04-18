package xyz.n7mn.dev.playerjoinchecker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public final class PlayerJoinChecker extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic]
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new CheckerListener(this), this);
        getLogger().info(this.getName() + " " + this.getDescription().getVersion() + " Loaded!!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
