package xyz.n7mn.dev.playerjoinchecker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.Enumeration;

public class PermCommand implements CommandExecutor {

    private final Plugin plugin;
    public PermCommand (Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && args.length == 0){
            Player player = (Player) sender;
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
                        "SELECT * FROM MinecraftUserList a, RoleRankList b\n" +
                        " WHERE a.RoleUUID = b.UUID\n" +
                        "   AND a.MinecraftUUID = ?\n"
                );

                statement.setString(1, player.getUniqueId().toString());
                ResultSet set = statement.executeQuery();

                if (set.next()){
                    player.sendMessage(ChatColor.YELLOW + "[ななみ鯖] "+ChatColor.RESET+"あなたの権限は "+set.getString("Name") + " です。");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "[ななみ鯖] "+ChatColor.RESET+"あなたの権限は 一般(未認証) です。");
                }

                set.close();
                statement.close();
                con.close();

            } catch (SQLException e){
                e.printStackTrace();

            }
        }

        return true;
    }
}
