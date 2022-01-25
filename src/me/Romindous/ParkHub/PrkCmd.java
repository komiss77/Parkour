package me.Romindous.ParkHub;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.komiss77.modules.player.profile.FriendView;
import ru.komiss77.utils.inventory.SmartInventory;


public class PrkCmd implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(final CommandSender send, final Command cmd, final String al, final String[] args) {
        final List<String> sugg = new ArrayList<>();
       /* if (send.hasPermission("ostrov.builder")) {
                if (args.length == 1) {
                        sugg.add("create");
                        sugg.add("join");
                        sugg.add("leave");
                        sugg.add("restart");
                        sugg.add("setlobby");
                        sugg.add("edit");
                        sugg.add("delete");
                        sugg.add("addchpt");
                        sugg.add("remchpt");
                        sugg.add("clear");
                        sugg.add("finish");
                        sugg.add("reload");
                        sugg.add("help");
                } else if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("remchpt") || args[0].equalsIgnoreCase("addchpt") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("finish") || args[0].equalsIgnoreCase("edit")) {
                                if (!Main.prks.contains("maps")) {
                                        return null;
                                }
                                for (final String s : Main.prks.getConfigurationSection("maps").getKeys(false)) {
                                        sugg.add(s);
                                }
                        } else {
                                return sugg;
                        }
                }
        }*/
        return sugg;
    }

        
        
        
        
        
        
        
        
        
        
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        if (!(sender instanceof Player)) {
            return true;
        }
        
        final Player p =(Player) sender;
        
        if (args.length==0) {
            SmartInventory
                .builder()
                .id(p.getName())
                .provider(new MainMenu())
                .size(6, 9)
                .title("§bВыбор Карты")
                .build()
                .open(p);            
        } else {
            if (args[0].equalsIgnoreCase("stat")) {
                p.sendMessage("stat");
            }
        }
    
        
        
        return true;

    }
    
    
    
}
