package me.A5H73Y.Parkour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
//import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import bsign.spigot.Bsign;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Enums.UniversalArenaState;


public class Parkour extends JavaPlugin {

    public static Parkour plugin;
    public static Parkour instance;
    public static final Logger logger = Logger.getLogger("Minecraft");
    
    private static Location lobby;
    public static List inProgress = new ArrayList();  //игроки на паркурах
   // public static List questioned = new ArrayList();  //что-то с подтверждениями
    
    public static HashMap<String,Integer> jumpTotal = new HashMap ();  //хранит прыжки текущие
    public static HashMap<String,Integer> fallTotal = new HashMap ();  //хранит пaдения всего
    public static HashMap<String,Integer> timeTotal = new HashMap ();  //хранит время прохождения когда онлайн
    public static HashMap<String,Integer> jumpCurrent = new HashMap ();  //хранит прыжки текущие 
    public static HashMap<String,Integer> fallCurrent = new HashMap ();  //хранит прыжки всего
    public static HashMap<String,Integer> timeCurrent = new HashMap ();  //хранит время всего
    
    public static HashMap<String, Scoreboard> boards = new HashMap<>();  //scoreboard
    
    private static int signint = 1;
    private static int signintt = 1;
    public static String Prefix;
    public static FileConfiguration config;
    public static MySQL park_mysql;
    


    
@Override
       public void onLoad() {
        instance = this;
    }
     

    @Override
    public void onEnable() {
        Parkour.Prefix = (  "§0[§bПаркур§0] §f" );
        Parkour.plugin = this;
        Parkour.logger.info("[Паркур] Загружен!");
        
        getServer().getPluginManager().registerEvents(new ParkourListener(this), this);
        this.getCommand("pa").setExecutor(new Commands(this));
        
        lobby = Bukkit.getWorlds().get(0).getSpawnLocation();
        
        BukkitTask globalTick = new Sheduler (instance).runTaskTimerAsynchronously(instance, 20, 20);
        

        config = getConfig();
        Files.addDefaulf();
        Files.Create();
        


      //  this.saveUsers();
        saveCourses();
        saveLeaders();
        Files.saveStrings();
        saveUpgrades();
        
        SignUpdate();
        if (config.getBoolean("Database.Use")) {
                park_mysql = new MySQL();
        }
        
////////////////////////////////////////////////////////////////////////////////
            ApiOstrov.sendArenaData(
                    "any",
                    "§1==============",
                    "§b§lП А Р К У Р Ы",
                    "§a§lЗаходите!",
                    "§1==============",
                    "",
                    0,
                    UniversalArenaState.ОЖИДАНИЕ,
                    true,
                    true
            );
////////////////////////////////////////////////////////////////////////////////
        

          
    }

    
   
    
    
 

    @Override
        public void onDisable() {
////////////////////////////////////////////////////////////////////////////////
            ApiOstrov.sendArenaData(
                    "any",
                    "§4█████████",
                    "§b§lП А Р К У Р Ы",
                    "§c§lВыключен...",
                    "§4█████████",
                    "",
                    0,
                    UniversalArenaState.ВЫКЛЮЧЕНА,
                    true,
                    true
            );
////////////////////////////////////////////////////////////////////////////////
        Parkour.logger.info("[Паркур]  Disabled!");
        reloadConfig();
        saveConfig();
        saveCourses();
        saveLeaders();
        Files.saveStrings();
        saveUpgrades();
        
        
        for (Player player : Bukkit.getOnlinePlayers()) {
           String nik = player.getName();
           Files.setIntVar(player, ".JumpTotal", (int) jumpTotal.get(nik));
           Files.setIntVar(player, ".FallTotal", (int) fallTotal.get(nik));
           Files.setIntVar(player, ".TimeTotal", (int) timeTotal.get(nik));
                    jumpTotal.remove(nik);
                    fallTotal.remove(nik);
                    timeTotal.remove(nik);
                    
            
            if (inProgress.contains(nik)) {
                inProgress.remove(nik);

            String current = Files.getStringVar(player, ".Selected");
               if ("".equals(current) | "-".equals(current)) return;
             
             Files.setIntVar(player, ".Progress." + current + ".Time", (int) timeCurrent.get(nik) );
             Files.setIntVar(player, ".Progress." + current + ".Jump", (int) jumpCurrent.get (nik));
             Files.setIntVar(player, ".Progress." + current + ".Falls", (int) fallCurrent.get (nik));
                    timeCurrent.remove(nik);
                    fallCurrent.remove(nik);
                    jumpCurrent.remove(nik);

             

            Files.saveUsers(player);
            }
        }
        
        
    }
        
        

        
        
    public static FileConfiguration GetConfig() {
        return config;
    }


