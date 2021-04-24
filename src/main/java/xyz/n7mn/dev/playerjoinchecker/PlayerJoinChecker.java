package xyz.n7mn.dev.playerjoinchecker;

import org.bukkit.plugin.java.JavaPlugin;


public final class PlayerJoinChecker extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic]
        saveDefaultConfig();

        getCommand("perm").setExecutor(new PermCommand(this));
        getCommand("join").setExecutor(new JoinCommand(this));


        getServer().getPluginManager().registerEvents(new CheckerListener(this), this);
        getLogger().info(this.getName() + " " + this.getDescription().getVersion() + " Loaded!!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
