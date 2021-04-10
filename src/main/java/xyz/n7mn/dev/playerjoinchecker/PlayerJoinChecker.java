package xyz.n7mn.dev.playerjoinchecker;

import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerJoinChecker extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        getServer().getPluginManager().registerEvents(new CheckerListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