    public static void saveCourses() {
        try {Files.courseData.save(Files.courseFile);
        } catch (IOException ioexception) {}
    }

    
    public static void saveLeaders() {
        try {Files.leaderData.save(Files.leaderFile);
        } catch (IOException ioexception) {}
    }


    public static void saveCheck() {
        try {Files.checkData.save(Files.checkFile);
        } catch (IOException ioexception) {}
    }

    
    public static void saveUpgrades() {
        try {Files.upgData.addDefault("XPRankUpAt", 500);
            Files.upgData.addDefault("XPRankUpMultiplier", 100);
            Files.upgData.options().copyDefaults(true);
            Files.upgData.save(Files.upgFile);
        } catch (IOException ioexception) {}
    }

  /*  
    public static void saveEcon() {
        try {Files.econData.addDefault("Price.Kit", 0);
            Files.econData.options().copyDefaults(true);
            Files.econData.save(Files.econFile);
        } catch (IOException ioexception) {}
    }


    public static void Econ() {
        Files.pluginFolder = plugin.getDataFolder();
        Files.econFile = new File(Files.pluginFolder, "economy.yml");
        Files.econData = new YamlConfiguration();
        if (!Files.econFile.exists()) {
            Parkour.logger.info("[Parkour] Creating economy.yml");

            try {Files.econFile.createNewFile();
                Parkour.logger.info("[Parkour] Done");
                saveEcon();
            } catch (Exception exception) {
                Parkour.logger.info("[Parkour] Failed!");
            }}

        try {Files.econData.load(Files.econFile);
        } catch (IOException | InvalidConfigurationException exception1) {}
    }
*/

    public static String Time(long ms) {
        int hours = (int) (ms );
        int minutes = hours / 60;
        int seconds = minutes / 60;

        hours %= 60;
        minutes %= 60;
        seconds %= 24;
        String hoursn = Integer.toString(hours);
        String minutesn = Integer.toString(minutes);
        String secondsn = Integer.toString(seconds);

        if (hours < 10) {
            hoursn = "0" + hoursn;
        }

        if (minutes < 10) {
            minutesn = "0" + minutesn;
        }

        if (seconds < 10) {
            (new StringBuilder("0")).append(secondsn).toString();
        }

        return minutesn + ":" + hoursn;
    }

    
    public int SimpleTime(long ms) {
        int hours = (int) (ms );
        //int minutes = hours / 60;
       // int seconds = minutes / 60;

        hours %= 60;
       // minutes %= 60;
        //seconds %= 24;
        return hours;
    }

    

    
    
    
    
    
    public static void ToLobby(Player player, String custom) {
        
        player.getActivePotionEffects().stream().forEach((gamemode) -> {    //чистим зельки
            player.removePotionEffect(gamemode.getType());
        });
        
        if (!config.getBoolean("Lobby.Set")) player.sendMessage(Prefix +  "§cЛобби не установлено! Наберите \'/pa setlobby\' !");
         
             player.setGameMode(GameMode.SURVIVAL);
     
             
             
           if (inProgress.contains(player.getName())) {              ///если играем, то сохранить (типа как выход из игры)
                inProgress.remove(player.getName());

            String savedCurse = Files.getStringVar(player, ".Selected");
               if (!"".equals(savedCurse) | !"-".equals(savedCurse) | savedCurse !=null ) {  //если есть активный курс,
             
             Files.setIntVar(player, ".Progress." + savedCurse + ".Time", (int) timeCurrent.get(player.getName()) );
             Files.setIntVar(player, ".Progress." + savedCurse + ".Jump", (int) jumpCurrent.get(player.getName()) );
             Files.setIntVar(player, ".Progress." + savedCurse + ".Falls", (int) fallCurrent.get(player.getName()) );
                    timeCurrent.remove(player.getName());
                    fallCurrent.remove(player.getName());
                    jumpCurrent.remove(player.getName());
               } 
        }
             
             
                        Score.InitScoreboard(player,  "Отдыхаем");


            if ("".equals(custom)) {player.sendMessage("Перемещаемся на спавн!");  //если ну указано лобби, то в начальное
            player.teleport(lobby); return;}
            
            if (!config.contains("Lobby." + custom + ".X")) {                     //если указано несуществуещее лобби, то в начальное
                player.sendMessage(Prefix + "Такого лобби не создано! Перенос в начальное");
                player.teleport(lobby);
                return;}

            int Level = 0;
            if (config.contains("Lobby." + custom + ".Level")) {                  //если лобби требует уровень, берём его
                Level = config.getInt("Lobby." + custom + ".Level");}  
            
            int cur = Files.getIntVar(player, ".Level");  //уровень игрока

            if (!player.hasPermission("Parkour.MinBypass") && cur < Level) {                   //если нет права обхода
                player.sendMessage(Prefix + "§cТребуется уровень §b"+String.valueOf(Level)+ "§c или выше!");
                player.teleport(lobby); return;}

            
           Double posx = config.getDouble("Lobby." + custom + ".X");   //если всё ок, то перенос в уровневое лобби
           Double posy = config.getDouble("Lobby." + custom + ".Y");
           Double posz = config.getDouble("Lobby." + custom + ".Z");
           int posyaw = config.getInt("Lobby." + custom + ".Yaw");
           int pospitch = config.getInt("Lobby." + custom + ".Pitch");
           String lworld = config.getString("Lobby." + custom + ".World");
            
            List worlds = Bukkit.getWorlds();
            Iterator iterator = worlds.iterator();
            while (iterator.hasNext()) {
                World world = (World) iterator.next();
                if (world.getName().equals(lworld)) {
                    Location l = new Location(world, posx, posy, posz, (float) posyaw, (float) pospitch);
                    player.sendMessage(Prefix + "Вы переместились на спавн "+ custom);
                    player.teleport(l);
                }}
            

    }


    
    
    
    
