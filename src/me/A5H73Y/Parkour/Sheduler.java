package me.A5H73Y.Parkour;

import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import ru.komiss77.ApiOstrov;


public class Sheduler  extends BukkitRunnable{


    Sheduler(Parkour plugin) {
        System.out.print("Parkour tick");
    }

    @Override
    public void run() {
        // for (Player p: Bukkit.getOnlinePlayers()) {
        Bukkit.getOnlinePlayers().stream().filter((p) -> (Parkour.inProgress.contains(p.getName()))).forEach((p) -> {
            int t = (int) Parkour.timeCurrent.get(p.getName());
            t++;
            final long n0 = TimeUnit.SECONDS.toHours(t);
            final long n1 = TimeUnit.SECONDS.toMinutes(t) - TimeUnit.SECONDS.toHours(t) * 60L;
            final long n2 = TimeUnit.SECONDS.toSeconds(t) - TimeUnit.SECONDS.toMinutes(t) * 60L;
            Parkour.timeCurrent.put(p.getName(), t);
            
            t = (int) Parkour.timeTotal.get(p.getName());
            t++;
            final long n3 = TimeUnit.SECONDS.toHours(t);
            final long n4 = TimeUnit.SECONDS.toMinutes(t) - TimeUnit.SECONDS.toHours(t) * 60L;
            //final long n4 = TimeUnit.SECONDS.toSeconds(t) - TimeUnit.SECONDS.toMinutes(t) * 60L;
            Parkour.timeTotal.put(p.getName(), t);
            
            final String current = ChatColor.AQUA +  String.format("%02d", n0) + ":" +String.format("%02d", n1) + ":" + String.format("%02d", n2);
            final String total = ChatColor.GOLD + String.format("%02d", n3) + "ч. " + String.format("%02d", n4)+ " мин.";
            ApiOstrov.sendActionBarDirect(p, "§bВремя прохождения: §f"+current+ "  §2(всего: §a" + total+"§2 )");
        });
    }
    
    
}
