package me.Romindous.ParkHub.Listener;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.events.BungeeDataRecieved;
import me.Romindous.ParkHub.Main;
import me.Romindous.ParkHub.PD;
import me.Romindous.ParkHub.Progress;






public class MainLis implements Listener {

    

	
    
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBungeeData(final BungeeDataRecieved e) {
        
        final Player p = e.getPlayer();
        Main.lobbyPlayer(e.getPlayer());
       // final String wantPK = PM.getOplayer(p.getName()).getDataString(Data.WANT_ARENA_JOIN);
        
        final PD pd = new PD(p);
        Main.data.put(p.getName(), pd);
        
        Ostrov.async( () -> {

            Statement stmt = null;
            ResultSet rs = null;
            
            try {  
                stmt = LocalDB.GetConnection().createStatement(); 
                rs = stmt.executeQuery( "SELECT * FROM `playerData` WHERE `name` = '"+pd.name+"';" );
                
                while (rs.next()) {
                    if (Main.trasses.containsKey(rs.getInt("trasseID"))) {
                        final Progress go = new Progress(rs.getInt("checkPoint"),rs.getInt("trasseTime"),rs.getInt("trasseJump"),rs.getInt("trasseFalls"),rs.getBoolean("cheat"));
                        pd.progress.put(rs.getInt("trasseID"), go);
                        pd.totalCheckPoints+=go.checkPoint;
                        pd.totalTime+=go.trasseTime;
                        pd.totalJumps+=go.trasseJump;
                        pd.totalFalls+=go.trasseFalls;
                        if(go.cheat) pd.cheat = true;
                    } else {
                        Ostrov.log_warn("Загрузка прогресса "+pd.name+" : нет паркура с ИД "+rs.getInt("trasseID")+"!");
                    }
                }
                
            } catch (SQLException ex) {

                Ostrov.log_err("onBungeeData error  "+pd.name+" -> "+ex.getMessage());

            } finally {
                
                Ostrov.sync( ()->Main.lobbyScore(p.getName()), 0);
                
                try{
                    if (rs!=null && !rs.isClosed()) rs.close();
                    if (stmt!=null) stmt.close();
                } catch (SQLException ex) {
                    Ostrov.log_err("onBungeeData close error - "+ex.getMessage());
                }
            }
            
        }, 0);
        

        
        String msg;
        switch (Ostrov.random.nextInt(4)) {
            case 0:
                msg = "§7 зашел расслабиться!";
                break;
            case 1:
                msg = "§7 пришел пошалить!";
                break;
            case 2:
                msg = "§7 прибыл в здание!";
                break;
            case 3:
                msg = "§7 готов кайфовать!";
                break;
            default:
                msg = "";
                break;
        }
        Bukkit.broadcastMessage("§b" + e.getPlayer().getName() + msg);

        switch (Ostrov.random.nextInt(4)) {
            case 0:
                msg = "Добро пожаловать!";
                break;
            case 1:
                msg = "Приятной игры!";
                break;
            case 2:
                msg = "Желаем удачи!";
                break;
            case 3:
                msg = "Развлекайтесь!";
                break;
            default:
                msg = "";
                break;
        }
        e.getPlayer().setPlayerListHeaderFooter("§7<§bПаркуры§7>\n" + msg, "§7Сейчас паркурят: §b" + Bukkit.getOnlinePlayers().size() + " §7человек!");
    }

    
    
    
    
    @EventHandler
    public void onQuit (final PlayerQuitEvent e) {
        final PD pd = Main.data.remove(e.getPlayer().getName());
        if (pd!=null) {
            if (pd.current!=null) {
                pd.saveProgress(pd.current.id);
            }
        }
    }
    
    
    
    
    
    
     
   
}
