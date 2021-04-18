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

        try {
            boolean found = false;
            Enumeration<Driver> drivers = DriverManager.getDrivers();

            while (drivers.hasMoreElements()){
                Driver driver = DriverManager.getDrivers().nextElement();
                if (driver.equals(new com.mysql.cj.jdbc.Driver())){
                    found = true;
                    break;
                }
            }

            if (!found){
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            }

        } catch (SQLException e){
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }

        getServer().getPluginManager().registerEvents(new CheckerListener(this), this);
        getLogger().info(this.getName() + " " + this.getDescription().getVersion() + " Loaded!!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
