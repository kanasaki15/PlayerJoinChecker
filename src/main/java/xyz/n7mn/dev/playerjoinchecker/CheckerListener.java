package xyz.n7mn.dev.playerjoinchecker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.Collection;

public class CheckerListener implements Listener {

    private final Plugin plugin;
    public CheckerListener(Plugin plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerJoinEvent(PlayerJoinEvent e){
        String name = e.getPlayer().getName();
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();

        new Thread(()->{
            for (Player player : players){
                if (player.isOp() || player.hasPermission("7misys.op")){
                    player.sendMessage(ChatColor.YELLOW +"[ななみ鯖] " + ChatColor.RESET + name + "さんが入室しました。");
                }
            }
        }).start();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerQuitEvent(PlayerQuitEvent e){
        String name = e.getPlayer().getName();
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();

        new Thread(()->{
            for (Player player : players){
                if (player.isOp() || player.hasPermission("7misys.op")){
                    player.sendMessage(ChatColor.YELLOW +"[ななみ鯖] " + ChatColor.RESET + name + "さんが退出しました。");
                }
            }
        }).start();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void AsyncPlayerPreLoginEvent (AsyncPlayerPreLoginEvent e){

        try {
            Connection con = DriverManager.getConnection("" +
                    "jdbc:mysql://"+
                    plugin.getConfig().getString("MySQLServer")+ ":"+plugin.getConfig().getInt("MySQLPort")+ "/"+
                    plugin.getConfig().getString("MySQLDatabase")+
                    plugin.getConfig().getString("MysqlOption"),
                    plugin.getConfig().getString("MysqlUsername"),
                    plugin.getConfig().getString("MysqlPassword")
            );
            con.setAutoCommit(true);

            PreparedStatement statement = con.prepareStatement("SELECT * FROM MinecraftUserList WHERE MinecraftUUID = ?");
            statement.setString(1, e.getUniqueId().toString());
            ResultSet set = statement.executeQuery();


            set.close();
            statement.close();
            con.close();
        } catch (SQLException ex){
            ex.printStackTrace();
        }

    }

}