    public static void  CourseStart(Player player, String course) {
        
      String nik=player.getName();
        int checkPoint = Files.getIntVar (player, ".Progress." + course + ".Point");
        
        if ( checkPoint ==0 ) {
           
            if (Files.courseData.contains(course + ".MinimumLevel") && !player.hasPermission("Parkour.MinBypass")) {  //проверка требуемого уровня игрока
                int worldconfig = Files.courseData.getInt(course + ".MinimumLevel");
                int posx = Files.getIntVar (player, ".Progress." + course + ".Level");
                if (posx < worldconfig) {
                  //  player.sendMessage(this.Prefix + Files.stringData.getString("Error.RequiredLvl").replaceAll("%LEVEL%", "" + worldconfig));
                player.sendMessage(Prefix + "§cДля прохождения требуется уровень §b"+worldconfig+ " §cили выше!");
                    return;
                }}


            if (!Files.courseData.getStringList("Courses").contains(course)) {player.sendMessage("Такого паркура не существует!"); return;}

                
                if (config.getBoolean("Other.Display.FinishedError") && !Files.courseData.contains(course + ".Finished")) {  //проверка паркура на готовность
                    if (config.getBoolean("Other.onJoin.forceFinished")) {
                        if (!player.hasPermission("Parkour.Admin")) {
                            player.sendMessage(Prefix + "§cТрасса ещё не доделана!");
                            return;
                        }} else {player.sendMessage(Prefix + "§cТрасса ещё не доделана!");return;}}
                
                
                
                if (!inProgress.contains(nik)) inProgress.add(nik);
                player.setHealth(player.getMaxHealth());
                player.setFireTicks(0);
                player.setFallDistance(0.0F);
                jumpCurrent.put(nik, 0);  //ставим таймер прохождения на 0
                fallCurrent.put(nik, 0); 
                timeCurrent.put(nik, 0); 
 
                //int cost = Files.econData.getInt("Price." + course + ".Join");
                
                //if ( cost != 0) {  //если плата за начало, то снимаем
               //     Ostrov.Money_change( player, - cost );
               //     player.sendMessage( cost + " снято с вашего счёта за начало трассы " + course);
               // }

                
                                    //титры и сообщение
                    int s1 = Files.courseData.getInt(course + ".Points");
                    if (s1 == 1) {
                        ApiOstrov.sendTitle(player, "&bНачало трассы &f"+course, "&7Контрольных точек: &f"+Files.courseData.getInt(course + ".Points"));
                    } 
                    

                    
  
                Files.setStringVar(player, ".Selected", course);
                Files.setStringVar(player,  ".Progress." + course, course);
                Files.setIntVar(player,  ".Progress." + course + ".Point", 0);
                Files.setIntVar(player,  ".Progress." + course + ".Falls", 0);
                Files.setIntVar(player,  ".Progress." + course + ".Time", 0);
                Files.setIntVar(player,  ".Progress." + course + ".Jump", 0);
                Files.setIntVar(player,  ".Progress." + course + ".BeginAt", (int) (System.currentTimeMillis()/1000));
                Files.setIntVar(player,  ".Progress." + course + ".EndAt", 0);
                Files.setIntVar(player,  ".Progress." + course + ".Time", 0);
            Files.saveUsers(player);                
                
                    
                int i = Files.courseData.getInt(course + ".Views"); ++i; Files.courseData.set(course + ".Views", i); //добавляем счётчик проходивших
                

                saveCourses();
                
                player.sendMessage(Prefix + "Начало пути §b"+ course);  //начинаем проходить
                                        //тп на старт
                String s = Files.courseData.getString(course + ".World");
                Double posx = Files.courseData.getDouble(course + ".0.X");
                Double posy = Files.courseData.getDouble(course + ".0.Y");
                Double posz = Files.courseData.getDouble(course + ".0.Z");
                int posyaw = Files.courseData.getInt(course + ".0.Yaw");
                int pospitch = Files.courseData.getInt(course + ".0.Pitch");
                Location start = new Location(plugin.getServer().getWorld(s), posx, posy, posz, (float) posyaw, (float) pospitch);
                player.teleport(start);
                
                
        int cps = Files.courseData.getInt(course + ".Points");
        int wiev = Files.courseData.getInt(course + ".Views");
        int complete = Files.courseData.getInt(course + ".Completed");
        
        Score.UpdateScoreboard(player, course, 0, cps, wiev, complete);        
                
                
    } else {
            Files.setStringVar(player, ".Selected", course);
            Files.saveUsers(player);
            Continue (player, course);
        }
    
    }

 
    
    
    
