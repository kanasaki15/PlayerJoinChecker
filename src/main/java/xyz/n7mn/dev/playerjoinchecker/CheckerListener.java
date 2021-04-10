package xyz.n7mn.dev.playerjoinchecker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;

public class CheckerListener implements Listener {

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
    public void PlayerQuitEvent (PlayerQuitEvent e){
        String name = e.getPlayer().getName();
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();

        if (e.getQuitMessage().length() == 0){
            new Thread(()->{
                for (Player player : players){
                    if (player.isOp() || player.hasPermission("7misys.op")){
                        player.sendMessage(ChatColor.YELLOW +"[ななみ鯖] " + ChatColor.RESET + name + "さんが退出しました。");
                    }
                }
            }).start();
        }
    }

}
