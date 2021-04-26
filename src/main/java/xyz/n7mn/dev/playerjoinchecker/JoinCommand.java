package xyz.n7mn.dev.playerjoinchecker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.Enumeration;

public class JoinCommand implements CommandExecutor {

    private final Plugin plugin;
    public JoinCommand (Plugin plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1 && args.length != 0){
            return true;
        }

        if (sender instanceof Player){
            Player player = (Player) sender;
            if (!player.isOp() && !player.hasPermission("7misys.op")){
                return true;
            }
        }

        if (args.length == 0){

            int nowRank = plugin.getConfig().getInt("DefaultJoinPermRank");

            new Thread(()->{
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

                    PreparedStatement statement = con.prepareStatement("SELECT * FROM RoleRankList");
                    ResultSet set = statement.executeQuery();

                    while (set.next()){

                        if (set.getInt("Rank") != nowRank){
                            continue;
                        }

                        sender.sendMessage(ChatColor.YELLOW + "[ななみ鯖] " + ChatColor.RESET + "現在の入室権限 " + set.getString("Name") + " 以上\n設定するには /join <権限レベル> を実行してください。");
                        break;

                    }

                    set.close();
                    statement.close();

                    con.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }).start();

            return true;
        }

        try {
            int level = Integer.parseInt(args[0]);
            plugin.getConfig().set("DefaultJoinPermRank", level);

            new Thread(()->{
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

                    PreparedStatement statement = con.prepareStatement("SELECT * FROM RoleRankList");
                    ResultSet set = statement.executeQuery();

                    while (set.next()){
                        if (set.getInt("Rank") != level){
                            continue;
                        }
                        sender.sendMessage(ChatColor.YELLOW + "[ななみ鯖] " + ChatColor.RESET + "入室権限を" + set.getString("Name") + " 以上にしました。");
                    }

                    set.close();
                    statement.close();

                    con.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e){
            // e.printStackTrace();
        }

        return true;
    }
}