    public static void Fall (Player player) {
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
        player.setFallDistance(0.0F);
         String nik=player.getName();
       
         player.getActivePotionEffects().stream().forEach((arenain) -> {
             player.removePotionEffect(arenain.getType());
        });
        
        if (!inProgress.contains(nik)) {player.sendMessage(Prefix + "Вы не проходите паркур!");return;}
        
        String course =  Files.getStringVar(player, ".Selected");
        if ("".equals(course)) {player.sendMessage(Prefix + "Непредвиденная ошибка! В лобби!");player.teleport(lobby);return;}
        
       // int fall = Files.getIntVar (player, ".Progress." + course + ".Falls");                //i=падения
       // ++fall;
       //  Files.setIntVar(player, ".Progress." + course + ".Falls", fall);  //добавить падение в файл
        //int fallTotal = (int) this.fallTotal.get(player.getName());fallTotal++;this.fallTotal.put(player.getName(), fallTotal);
       // int fallCurrent = (int) this.fallCurrent.get(player.getName());fallCurrent++;this.fallCurrent.put(player.getName(), fallCurrent);
        
       // String playerpoint = Integer.toString(Files.getIntVar (player,  ".Progress." + course + ".Point"));  //берём точку
        int playerpoint = Files.getIntVar (player,  ".Progress." + course + ".Point");  //берём точку
        String worldconfig = Files.courseData.getString(course + ".World");                                  //берём мир курса
        Double posx = Files.courseData.getDouble(course + "." + playerpoint + ".X");         //берём координаты контр точки
        Double posy = Files.courseData.getDouble(course + "." + playerpoint + ".Y");         //
        Double posz = Files.courseData.getDouble(course + "." + playerpoint + ".Z");         //
        int posyaw = Files.courseData.getInt(course + "." + playerpoint + ".Yaw");                           //
        int pospitch = Files.courseData.getInt(course + "." + playerpoint + ".Pitch");                       //
                                                                                                       //тп на контр точку
          Location l = new Location(plugin.getServer().getWorld(worldconfig), posx, posy, posz, (float) posyaw, (float) pospitch);
          player.teleport(l);

                


        
        if (Files.courseData.contains(course + ".MaxDeaths")) {  //проверка на лимит падений в этом паркуре
            //int j = Files.courseData.getInt(course + ".MaxDeaths");
            if ( (int) fallCurrent.get(player.getName()) == Files.courseData.getInt(course + ".MaxDeaths")) {
                player.sendMessage(Prefix + Files.stringData.getString("Parkour.MaxDeaths").replaceAll("%AMOUNT%", String.valueOf(fallCurrent)));
                CourseLeave(player, "");           //елм лимит, то пошел вон
                return;
            }
        }

        
       // Integer integer = Integer.valueOf(playerpoint);                          //берём номер сохранённой точки
       // if (integer == 0) {                                           //если нулевая, сброс таймера 
        if (playerpoint == 0) {                                           //если нулевая, сброс таймера 
                player.sendMessage(Prefix + Files.stringData.getString("Parkour.Die1") + " &fТаймер сброшен.");
                timeCurrent.put(nik, 0);
                Files.setIntVar(player, ".Progress." + course + ".BeginAt", (int) (System.currentTimeMillis()/1000));
        }                       //если не нулевая, сообщ о переносе в точку     
            player.sendMessage(Prefix + Files.stringData.getString("Parkour.Die2").replaceAll("%POINT%", Integer.toString(playerpoint) ));

        

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BIG_FALL , 1.0F, 1.0F);

