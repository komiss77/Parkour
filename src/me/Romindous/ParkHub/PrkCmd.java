package me.Romindous.ParkHub;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Timer;
import ru.komiss77.utils.inventory.ConfirmationGUI;
import ru.komiss77.utils.inventory.SmartInventory;


public class PrkCmd implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(final CommandSender send, final Command cmd, final String al, final String[] args) {
       // final List<String> sugg = new ArrayList<>();
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
        return Arrays.asList("leave","suicide","restart","way","exit");
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
            
            final PD pd = Main.data.get(p.getName());
            
            switch (args[0]) {
                
               /* case "builder":
                    if (!ApiOstrov.isLocalBuilder(p, true)) {
                        return false;
                    }
                    final Oplayer op = PM.getOplayer(p);
                    if (op.setup==null) {
                        op.setup = new SetupMode(p);
                    }
                    return true;*/
                    
                //case "stat":
                    //p.sendMessage("stat");
                    //return true;
                
                case "leave":
                    Main.lobbyPlayer(p);
                    return true;
                
                case "suicide":
                    if (pd.current==null) {
                        p.sendMessage("§6Вы не на трассе!");
                        return true;
                    }
                    pd.fall();
                    Main.joinParkur(p, pd.current.id);
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                    return true;
                
                case "restart":
                    if (pd.current==null) {
                        p.sendMessage("§6Вы не на трассе!");
                        return true;
                    }
                    ConfirmationGUI.open(p, "§4Сбросить и пройти заново?", (confirm)-> {
                            
                        if (confirm) {
                            final Progress go = pd.getProgress(pd.current.id);
                            go.reset();
                            pd.saveProgress(pd.current.id);
                            Main.joinParkur(p, pd.current.id);
                            p.playSound(p.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1, 1.5f);
                        } else {
                            p.closeInventory();
                        }

                    });
                    
                case "way":
                    if (pd.current==null) {
                        p.sendMessage("§6Вы не на трассе!");
                        return true;
                    }
                    if (Timer.has(p, "way")) {
                        p.sendMessage("§6До следующего использования: "+Timer.getLeft(p, "way"));
                        return true;
                    }
                    Timer.add(p, "way", 15);
                    final CheckPoint cp = pd.current.getNextCp(pd.getProgress(pd.current.id).checkPoint);
                    Location from = p.getLocation();
                    final Location to = cp.getLocation(p.getWorld().getName());
                    final Vector v = to.clone().subtract(from).toVector().normalize();
                    from.add(v);
                    int limit = 36;
                    while (from.getBlockX()!=to.getBlockX() && from.getBlockY()!=to.getBlockY() && from.getBlockZ()!=to.getBlockZ()) {
                        from.add(v);
                        from.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, from, 1, 0, 0, 0);
                        limit--;
                        if (limit==0) break;
                    }
                    return true;
                
                case "exit":
                    ApiOstrov.sendToServer(p, "lobby0", "");
                    return true;
                
                
            }

        }
    
        
        
        return true;

    }
    
    
    
}
