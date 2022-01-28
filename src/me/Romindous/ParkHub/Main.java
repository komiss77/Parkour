package me.Romindous.ParkHub;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.modules.menuItem.MenuItem;
import ru.komiss77.modules.menuItem.MenuItemBuilder;
import ru.komiss77.modules.world.WorldManager;
import ru.komiss77.objects.CaseInsensitiveMap;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.OstrovConfig;
import ru.komiss77.utils.OstrovConfigManager;
import ru.komiss77.Timer;
import ru.komiss77.utils.inventory.SmartInventory;




public class Main extends JavaPlugin {
    
    public static Main plug;
    public static Location lobby;
    public static OstrovConfigManager configManager;
    public static OstrovConfig parkData;
    public static OstrovConfig parkStat;
    
    public static final HashMap<Integer,Trasse> trasses;
    public static final CaseInsensitiveMap<PD> data;

    public static MenuItem select, stat, exit;
    public static MenuItem suicide, navigator, toStatr, leave;
   
    static {
        trasses = new HashMap<>();
        data = new CaseInsensitiveMap<>();
    }

    
    
    
    @Override
    public void onEnable() {
        
        plug = this;
        configManager = new OstrovConfigManager(this);
        
        lobby = Bukkit.getWorlds().get(0).getSpawnLocation();
        lobby.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        lobby.getWorld().setTime(6000);
        
        parkData = configManager.getNewConfig("parkData.yml");
        parkStat = configManager.getNewConfig("parkStat.yml");
        

       
        if (parkData.getConfigurationSection("trasses")!=null) {
            
            String mapPath;
            String worldName;
            for (final String id : parkData.getConfigurationSection("trasses").getKeys(false)) {
                mapPath = "trasses."+id+".";
                
                worldName = parkData.getString(mapPath+"worldName");
                World w = Bukkit.getWorld(worldName);
                if (w==null) {
                    w = ApiOstrov.getWorldManager().load(Bukkit.getConsoleSender(), worldName, World.Environment.NORMAL, WorldManager.Generator.Empty);
                }
                if (w==null) {
                    Ostrov.log_err("Мир "+worldName+" для паркура "+id+" не создан!");
                    continue;
                }

                
                final LinkedList<CheckPoint> points = new LinkedList<>();
                String[]split;
                for (String cpString : parkData.getStringList(mapPath+"points")) {
                    split = cpString.split(",");
                    final CheckPoint cp = new CheckPoint(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]),
                                                            Integer.parseInt(split[4]), Integer.parseInt(split[5]), Integer.parseInt(split[6]));

                    points.add(cp);
		}
                final Trasse tr = new Trasse( Integer.parseInt(id), parkData.getString(mapPath+"displayName"), worldName, points);
                
                tr.mat = Material.matchMaterial(parkData.getString(mapPath+"mat"));
                if (tr.mat==null) {
                    tr.mat=Material.BEDROCK;
                }
                tr.descr = parkData.getString(mapPath+"descr");
                tr.pay = parkData.getInt(mapPath+"pay");
                tr.level = Level.valueOf(parkData.getString(mapPath+"level"));
                tr.disabled = parkData.getBoolean(mapPath+"disabled");
                tr.creator = parkData.getString(mapPath+"creator","§8аноним");
                tr.createAt = parkData.getInt(mapPath+"createAt", Timer.getTime());
                
                trasses.put(tr.id, tr);
                
                mapPath = "stat."+id+".";
                if (parkStat.getConfigurationSection(mapPath)!=null) {
                    tr.totalDone = parkStat.getInt(mapPath+"totalDone",0);
                    tr.totalTime = parkStat.getInt(mapPath+"totalTime",0);
                    tr.totalJumps = parkStat.getInt(mapPath+"totalJumps",0);
                    tr.totalFalls = parkStat.getInt(mapPath+"totalFalls",0);
                }
                
                
            }
            
//for (Trasse t:trasses.values()) {
    //t.saveConfig();
    //t.saveStat();
//}
            
