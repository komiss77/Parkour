package me.Romindous.ParkHub;

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
import ru.komiss77.events.LocalDataLoadEvent;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ParticleUtil;
import me.Romindous.ParkHub.builder.LocalBuilder;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import ru.komiss77.builder.SetupMode;
import ru.komiss77.utils.ScreenUtil;
import ru.komiss77.utils.TCUtil;
import ru.komiss77.utils.TimeUtil;






public class PlayerLst implements Listener {

    private static final boolean SET_CHEAT_MARK = false;
    private static final boolean USE_ANTICHEAT = true;
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBuilderMenu(final BuilderMenuEvent e) {
        LocalBuilder.open(e.getPlayer(), e.getSetupMode());
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final SetupMode sm = PM.getOplayer(e.getPlayer()).setup;
        if (sm!=null) {
            sm.loacalEditMode = null;
        }
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onWorldChange (final PlayerChangedWorldEvent e) {
        final SetupMode sm = PM.getOplayer(e.getPlayer()).setup;
        if (sm!=null) {
            sm.loacalEditMode = null;
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onLocalData(final LocalDataLoadEvent e) {
        e.setLogoutLocation(null);
   // }
   // @EventHandler(priority = EventPriority.NORMAL)
   // public void onBungeeData(final BungeeDataRecieved e) {
        
        final Player p = e.getPlayer();
        Main.lobbyPlayer(e.getPlayer());
       // final String wantPK = PM.getOplayer(p.getName()).getDataString(Data.WANT_ARENA_JOIN);
        
        final PD pd = new PD(p);
        Main.data.put(p.getName(), pd);
        
        Ostrov.async( () -> {

            Statement stmt = null;
            ResultSet rs = null;
            
            try {  
                stmt = LocalDB.getConnection().createStatement(); 
                
                rs = stmt.executeQuery( "SELECT * FROM `parkData` WHERE `name` = '"+pd.name+"';" );
                while (rs.next()) {
                    if (Main.trasses.containsKey(rs.getInt("trasseID"))) {
                        final Progress go = new Progress(rs.getInt("done"),rs.getInt("checkPoint"),rs.getInt("trasseTime"),rs.getInt("trasseJump"),rs.getInt("trasseFalls"),rs.getBoolean("cheat"));
                        pd.progress.put(rs.getInt("trasseID"), go);
                        pd.totalCheckPoints += go.checkPoint;
                        pd.totalTime += go.trasseTime;
                        pd.totalJumps += go.trasseJump;
                        pd.totalFalls += go.trasseFalls;
                        if(go.cheat) {
                            if (!pd.cheat) {
                                p.sendMessage("§cМы заподозрили, что Вы могли использовать читы. Если опасения подвердятся, ваш результат будет аннулирован.");
                            }
                            if (SET_CHEAT_MARK) pd.cheat = true;
                        }
                    } else {
                        Ostrov.log_warn("Загрузка прогресса "+pd.name+" : нет паркура с ИД "+rs.getInt("trasseID")+"!");
                    }
                }
                rs.close();
                
                rs = stmt.executeQuery( "SELECT * FROM `completions` WHERE `name` = '"+pd.name+"';" );
                while (rs.next()) {
                    if (Main.trasses.containsKey(rs.getInt("trasseID"))) {
                        final CompleteInfo ci = new CompleteInfo(rs.getInt("done"),rs.getInt("time"),rs.getInt("jump"),rs.getInt("falls"));
                        pd.completions.put(rs.getInt("trasseID"), ci);
                    } else {
                        Ostrov.log_warn("Загрузка прохождений "+pd.name+" : нет паркура с ИД "+rs.getInt("trasseID")+"!");
                    }
                }
                rs.close();
                
                
                
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
            pl.sendPlayerListFooter( Component.text("§7Паркуристов на трассах: §b" + Bukkit.getOnlinePlayers().size()));
        }

    }

    
    private static void broadcast(final Player p, final boolean cheat) {
        
        String msg;
        if (cheat) {
            msg = "§c - читак!";
        } else {
            msg = switch (Ostrov.random.nextInt(4)) {
                case 0 -> "§7 зашел расслабиться!";
                case 1 -> "§7 пришел пошалить!";
                case 2 -> "§7 прибыл в здание!";
                case 3 -> "§7 готов кайфовать!";
                default -> "";
            };
        } 
        
        Bukkit.broadcast(Component.text("§b" + p.getName() + msg));

        if (cheat) {
            msg = "§c Рабочих читов!";
        } else {
            msg = switch (Ostrov.random.nextInt(4)) {
                case 0 -> "Добро пожаловать!";
                case 1 -> "Приятной игры!";
                case 2 -> "Желаем удачи!";
                case 3 -> "Развлекайтесь!";
                default -> "";
            };
        }
        p.sendPlayerListHeader(Component.text("§7<§bПаркуры§7>\n" + msg));
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
            pl.sendPlayerListFooter( Component.text( "§7Паркуристов на трассах: §b" + (Bukkit.getOnlinePlayers().size()-1) ));
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
            if (USE_ANTICHEAT && p.getGameMode()==GameMode.SURVIVAL && (pd.stageTime<cp.controlTime || pd.stageJump<cp.controlJump || pd.stageFall<cp.controlFall) ) {
                if (!go.cheat) { //до этого не было чита - пометить везде
                    LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `parkData` SET `cheat` = '1' WHERE `name`='"+pd.name+"'");
                    LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `completions` WHERE  `name`='"+pd.name+"' AND `trasseID`='"+t.id+"';");
                }
                go.cheat = true;
                LocalDB.executePstAsync(Bukkit.getConsoleSender(), "INSERT INTO `cheatLog` (name,parkName,log,stamp) VALUES ('"+p.getName()+"', '"+TCUtil.strip(t.displayName)+"', '"+log+"', '"+Timer.getTime()+"');");
            }
//p.sendMessage("§8log: "+(go.cheat?"§cCHEAT!§8 " :"§aOK§8 ")+log);
            go.checkPoint++;
            
            if (t.isLastCp(go.checkPoint)) { //прошел
                t.totalDone++;
                t.totalTime+=go.trasseTime;
                t.totalJumps+=go.trasseJump;
                t.totalFalls+=go.trasseFalls;
                pd.resetTrasse(false); //если не ресануть, плита сразу срабатывает снова
                pd.progress.remove(t.id);
                LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `parkData` WHERE  `hash`='"+go.hash+"';");
                ScreenUtil.sendTitle(p, "", "§fВы прошли паркур!");
                
                CompleteInfo ci = pd.completions.get(t.id);
                if (ci == null) {
                    ci = new CompleteInfo(1, go.trasseTime, go.trasseJump, go.trasseFalls);
                    pd.completions.put(t.id, ci);
                } else {
                    ci.done++;
                    ci.trasseTime = go.trasseTime;
                    ci.trasseJump = go.trasseJump;
                    ci.trasseFalls = go.trasseFalls;
                }
                
                if (pd.cheat) {
                    
                    p.sendMessage("§c*Читеры проходят без почестей*");
                    Main.lobbyPlayer(p);
                    //t.saveStat(); на читеров не делаем
                    
                } else {
                    
                    LocalDB.executePstAsync(Bukkit.getConsoleSender(), "INSERT INTO `completions` (hash,name,trasseID,done,time,jump,falls) VALUES ('"
                            +go.hash+"','"+p.getName()+"','"+t.id+"','1','"+go.trasseTime+"','"+go.trasseJump+"','"+go.trasseFalls
                            +"') ON DUPLICATE KEY UPDATE done=done+1,`time`='"+go.trasseTime+"',`jump`='"+go.trasseJump+"',`falls`='"+go.trasseFalls+"';");

                    ApiOstrov.moneyChange(p, t.pay, "Паркуры");
                    ApiOstrov.addStat(p, Stat.PA_done);
                    PM.getOplayer(pd.name).score.getSideBar().reset();
                    p.getInventory().setItem(0, new ItemStack(Material.AIR));
                    p.getInventory().setItem(2, new ItemStack(Material.AIR));
                    p.getInventory().setItem(4, new ItemStack(Material.AIR));
                    ParticleUtil.spawnRandomFirework(p.getLocation());
                    p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 240, 0));
                    
                    
                    if (!go.cheat) {//тихонько сохраним для нечитеров
                        t.saveStat();
                        final String msg = "§f"+pd.name+(ApiOstrov.isFemale(pd.name)?" §7прошла трассу ":" §7прошел трассу ")+t.displayName+" §7за §e"+TimeUtil.secondToTime(go.trasseTime)+" §7(⇪: §6"+go.trasseJump+"§7, ☠: §4"+go.trasseFalls+"§7)";
                        for (Player pl : Bukkit.getOnlinePlayers()) {
                            pl.sendMessage(msg);
                        }
                    }
                    
                    final String name = p.getName();
                    pd.task = new BukkitRunnable() {
                        int count = 10;
                        @Override
                        public void run() {
                            final Player p = Bukkit.getPlayerExact(name);
                            if (p==null || !p.isOnline()) {
                                this.cancel();
                                return;
                            }
                            ScreenUtil.sendActionBarDirect(p, "§6Возвращение в лобби через: §e"+count);
                            if (count==0) {
                                this.cancel();
                                Main.lobbyPlayer(p);
                            }
                            count--;
                            if (count==14 && t.level==Level.Нормально) ParticleUtil.spawnRandomFirework(p.getLocation());
                            if (count==13 && t.level==Level.Трудно) ParticleUtil.spawnRandomFirework(p.getLocation());
                            if (count==12 && t.level==Level.Нереально) ParticleUtil.spawnRandomFirework(p.getLocation());
                        }
                    }.runTaskTimer(Main.plug, 1, 20);
                    
                }
                
                p.playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
                
                
                
                return;
            }
            

            final CheckPoint next = pd.current.getNextCp(go.checkPoint);
          
            pd.setNextPoint(next);
            p.setCompassTarget(next.getLocation(p.getWorld().getName()));
            
            ScreenUtil.sendTitle(p, "", "§7Чекпоинт §b#" + (go.checkPoint+1)+" §7пройден.");
            p.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1 + ((float) go.checkPoint / (float) t.size()));
            
            pd.saveProgress(t.id);
            ApiOstrov.addStat(p, Stat.PA_chpt);
        }
    }
    
     
   
}
