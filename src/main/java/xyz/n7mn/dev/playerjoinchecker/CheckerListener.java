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
import java.util.Enumeration;

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

    @EventHandler
    public void AsyncPlayerPreLoginEvent (AsyncPlayerPreLoginEvent e){

        // System.out.println("test2");

        int rank = 0;
        String userRankName = "一般";
        String reqRankName;
        try {
            boolean found = false;
            Enumeration<Driver> drivers = DriverManager.getDrivers();

            while (drivers.hasMoreElements()){
                Driver driver = drivers.nextElement();
                if (driver.equals(new com.mysql.cj.jdbc.Driver())){
                    found = true;
                    break;
                }
            }

            if (!found){
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            }

            Connection con = DriverManager.getConnection("" +
                    "jdbc:mysql://"+
                    plugin.getConfig().getString("MySQLServer")+ ":"+plugin.getConfig().getInt("MySQLPort")+ "/"+
                    plugin.getConfig().getString("MySQLDatabase")+
                    plugin.getConfig().getString("MySQLOption"),
                    plugin.getConfig().getString("MySQLUsername"),
                    plugin.getConfig().getString("MySQLPassword")
            );
            con.setAutoCommit(true);

            PreparedStatement statement = con.prepareStatement("" +
                    "SELECT * FROM MinecraftUserList, RoleRankList " +
                    "WHERE MinecraftUserList.RoleUUID = RoleRankList.UUID" +
                    "  AND MinecraftUUID = ?;"
            );
            statement.setString(1, e.getUniqueId().toString());
            ResultSet set = statement.executeQuery();

            if (set.next()){
                rank = set.getInt("Rank");
                userRankName = set.getString("Name");
            }

            set.close();
            statement.close();

            PreparedStatement statement2 = con.prepareStatement("SELECT * FROM RoleRankList");
            ResultSet set2 = statement2.executeQuery();

            StringBuffer sb = new StringBuffer();
            while (set2.next()){
                if (set2.getInt("Rank") == plugin.getConfig().getInt("DefaultJoinPermRank")){
                    sb.append(set2.getString("Name"));
                    sb.append(",");
                }
            }
            reqRankName = sb.toString();

            set2.close();
            statement2.close();
            con.close();
        } catch (SQLException ex){
            ex.printStackTrace();
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "\n" +
                    "--- ななみ鯖 ---\n" +
                    "現在サーバーメンテナンス中です。しばらくお待ち下さい。" +
                    "\n" +
                    "詳しくはDiscordまで : "+plugin.getConfig().getString("DiscordInviteLink")
            );
            return;
        }

        if (rank >= plugin.getConfig().getInt("DefaultJoinPermRank")){
            e.allow();
            return;
        }

        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "\n" +
                "--- ななみ鯖 ---\n" +
                "現在あなたの権限では入室できません。\n" +
                "あなたの権限 : " + userRankName + "\n" +
                "必要な権限 : " + reqRankName.substring(0, reqRankName.length() - 1) + "以上\n" +
                "\n" +
                "詳しくはDiscordまで : "+plugin.getConfig().getString("DiscordInviteLink")
        );

        String finalUserRankName = userRankName;
        new Thread(()->{
            for (Player player : Bukkit.getServer().getOnlinePlayers()){
                if (player.isOp() || player.hasPermission("7misystem.op")){
                    player.sendMessage(ChatColor.YELLOW + "[ななみ鯖] "+ChatColor.RESET+e.getName()+"さんが入室を試みました。(権限: "+ finalUserRankName +")");
                }
            }
        }).start();
    }

}