            Ostrov.log_ok("Загружено паркуров: "+trasses.size());
        }


        
        createMenuItems();
        
        
        new BukkitRunnable() {
            @Override
            public void run() {
                Progress go;
                
                for (PD pd : data.values()) {
                    
                    if (pd.tick%20==0) {
                        
                        final Player p = pd.getPlayer();
                        
                        if (p==null | !p.isOnline()) continue;
                        
                        if (pd.current!=null) {
                            
                            if (pd.current.disabled) {
                                p.sendMessage("§cАй-ай, кто-то выключил эту трассу!");
                                lobbyPlayer(p); //pd.current = null там есть
                                continue;
                            }
                            go = pd.getProgress(pd.current.id);
                            go.trasseTime++;
                            pd.stageTime++;
                            pd.totalTime++;
                            
                            if (pd.current.isLastCp(go.checkPoint)) {
                                //финиш
                                //табло удаляется на финише в MainLis
                            } else {
                                gameScore(pd, getDistance(p, pd));
                            }
                            
                        }
                        
                        
                    }
                    
                    pd.tick++;
                }
            }
        }.runTaskTimer(plug, 1, 1);
        
        
        getServer().getPluginManager().registerEvents(new ListenerPlayer(), this);
        getServer().getPluginManager().registerEvents(new ListenerWorld(), this);
        //getServer().getPluginManager().registerEvents(new InterLis(), this);
        //getServer().getPluginManager().registerEvents(new InventLis(), this); 
        
        getCommand("pk").setExecutor(new PrkCmd());
        
        getServer().getConsoleSender().sendMessage("§2ParkHub is ready!");
    }
    
    private int getDistance(final Player p, final PD pd) {
        return (Math.abs(p.getLocation().getBlockX() - pd.nextCpX) + Math.abs(p.getLocation().getBlockY() - pd.nextCpY) + Math.abs(p.getLocation().getBlockZ() - pd.nextCpZ))/3;
    }      
   // private static int square(final int num) {
   //     return num * num;
   // }    

        
        
    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("§2ParkHub is off!");
    }
	

        
        

        
    public static void lobbyPlayer(final Player p) {
        p.closeInventory();
        p.teleport(lobby);
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        p.setHealth(20);
        p.setGameMode(GameMode.SURVIVAL);
        p.setFireTicks(0);
        for (final PotionEffect ef : p.getActivePotionEffects()) {
            p.removePotionEffect(ef.getType());
        }

        p.getInventory().clear();
        select.giveForce(p);//p.getInventory().setItem(1, mkItm(Material.COMPASS, "§3Выбор Паркура"));
        //stat.giveForce(p);//p.getInventory().setItem(4, mkItm(Material.MAP, "§9Статистика", "§bПКМ §7- Топ статистика", p.hasPermission("ostrov.builder") ? "§3Шифт + ПКМ §7- Создание карты" : ""));
        exit.giveForce(p);//p.getInventory().setItem(7, mkItm(Material.MAGMA_CREAM, "§4Выход"));

        //!! При первом тп в лобби после BungeeDataRecieved PD не будет!!!
        final PD pd = data.get(p.getName());
        if (pd!=null) {
            pd.resetTrasse();
            lobbyScore(p);
        }

        p.setCompassTarget(Main.lobby);
    }

        
    
    
    
    
    
    
    
    
    
    
    
    

    public static void joinParkur(final Player p, final int pkId) {

        final Trasse tr = Main.trasses.get(pkId);
        if (tr==null) {
            p.sendMessage("§cНет паркура "+pkId+"!");
            return;
        }
        if (tr.disabled) {
            p.sendMessage("§cПаркур "+tr.displayName+" §cвыключен!");
            return;
        }
        final PD pd = data.get(p.getName());
        if (pd==null) {
            p.sendMessage("§cДанные паркуриста не загружены!");
            return;
        }
        
        pd.current = tr;
        tr.inProgress.add(pd.name);
        
        final Progress go = pd.getProgress(pkId); //null не будет
        //если достигнута последняя точка ?? -предварительно сбрасывать при выботе карты, если пройден!
        if (tr.isCompleted(pd)) { // если пройден - сбрасывать!
            go.reset();
            pd.saveProgress(tr.id);
        }
        
        final CheckPoint next = pd.current.getNextCp(go.checkPoint);
        pd.setNextPoint(next);//постав.коорд.след.чекпоинта
        
        p.closeInventory();
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        p.setHealth(20);
        p.setGameMode(GameMode.SURVIVAL);
        p.setFireTicks(0);
        
        p.getInventory().clear();
        suicide.giveForce(p);
        navigator.giveForce(p);//p.getInventory().setItem(4, mkItm(Material.MAP, "§9Статистика", "§bПКМ §7- Топ статистика", p.hasPermission("ostrov.builder") ? "§3Шифт + ПКМ §7- Создание карты" : ""));
        toStatr.giveForce(p);
        leave.giveForce(p);
        
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        for (PotionEffect ef : p.getActivePotionEffects()) {
            p.removePotionEffect(ef.getType());
        }
        
        final CheckPoint cp = pd.current.getCp(go.checkPoint);
        
        final Location loc = cp.getLocation(tr.worldName);
        final Location nextLoc = next.getLocation(tr.worldName);
        
        //if (cp.bf!=BlockFace.SELF) {
            //loc.setDirection(cp.bf.getDirection());
        //} else {
            loc.setDirection(nextLoc.toVector().subtract(loc.toVector()));
        //}
        
        p.teleport(loc);
        
        p.setCompassTarget(nextLoc);
        
//p.sendMessage("§8log: curr="+pd.current.getCp(go.checkPoint));
//p.sendMessage("§8log: next="+next);
        p.sendMessage(go.checkPoint==0? "§fВы на старте паркура "+tr.displayName : "§fВы на чекпоинте #"+go.checkPoint);
        p.sendMessage("§6Ваш вгляд направлен на цель.");
        
        p.playerListName(Component.text("§7[§3"+tr.displayName+"§7] "+p.getName()+(pd.cheat ? " §4[§cЧИТЫ§4]" :"") ));
        if (PM.nameTagManager!=null) {
            PM.nameTagManager.setNametag(p, pd.cheat ? "§4[§cЧитак§4]" : "§7[§3"+getRank(pd.totalCheckPoints)+"§7] ", ""); //final Parkour ar = Parkour.getPlPark(other.getName());
        }
    }


        

    
    
    
    
    public static void openTop(final Player p, final Trasse t) {
        p.closeInventory();
        
        Ostrov.async( ()-> {
                final List<ItemStack> topList = new ArrayList<>();
                
                Statement stmt = null;
                ResultSet rs = null;
                
                try { 
                    stmt = ApiOstrov.getLocalConnection().createStatement();

                    rs = stmt.executeQuery( "SELECT *  FROM `playerData` WHERE `trasseID`='"+t.id+"' AND `done`>'0' AND `cheat`='0' ORDER BY `trasseTime` ASC LIMIT 24" );
                    
                    int place = 1;
                    while (rs.next()) {
                        topList.add( getTopIcon(place, rs) );
                        place++;
                    }
                    
                            
                    Ostrov.sync( ()-> {
                        SmartInventory.builder().id("Top паркура "+t.displayName)
                                .provider(new TopMenu(topList))
                                .size(6, 9)
                                .title("Top паркура "+t.displayName)
                                .build()
                                .open(p);
                    }, 5);
                    
                    
                } catch (SQLException e) { 

                    Ostrov.log_err("§сзагрузка топ - "+e.getMessage());

                } finally {
                    try{
                        if (rs!=null) rs.close();
                        if (stmt!=null) stmt.close();
                    } catch (SQLException e) {
                        Ostrov.log_err("§сзагрузка топ2 - "+e.getMessage());
                    }
                }
            }, 0);
    }
    
    public static ItemStack getTopIcon(final int place, final ResultSet rs ) throws SQLException {
        

    return new ItemBuilder( place==1 ? Material.DIAMOND : 
                place<=3 ? Material.EMERALD :
                place<=6 ? Material.GOLD_INGOT :
                place<=10 ? Material.IRON_INGOT :
                place<=15 ? Material.BRICK : Material.FLINT
        
        )
            .name("§f"+rs.getString("name"))
            .lore("§7")
            .lore("§7Место : §b"+place)
            .lore("§7⌚ : §f"+ApiOstrov.secondToTime(rs.getInt("trasseTime")))
            .lore("§7⇪ : §6"+rs.getInt("trasseJump"))
            .lore("§7☠: §c"+rs.getInt("trasseFalls"))
            .lore("§7")
            .build();
        
    }    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
        

    public static void lobbyScore(final Player p) {
        final PD pd = data.get(p.getName());
        if (PM.nameTagManager!=null) {
                PM.nameTagManager.setNametag(p, "§7[§5ЛОББИ§7] ", pd.cheat ? " §4[§cЧИТЫ§4]" :"");
            }
        p.playerListName(Component.text("§7[§5ЛОББИ§7] "+p.getName()+(pd.cheat ? " §4[§cЧИТЫ§4]" :"") ));
        final Oplayer op = PM.getOplayer(p.getName());

        op.score.getSideBar().setTitle("§7[§3Паркуры§7]");
        op.score.getSideBar().updateLine(13, "");
        op.score.getSideBar().updateLine(12, "§bЛобби");
        op.score.getSideBar().updateLine(11, "");
        op.score.getSideBar().updateLine(10, "§7Ваша статистика: ");
        op.score.getSideBar().updateLine(9, "§7⌚: "+ApiOstrov.secondToTime(pd.totalTime));
        op.score.getSideBar().updateLine(8, "§7☠: "+pd.totalFalls);
        op.score.getSideBar().updateLine(7, "§7⇪: "+pd.totalJumps);
        op.score.getSideBar().updateLine(6, "§7⚐: §b"+pd.totalCheckPoints);
        op.score.getSideBar().updateLine(5, "§7Средние падения за");
        op.score.getSideBar().updateLine(4, "§7чекп.: §3"+pd.stageFall+" §7/ §3"+pd.totalCheckPoints+"§7 = §3"+(pd.totalCheckPoints>0 ? ((int)pd.totalFalls/pd.totalCheckPoints) : 0) );
        //op.score.getSideBar().updateLine(5, ChatColor.GRAY + "чекп.: " + ChatColor.DARK_AQUA + (exst ? String.valueOf(rs.getInt("TFLS")) + ChatColor.GRAY + " / " + ChatColor.DARK_AQUA + rs.getInt("TCPTS") + ChatColor.GRAY + " = " + ChatColor.DARK_AQUA + lmRslt(rs.getFloat("TFLS") / rs.getFloat("TCPTS"), 4) : 0));
        op.score.getSideBar().updateLine(3, "§7Ранг: §b" + getRank(pd.totalCheckPoints));
        op.score.getSideBar().updateLine(2, "§7До след. ранга: §b" + (getToNxtRnk(pd.totalCheckPoints)));
        op.score.getSideBar().updateLine(1, "");

    }

    
    
    public void gameScore(final PD pd, int distance) {
        final Oplayer op = PM.getOplayer(pd.name);
        final Progress go = pd.getProgress(pd.current.id);
        
        op.score.getSideBar().setTitle(pd.current.displayName+" §7[§f"+pd.current.inProgress.size()+"§7]");
        op.score.getSideBar().updateLine(13, "");
        op.score.getSideBar().updateLine(12, "§7⌚: §3"+ApiOstrov.secondToTime(go.trasseTime));
        op.score.getSideBar().updateLine(11, "");
        op.score.getSideBar().updateLine(10, "§7⇪: §6"+go.trasseJump);
        op.score.getSideBar().updateLine(9, "");
        op.score.getSideBar().updateLine(8, "§7☠: §c"+go.trasseFalls);
        op.score.getSideBar().updateLine(7, "");
        op.score.getSideBar().updateLine(6,"§7⚐:"+ (go.checkPoint==0 ? "§fНачало" : "§b"+(go.checkPoint+1)+" §7из §b"+(pd.current.size())) );
        op.score.getSideBar().updateLine(5, "");
        op.score.getSideBar().updateLine(4, pd.current.isLastCp(go.checkPoint+1) ? "§aДо финиша§7:" : "§7До след. точки:");
        op.score.getSideBar().updateLine(3, distance==0 ? "§3пара шагов" : "§3~"+distance+"m.");
        op.score.getSideBar().updateLine(2, "");
        op.score.getSideBar().updateLine(1, "");


    }   
  
        
        
        
        
    private static int getToNxtRnk(final int ch) {
            switch (ch / 100) {
            case 0:
                    return 100-ch;
            case 1:
                    return 200-ch;
            case 2:
            case 3:
                    return 300-ch;
            case 4:
            case 5:
            case 6:
                    return 600-ch;
            case 7:
            case 8:
            case 9:
            case 10:
                    return 1000-ch;
            default:
                    return 10000;
            }
    }

    public static String getRank(final int totalCheckPoints) {
        switch (totalCheckPoints / 100) {
        case 0:
            return "Начинающий";
        case 1:
        case 2:
            return "Любитель";
        case 3:
        case 4:
            return "Продвинутый";
        case 5:
        case 6:
        case 7:
            return "Умелец";
        case 8:
        case 9:
        case 10:
            return "Мастер";
        case 11:
        case 12:
        case 13:
            return "Архонт";
        case 14:
        case 15:
        default:
            return "Спэрма";
        }
    }




        
        
    private void createMenuItems() {
        final ItemStack is=new ItemBuilder(Material.TARGET)
            .setName("§3Выбор Паркура")
            .build();
        select = new MenuItemBuilder("select", is)
            .slot(1) //Chestplate
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .leftClickCmd("menu")
            .rightClickCmd("pk")
            .create();
        
        final ItemStack is2=new ItemBuilder(Material.MAP)
            .setName("§9Статистика")
            .lore("§bПКМ §7- Топ статистика")
            .build();
        stat = new MenuItemBuilder("stat", is2)
            .slot(4) //Chestplate
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .rightClickCmd("pk stat")
            .create();
        
        final ItemStack is3=new ItemBuilder(Material.MAGMA_CREAM)
            .setName("§4Выход в лобби")
            .build();
        exit = new MenuItemBuilder("exit", is3)
            .slot(7)
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .rightClickCmd("pk exit")
            .create();
        
        final ItemStack navi=new ItemBuilder(Material.COMPASS)
            .setName("§aНави")
            .lore("§7Всегда показывает")
            .lore("§7направление на след.")
            .lore("§7чекпоинт.")
            .lore("§7ПКМ - подсветить путь")
            .build();
        navigator = new MenuItemBuilder("navigator", navi)
            .slot(0)
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .rightClickCmd("pk way")
            .create();        
        
        final ItemStack is4=new ItemBuilder(Material.REDSTONE)
            .setName("§3Харакири")
            .build();
        suicide = new MenuItemBuilder("suicide", is4)
            .slot(2)
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .rightClickCmd("pk suicide")
            .create();


        final ItemStack is5=new ItemBuilder(Material.HONEYCOMB)
            .setName("§4Начать сначала")
            .build();
        toStatr = new MenuItemBuilder("toStatr", is5)
            .slot(4)
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .rightClickCmd("pk restart")
            .create();
        
        final ItemStack is6=new ItemBuilder(Material.SLIME_BALL)
            .setName("§cВыйти с Карты")
            .build();
        leave = new MenuItemBuilder("leave", is6)
            .slot(8)
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .rightClickCmd("pk leave")
            .create();
        
    }        	


	

    
    
    

    
    
    
    
    
    
    
}

    

