package me.Romindous.ParkHub;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.komiss77.Ostrov;
import ru.komiss77.Timer;




public class Trasse {

    public final int id;
    public String displayName;
    public String creator = "";
    public int createAt = Timer.getTime();
    public Material mat = Material.FEATHER;
    public String descr = "";
    public Level level = Level.Легко;
    public int pay;
    
    public final String worldName;
    public final HashSet<String> inProgress;
    //public boolean service = false; //загружена в режим билдера
    public boolean disabled = false;
    public boolean changed = false; //для иконки сохранения
    
    public LinkedList<CheckPoint> points = new LinkedList();
    
    //стата
    public int totalDone;
    public int totalTime;
    public int totalJumps;
    public int totalFalls;
	
    public Trasse(final int id, final String displayName, final String worldName, final LinkedList<CheckPoint> points) {
        inProgress = new HashSet<>();
        
        this.id = id;
        this.displayName = displayName;
        this.worldName = worldName;
        this.points = points;
        
    }



    
    
    
    
    
    
    
    
    
    
    


    public void broadCastMsg(final String msg) {
        for (final Player p : getPlayers()) {
            p.sendMessage(msg);
        }
    }

    public List<Player> getPlayers() {
        final List<Player>list = new ArrayList<>(inProgress.size());
        for (String name : inProgress) {
            if (Bukkit.getPlayer(name)!=null) {
                list.add(Bukkit.getPlayer(name));
            }
        }
        return list;
    }

    public int size() {
        return points.size();
    }

    public CheckPoint getCp(final int current) {
        return points.get(current);
    }
    
    public CheckPoint getCp(final Location loc) {
        for (CheckPoint cp:points) {
            if (cp.x==loc.getBlockX() && cp.y==loc.getBlockY() && cp.z==loc.getBlockZ()) {
                return cp;
            }
        }
        return null;
    }

    
    public CheckPoint getNextCp(final int current) {
        if (current<0 || current>=points.size()-1) {
            return points.getFirst();
        }
        return points.get(current+1);
    }   
    
    public CheckPoint getNextCp(final CheckPoint current) {
        final int idx = points.indexOf(current);
        return getNextCp(idx);
    }

    public void saveConfig(final CommandSender cs) {
        final String path = "trasses."+id+".";
        Main.parkData.set (path+"displayName", displayName);
        Main.parkData.set (path+"worldName", worldName);
        Main.parkData.set (path+"mat", mat.name());
        Main.parkData.set (path+"descr", descr);
        Main.parkData.set (path+"level", level.name());
        Main.parkData.set (path+"pay", pay);
        Main.parkData.set (path+"disabled", disabled);
        Main.parkData.set (path+"creator", creator);
        Main.parkData.set (path+"createAt", createAt);
        
        final List<String>list = new ArrayList<>(points.size());
        for (CheckPoint cp : points) {
            list.add(cp.toString());
        }
        Main.parkData.set (path+"points", list);
        Main.parkData.saveConfig();
        changed = false;
        Ostrov.log_ok("Данные паркура "+displayName+" сохранены.");
        if (cs!=null) {
            cs.sendMessage("Данные паркура "+displayName+" сохранены.");
        }
    }
    
    public void saveStat() {
        Main.parkStat.set ("stat."+id+".parkName", displayName);
        Main.parkStat.set ("stat."+id+".totalDone", totalDone);
        Main.parkStat.set ("stat."+id+".totalTime", totalTime);
        Main.parkStat.set ("stat."+id+".totalJumps", totalJumps);
        Main.parkStat.set ("stat."+id+".totalFalls", totalFalls);
        Main.parkStat.saveConfig();
        Ostrov.log_ok("Статистика паркура "+displayName+" обновлена!");    }

    
    
    //public boolean isCompleted(final PD pd) {
    //    final Progress go = pd.getProgress(id);
    //    return go.checkPoint>=1 && !points.isEmpty() && go.checkPoint >= points.size()-1;
    //}    
    
   // public boolean hasProgress(final @Nullable PD pd) {
   //     return pd != null &&  pd.getProgress(id) != null && pd.getProgress(id).haProgress();
   // }

    public boolean isLastCp(final int cpNumber) {
        return cpNumber >= points.size()-1;
    }
    
}