          //  Score.AddFall(player);
        int cps = Files.courseData.getInt(course + ".Points");
        int wiev = Files.courseData.getInt(course + ".Views");
        int complete = Files.courseData.getInt(course + ".Completed");
 
        int ft = (int) fallTotal.get(nik);
        fallTotal.put ( nik, ft+1 );
        int fc = (int) fallCurrent.get(nik);
        fallCurrent.put ( nik, fc+1 );

        Score.UpdateScoreboard(player, course, playerpoint, cps-playerpoint, wiev, complete);  
           //Files.saveUsers(player);
        
    }

 
    
    
    
    
    public static void Continue (Player player, String course) {
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
        player.setFallDistance(0.0F);
       final String nik = player.getName();
        
       player.getActivePotionEffects().stream().forEach((arenain) -> {
           player.removePotionEffect(arenain.getType());
        });
        
       

        
        if (!inProgress.contains(nik)) inProgress.add(nik);
        
        player.sendMessage(Prefix + Files.stringData.getString("Parkour.Continue").replaceAll("%COURSE%", course));
        ApiOstrov.sendTitle(player, "&bПродолжение трассы: ", "&f "+ course);
                   
        timeCurrent.put(nik, Files.getIntVar (player, ".Progress." + course + ".Time")); //возвращаем таймер
        fallCurrent.put(nik, Files.getIntVar (player, ".Progress." + course + ".Falls")); //возвращаем падения
        jumpCurrent.put(nik, Files.getIntVar (player, ".Progress." + course + ".Jump")); //возвращаем прыжки
        
        int cp = Files.getIntVar (player, ".Progress." + course + ".Point");  //берём точку
        String worldconfig = Files.courseData.getString(course + ".World");                                  //берём мир курса
        Double posx = Files.courseData.getDouble(course + "." + cp + ".X");         //берём координаты контр точки
        Double posy = Files.courseData.getDouble(course + "." + cp + ".Y");         //
        Double posz = Files.courseData.getDouble(course + "." + cp + ".Z");         //
        int posyaw = Files.courseData.getInt(course + "." + cp + ".Yaw");                           //
        int pospitch = Files.courseData.getInt(course + "." + cp + ".Pitch");                       //
                                                                                                       //тп на контр точку
          Location l = new Location(plugin.getServer().getWorld(worldconfig), posx, posy, posz, (float) posyaw, (float) pospitch);
          player.teleport(l);

        player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK , 1.0F, 1.0F);

        int cps = Files.courseData.getInt(course + ".Points");
        
        int wiev = Files.courseData.getInt(course + ".Views");
        int complete = Files.courseData.getInt(course + ".Completed");
        
        Score.UpdateScoreboard(player,  course, cp, cps, wiev, complete);

        
    }
    
    
   
    
    
    
    
    public static void CourseLeave(Player player, String leavedCourse) {
                 String nik=player.getName();

        
        if (!inProgress.contains(nik)) {player.sendMessage(Prefix + "Вы не проходите паркур!");return;}
            
               if ( "".equals(leavedCourse)) leavedCourse =  Files.getStringVar(player,  ".Selected");
               if ("".equals(leavedCourse)) {player.sendMessage(Prefix + "Паркур не указан!");return;}
                 
               
                    inProgress.remove(nik);
                    player.sendMessage(Prefix + Files.stringData.getString("Parkour.Leave").replace("%COURSE%", leavedCourse ));
                    
                    Files.setStringVar(player, ".Selected", "-");
                    Files.setIntVar(player,  ".Progress." + leavedCourse + ".Point", 0);
                    Files.setIntVar(player,  ".Progress." + leavedCourse + ".Falls", 0);
                    Files.setIntVar(player,  ".Progress." + leavedCourse + ".EndAt", (int) (System.currentTimeMillis()/1000));
                    Files.setIntVar(player,  ".Progress." + leavedCourse + ".Time", 0);
                    Files.setIntVar(player,  ".Progress." + leavedCourse + ".Falls", 0);
                    Files.setIntVar(player,  ".Progress." + leavedCourse + ".Jump", 0);
                
                    timeCurrent.remove(nik);
                    fallCurrent.remove(nik);
                    jumpCurrent.remove(nik);
                    
            Files.saveUsers(player);                
                    
                Score.InitScoreboard(player,  "Сломался");
        
                    player.teleport(lobby);
            }
            
 
    
    
    


    public static void CourseFinish(Player player, String course) {
               final String nik = player.getName();

        
        if (!inProgress.contains(nik)) {
            player.sendMessage(Prefix + "Вы не проходите паркур!");
            return;
        }
            
            inProgress.remove(nik);

            player.setHealth(player.getMaxHealth());
            player.setFireTicks(0);
            player.setFallDistance(0.0F);

            
            player.setLevel(Files.getIntVar (player,  ".Level"));   //уровень в бар
            

            //int deathcount = fallCurrent.get(nik);
           // int j = timeCurrent.get(nik);                                   //берём время прохождения
            //сбрасываем таймер
            Bukkit.getOnlinePlayers().stream().forEach((p) -> { //завершаем фунцией оповещения
                p.sendMessage(Prefix + "§3§l"+player.getName()+"§f прошел трассу "+" §5§lза §3"+Time(timeCurrent.get(nik))+"§f , падения: §3"+String.valueOf(fallCurrent.get(nik))+" §f!");
        });     
        
            Leaderboard(player, course, timeCurrent.get(nik), fallCurrent.get(nik));   //что-то с лидерами
            
 
                    Files.setStringVar(player, ".Selected", "-");
                    Files.setIntVar(player,  ".Progress." + course + ".Point", 0);
                    Files.setIntVar(player,  ".Progress." + course + ".Falls", 0);
                    Files.setIntVar(player,  ".Progress." + course + ".EndAt", (int) (System.currentTimeMillis()/1000));
                    Files.setIntVar(player,  ".Progress." + course + ".Time", (int) timeCurrent.get(nik));
                    Files.setIntVar(player,  ".Progress." + course + ".Falls", (int) fallCurrent.get(nik));
                    Files.setIntVar(player,  ".Progress." + course + ".Jump", (int) jumpCurrent.get(nik));
               
                    timeCurrent.remove(nik);
                    fallCurrent.remove(nik);
                    jumpCurrent.remove(nik);

           Files.setIntVar(player, ".JumpTotal", (int) jumpTotal.get(nik));
           Files.setIntVar(player, ".FallTotal", (int) fallTotal.get(nik));
           Files.setIntVar(player, ".TimeTotal", (int) timeTotal.get(nik));


                    
            try { 
                int compl = Files.courseData.getInt(course + ".Completed");           //добавляем счётчик прохождений для курса
                ++compl;
                Files.courseData.set(course + ".Completed", compl);
            } catch (Exception e) {Files.courseData.set(course + ".Completed", 1);}

            Files.saveUsers(player);
            saveLeaders();
            saveCourses();

           if (config.getBoolean("Prize.Use")) Prize(player, course);  //выдаём приз
           
           int rew = Files.courseData.getInt(course + ".Reward");
            if (rew != 0) {          //награда деньгами
                ApiOstrov.moneyChange(player.getName(), rew, "Прохождение паркура");
                player.sendMessage(Prefix + Files.stringData.getString("Parkour.FinishEconomy").replace("%COURSE%", course).replace("%AMOUNT%", String.valueOf(rew) ));
            }

           player.setGameMode(GameMode.ADVENTURE);                  //эффект после прохождения
           player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 180, 0));
            
           
        for (int i = 0; i < 6; ++i) {                           //салютики
            new BukkitRunnable() {
                @Override
                public void run() {
            Random random = new Random();
            Firework firework = (Firework) player.getWorld().spawn(player.getLocation(), Firework.class);
            FireworkMeta fireworkmeta = firework.getFireworkMeta();
            FireworkEffect fireworkeffect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256))).withFade(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256))).with(FireworkEffect.Type.STAR).trail(true).build();

            fireworkmeta.addEffect(fireworkeffect);
            firework.setFireworkMeta(fireworkmeta);   
                }}.runTaskLater(plugin, (long)(i * 5));}
        

            new BukkitRunnable() {                          ///потом в лобби
                @Override
                public void run() {
                     if (player!=null && player.isOnline()) player.teleport(lobby);
                 }}.runTaskLater(plugin, 180);
            
            Score.InitScoreboard(player,  "Закончена!");
            Files.saveUsers(player);  
            
    }

    

 
    
    

    
    
    
    
    
    
    
    
    
    public static void Leaderboard(Player player, String course, int finalTime, int deathcount) {
        
        int leadertime1 = Files.leaderData.getInt(course + ".1.time");
        int leadercount1 = Files.leaderData.getInt(course + ".1.deaths");
        String leadername1 = Files.leaderData.getString(course + ".1.player");
        
        int leadertime2 = Files.leaderData.getInt(course + ".2.time");
        int leadercount2 = Files.leaderData.getInt(course + ".2.deaths");
        String leadername2 = Files.leaderData.getString(course + ".2.player");
        
        int leadertime3 = Files.leaderData.getInt(course + ".3.time");



        if (leadertime1==0 || finalTime < leadertime1) {
            player.sendMessage(Prefix + Files.stringData.getString("Parkour.Win1").replaceAll("%COURSE%", course).replaceAll("%TIME%", Time(finalTime)));
            Files.leaderData.set(course + ".1.time", finalTime);
            Files.leaderData.set(course + ".1.deaths", deathcount);
            Files.leaderData.set(course + ".1.player", player.getName());
            Files.leaderData.set(course + ".2.time", leadertime1);
            Files.leaderData.set(course + ".2.deaths", leadercount1);
            Files.leaderData.set(course + ".2.player", leadername1);
            Files.leaderData.set(course + ".3.time", leadertime2);
            Files.leaderData.set(course + ".3.deaths", leadercount2);
            Files.leaderData.set(course + ".3.player", leadername2);
  
            if (config.getBoolean("Database.Use")) {
                Parkour.logger.log(Level.INFO, "[Parkour] [SQL] Updated the best player for {0}!", course);
                MySQL.updateRecord(course, Time(finalTime), deathcount, player.getName());
            }

            
        } else if (leadertime2==0 || finalTime < leadertime2) {
            player.sendMessage(Prefix + (Files.stringData.getString("Parkour.Win2").replaceAll("%COURSE%", course)));
            Files.leaderData.set(course + ".2.time", finalTime);
            Files.leaderData.set(course + ".2.deaths", deathcount);
            Files.leaderData.set(course + ".2.player", player.getName());
            Files.leaderData.set(course + ".3.time", leadertime2);
            Files.leaderData.set(course + ".3.deaths", leadercount2);
            Files.leaderData.set(course + ".3.player", leadername2);
            
        } else if (leadertime3==0 || finalTime < leadertime3) {
            player.sendMessage(Prefix + (Files.stringData.getString("Parkour.Win3").replaceAll("%COURSE%", course)));
            Files.leaderData.set(course + ".3.time", finalTime);
            Files.leaderData.set(course + ".3.deaths", deathcount);
            Files.leaderData.set(course + ".3.player", player.getName());
            
        } 
        
        
    }




    
    


    public static void SignUpdate() {
        
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(instance, () -> {
            
            if (Parkour.signintt == 3) {
                Parkour.signintt = 1;
                if (Parkour.signint == 3) {
                    Parkour.signint = 1;
                } else {
                    ++Parkour.signint;
                }
            } else {
                ++Parkour.signintt;
            }
            
            int sign = Files.leaderData.getInt("SignNumber") + 1;
            
            for (int currentSign = 1; currentSign < sign; ++currentSign) {
                int x = Files.leaderData.getInt("Signs." + currentSign + ".X");
                int y = Files.leaderData.getInt("Signs." + currentSign + ".Y");
                int z = Files.leaderData.getInt("Signs." + currentSign + ".Z");
                String world = Files.leaderData.getString("Signs." + currentSign + ".World");
                String course = Files.leaderData.getString("Signs." + currentSign + ".Course");
                
                if (world != null) {
                    Block block = Bukkit.getWorld(world).getBlockAt(x, y, z);
                    Block blockbelow = Bukkit.getWorld(world).getBlockAt(x, y - 1, z);
                    
                    block.getChunk().load();
                    //blockbelow.getChunk().load();
                    //BlockState state = block.getState();
                    //BlockState statebelow = blockbelow.getState();
                    Sign s;
                    
                    
                    if (block.getState() instanceof Sign) {
                        s = (Sign) block.getState();
                        s.setLine(0,  "§1§lТОП. Трасса:"  );
                        s.setLine(1,  "§a"+course);
                        s.setLine(2, "§3Пытались: §4" +Files.courseData.getInt(course + ".Views"));
                        s.setLine(3, "§3Точки: §4" + Files.courseData.getInt(course + ".Points"));
                        block.getChunk().load();
                        s.update(true);
                    }
                    
                    if (blockbelow.getState() instanceof Sign) {
                        int time = Files.leaderData.getInt(course + "." + signint + ".time");
                        int fall =  Files.leaderData.getInt(course + "." + signint + ".deaths");
                        
                        s = (Sign) blockbelow.getState();
                        s.setLine(0, "§2§l"+ signint+ " место");
                        s.setLine(1, "§b" + Files.leaderData.getString(course + "." + signint + ".player"));
                        
                        if (time != 0) s.setLine(2, "§6Время: §4" + Time(time));
                        else s.setLine(2, "§6Время: §4"  + "--");
                        
                        if (fall != 0)  s.setLine(3, "§6Падения: §4" + fall);
                        else s.setLine(3, "§6Падения: §4"  + "--");
                        blockbelow.getChunk().load();
                        s.update(true);
                    }
                }
            }
        }, 0L, 200L);
    }

   
    
    public static void GiveXP(Player player, int XP) {
            int currentXP =  Files.getIntVar (player, ".XP");
            int finalXP = currentXP + XP;
            int currentRank =  Files.getIntVar (player, ".Level");

            if (currentXP + XP >= Files.upgData.getInt("XPRankUpAt") + currentRank * Files.upgData.getInt("XPRankUpMultiplier")) {
                Files.setIntVar (player, ".Level", currentRank + 1);
                finalXP = currentXP + XP - (Files.upgData.getInt("XPRankUpAt") + currentRank * Files.upgData.getInt("XPRankUpMultiplier"));
                player.sendMessage(Prefix + "Поздравляем! У Вас повышение уровня: §3" +  (currentRank + 1) +  "§f!");
                ApiOstrov.sendTitle(player, "&6Поздравляем!", "&6Теперь у вас уровень: &f"+(currentRank + 1) );
             }

            Files.setIntVar (player, ".XP", finalXP);
            Files.saveUsers(player);
    }

    public static String GiveInfo(Player p) {
        
        String course = Files.getStringVar(p, ".Selected");
        if ("".equals(course)) course="Отдыхает";
        
        return "§3На трассе: §b"+course;
           // int currentXP =  Files.getIntVar (player, ".XP");
           // int finalXP = currentXP + XP;
           // int currentRank =  Files.getIntVar (player, ".Level");


    }

    
    public static void setXP(Player player, int XP) {
       Files.setIntVar(player, ".XP", XP);
          //  Files.saveUsers(player);
    }

    
    public static void setLevel(Player player, int Level) {
      Files.setIntVar(player, ".Level", Level);
           // Files.saveUsers(player);
    }

    
    
    public static void setRank(Player player, String rank) {
        //Files.usersData.set("PlayerInfo." + player.getName() + ".Rank", Rank);
        Files.setStringVar(player, ".Rank", rank);
          //  Files.saveUsers(player);
    }

    
    //public void Promote(String player, int rank) {
   //     if (Files.usersData.contains("Ranks." + rank) && Files.usersData.getInt("PlayerInfo." + player + ".Rank") < rank) {
   //         Files.usersData.set("PlayerInfo." + player + ".Rank", rank);
    //    }

   // }

    
    
    
    public static void Prize(Player player, String course) {


        int amountstack1;

        if (Files.courseData.contains(course + ".Prize.Amount")) {
            amountstack1 = Files.courseData.getInt(course + ".Prize.Amount");
        } else {
            amountstack1 = config.getInt("Prize.DefaultAmount");
        }

        if (amountstack1 < 1) {
            amountstack1 = 1;
        }


        //if (Files.courseData.contains(course + ".Prize.ID")) {
        //    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.getMaterial(Files.courseData.getInt(course + ".Prize.ID")), amountstack1)});

       // } else {
        //    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.getMaterial(config.getInt("Prize.DefaultID")), amountstack1)});

      //  }

        player.updateInventory();
        int prizexp;

        if (Files.courseData.contains(course + ".XP")) {
            prizexp = Files.courseData.getInt(course + ".XP");
        } else {
            prizexp = config.getInt("Prize.DefaultXP");
        }



      //  player.giveExp(prizexp);
        
       // int XP = Files.courseData.getInt(course + ".XP");

        if (prizexp != 0) {
            GiveXP(player, prizexp);
            if (config.getBoolean("Other.Display.XPReward")) {
                player.sendMessage(Prefix + "Вы получили §b" +  prizexp +  "§f опыта за прохождение §b" +  course);
            }
        }

        int Level = Files.courseData.getInt(course + ".Level");

   //     if (Files.usersData.contains("ServerInfo.Levels." + String.valueOf(Level))) {
   //         String current = Files.usersData.getString("ServerInfo.Levels." + String.valueOf(Level) + ".Rank");
   //         int current1 = Files.usersData.getInt("PlayerInfo." + player.getName() + ".Level");

  //          if (current1 < Level) {
  //              Files.usersData.set("PlayerInfo." + player.getName() + ".Rank", current);
  //          }
  //      }

        if (Files.courseData.contains(course + ".Level")) {
            int current2 = Files.getIntVar(player,  ".Level");

            if (current2 < Level) {
                    player.sendMessage(Prefix + "За прохождение этого паркура ваш уровень повысился до §b"  +  Level);
                ApiOstrov.sendTitle(player,"&6Поздравляем!" ,  "&6За сложный паркур уровень стал: &f"+(Level + 1) );

                 Files.setIntVar(player, ".Level", Level);
            }
        }

            Files.saveUsers(player);
            
       // if (Files.courseData.contains(course + ".Prize.CMD")) {
     //       this.getServer().dispatchCommand(this.getServer().getConsoleSender(), Files.courseData.getString(course + ".Prize.CMD").replaceAll("%PLAYER%", player.getName()));
    //    }

    }

    




 
    
    
    

    
    
    
    
}
