package me.Romindous.ParkHub;


import me.Romindous.ParkHub.builder.LocalBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.events.BungeeDataRecieved;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Timer;
import ru.komiss77.enums.Stat;
import ru.komiss77.events.BuilderMenuEvent;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.DonatEffect;






public class ListenerPlayer implements Listener {

    
    private static boolean anticheat = false;
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBuilderMenu(final BuilderMenuEvent e) {
        LocalBuilder.open(e.getPlayer(), e.getSetupMode());
    }
    
    
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
                rs = stmt.executeQuery( "SELECT * FROM `parkData` WHERE `name` = '"+pd.name+"';" );
                
                while (rs.next()) {
                    if (Main.trasses.containsKey(rs.getInt("trasseID"))) {
                        final Progress go = new Progress(rs.getInt("done"),rs.getInt("checkPoint"),rs.getInt("trasseTime"),rs.getInt("trasseJump"),rs.getInt("trasseFalls"),rs.getBoolean("cheat"));
                        pd.progress.put(rs.getInt("trasseID"), go);
                        pd.totalCheckPoints+=go.checkPoint;
                        pd.totalTime+=go.trasseTime;
                        pd.totalJumps+=go.trasseJump;
                        pd.totalFalls+=go.trasseFalls;
                        if(go.cheat) {
                            if (!pd.cheat) {
                                p.sendMessage("§cМы обнаружили, что Вы проходили паркуры с читами. Можете продолжать в том же духе, но в ТОП Ваши результаты не попадут.");
                            }
                            pd.cheat = true;
                        }
                    } else {
                        Ostrov.log_warn("Загрузка прогресса "+pd.name+" : нет паркура с ИД "+rs.getInt("trasseID")+"!");
                    }
                }
                
            } catch (SQLException ex) {

                Ostrov.log_err("onBungeeData error  "+pd.name+" -> "+ex.getMessage());

            } finally {
                
                Ostrov.sync( ()->{
                    Main.lobbyScore(p);
                    broadcast(p, pd.cheat);
                }, 0);
                
                try{
                    if (rs!=null && !rs.isClosed()) rs.close();
                    if (stmt!=null) stmt.close();
                } catch (SQLException ex) {
                    Ostrov.log_err("onBungeeData close error - "+ex.getMessage());
                }
            }
            
        }, 0);
        

        

        
        for (Player pl:Bukkit.getOnlinePlayers()) {
            pl.setPlayerListFooter( "§7Паркуристов на трассах: §b" + Bukkit.getOnlinePlayers().size());
        }

    }

    
    private static void broadcast(final Player p, final boolean cheat) {
        
        String msg;
        if (cheat) {
            msg = "§c - читак!";
        } else {
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
        } 
        
        Bukkit.broadcast(Component.text("§b" + p.getName() + msg));

        if (cheat) {
            msg = "§c Рабочих читов!";
        } else {
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
        }
        p.setPlayerListHeader("§7<§bПаркуры§7>\n" + msg);
    }
    
    
    @EventHandler
    public void onQuit (final PlayerQuitEvent e) {
        final PD pd = Main.data.remove(e.getPlayer().getName());
        if (pd!=null) {
            if (pd.current!=null) {
                pd.saveProgress(pd.current.id);
            }
        }
        for (Player pl:Bukkit.getOnlinePlayers()) {
            pl.setPlayerListFooter( "§7Паркуристов на трассах: §b" + (Bukkit.getOnlinePlayers().size()-1) );
        }
    }
    
    
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onJump(final PlayerJumpEvent e) {    
        final PD pd = Main.data.get(e.getPlayer().getName());
        pd.jump();
    }
    
    
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlate(final PlayerInteractEvent e) {
        if (e.getAction()!=Action.PHYSICAL) return;
        final PD pd = Main.data.get(e.getPlayer().getName());
        if (pd==null || pd.current==null) return;
        final Location loc = e.getClickedBlock().getLocation();
        
        if (pd.nextCpX==loc.getBlockX() && pd.nextCpY==loc.getBlockY() && pd.nextCpZ==loc.getBlockZ() ) {
            
            final Player p = e.getPlayer();
            final Trasse t = pd.current;
            final Progress go = pd.getProgress(pd.current.id);
            
            final CheckPoint cp = t.getCp(go.checkPoint);

            final String log = "reach point:"+go.checkPoint+" ("+pd.nextCpX+","+pd.nextCpY+","+pd.nextCpZ+") by time:"+pd.stageTime+"/"+cp.controlTime+"  jump:"+pd.stageJump+"/"+cp.controlJump + " fall:"+pd.stageFall+"/"+cp.controlFall;
            if (anticheat && p.getGameMode()==GameMode.SURVIVAL && (pd.stageTime<cp.controlTime || pd.stageJump<cp.controlJump || pd.stageFall<cp.controlFall) ) {
                if (!go.cheat) { //до этого не было чита - пометить везде
                    LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `parkData` SET `cheat` = '1' WHERE `name`='"+pd.name+"'");
                }
                go.cheat = true;
                LocalDB.executePstAsync(Bukkit.getConsoleSender(), "INSERT INTO `cheatLog` (name,parkName,log,stamp) VALUES ('"+p.getName()+"', '"+ChatColor.stripColor(t.displayName)+"', '"+log+"', '"+Timer.getTime()+"');");
            }
//p.sendMessage("§8log: "+(go.cheat?"§cCHEAT!§8 " :"§aOK§8 ")+log);
            go.checkPoint++;
            
            if (t.isLastCp(go.checkPoint)) { //прошел
                go.done++;
                t.totalDone++;
                t.totalTime+=go.trasseTime;
                t.totalJumps+=go.trasseJump;
                t.totalFalls+=go.trasseFalls;
                pd.resetTrasse(); //если не ресануть, плита сразу срабатывает снова
                
                ApiOstrov.sendTitle(p, "", "§fВы прошли паркур!");
                
                if (pd.cheat) {
                    
                    p.sendMessage("§c*Читеры проходят без почестей*");
                    Main.lobbyPlayer(p);
                    //t.saveStat(); на читеров не делаем
                    
                } else {
                    
                    ApiOstrov.moneyChange(p, t.pay, "Паркуры");
                    ApiOstrov.addStat(p, Stat.PA_done);
                    PM.getOplayer(pd.name).score.getSideBar().reset();
                    p.getInventory().setItem(0, new ItemStack(Material.AIR));
                    p.getInventory().setItem(2, new ItemStack(Material.AIR));
                    p.getInventory().setItem(4, new ItemStack(Material.AIR));
                    DonatEffect.spawnRandomFirework(p.getLocation());
                    p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 240, 0));
                    
                    final String name = p.getName();
                    pd.task = new BukkitRunnable() {
                        int count = 10;
                        @Override
                        public void run() {
                            final Player p = Bukkit.getPlayerExact(name);
                            if (p==null || !p.isOnline()) {
                                this.cancel();
                            }
                            ApiOstrov.sendActionBarDirect(p, "§6Возвращение в лобби через: §e"+count);
                            if (count==0) {
                                this.cancel();
                                Main.lobbyPlayer(p);
                            }
                            count--;
                            if (count==14 && t.level==Level.Нормально) DonatEffect.spawnRandomFirework(p.getLocation());
                            if (count==13 && t.level==Level.Трудно) DonatEffect.spawnRandomFirework(p.getLocation());
                            if (count==12 && t.level==Level.Нереально) DonatEffect.spawnRandomFirework(p.getLocation());
                        }
                    }.runTaskTimer(Main.plug, 1, 20);
                    
                    if (!go.cheat) {//тихонько сохраним для нечитеров
                        t.saveStat();
                        final String msg = "§f"+pd.name+(ApiOstrov.isFemale(pd.name)?" §7прошла трассу ":" §7прошел трассу ")+t.displayName+" §7за §e"+ApiOstrov.secondToTime(go.trasseTime)+" §7(⇪: §6"+go.trasseJump+"§7, ☠: §4"+go.trasseFalls+"§7)";
                    } 
                    
                }
                
                p.playSound(e.getClickedBlock().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
                
                
                
                return;
            }
            

            final CheckPoint next = pd.current.getNextCp(go.checkPoint);
          
            pd.setNextPoint(next);
            p.setCompassTarget(next.getLocation(p.getWorld().getName()));
            
            ApiOstrov.sendTitle(p, "", "§7Чекпоинт §b#" + (go.checkPoint+1)+" §7пройден.");
            p.playSound(e.getClickedBlock().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1 + ((float) go.checkPoint / (float) t.size()));
            
            pd.saveProgress(t.id);
            ApiOstrov.addStat(p, Stat.PA_chpt);
        }
    }
    
     
   
}
