package me.Romindous.ParkHub;


import java.util.HashMap;
import java.util.LinkedList;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
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
import me.Romindous.ParkHub.Listener.MainLis;
import ru.komiss77.Timer;




public class Main extends JavaPlugin {
    
    public static Main plug;
    public static Location lobby;
    public static OstrovConfigManager configManager;
    public static OstrovConfig parkData;
    
    public static final HashMap<Integer,Trasse> trasses;
    public static final CaseInsensitiveMap<PD> data;
    
    //public static final BlockFace[] bfs;

    public static MenuItem select, stat, exit;
    public static MenuItem suicide, toStatr, leave;
   
    static {
        trasses = new HashMap<>();
        data = new CaseInsensitiveMap<>();
        //bfs = new BlockFace[] {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
    }
    
    
    
    
    @Override
    public void onEnable() {
        
        plug = this;
        configManager = new OstrovConfigManager(this);
        
        lobby = Bukkit.getWorlds().get(0).getSpawnLocation();
        lobby.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        lobby.getWorld().setTime(6000);
        
        parkData = configManager.getNewConfig("parkData.yml");
        

       
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
                    final CheckPoint cp = new CheckPoint(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), BlockFace.valueOf(split[3]),
                                                            Integer.parseInt(split[4]), Integer.parseInt(split[5]), Integer.parseInt(split[6]));

                    points.add(cp);
		}
                final Trasse tr = new Trasse( Integer.parseInt(id), parkData.getString(mapPath+"displayName"), worldName, points);
                
                tr.mat = Material.matchMaterial(parkData.getString(mapPath+"mat"));
                if (tr.mat==null) {
                    tr.mat=Material.BEDROCK;
                }
                tr.descr = parkData.getString(mapPath+"descr");
                tr.level = Level.valueOf(parkData.getString(mapPath+"level"));
                tr.pay = parkData.getInt(mapPath+"pay");
                tr.disabled = parkData.getBoolean(mapPath+"disabled");
                tr.totalDone = parkData.getInt(mapPath+"totalDone",0);
                tr.totalTime = parkData.getInt(mapPath+"totalTime",0);
                tr.totalJumps = parkData.getInt(mapPath+"totalJumps",0);
                tr.totalFalls = parkData.getInt(mapPath+"totalFalls",0);
                tr.creator = parkData.getString(mapPath+"creator","§8аноним");
                tr.createAt = parkData.getInt(mapPath+"createAt", Timer.getTime());
                trasses.put(tr.id, tr);
            }
            Ostrov.log_ok("Загружено паркуров: "+trasses.size());
        }


        
        createMenuItems();
        
        
        new BukkitRunnable() {
            @Override
            public void run() {
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
                            pd.getProgress(pd.current.id).trasseTime++;
                            pd.stageTime++;
                            
                            gameScore(pd);
                            
                        }
                        
                        
                    }
                    
                    pd.tick++;
                }
            }
        }.runTaskTimer(plug, 1, 1);
        
        
        getServer().getPluginManager().registerEvents(new MainLis(), this);
        //getServer().getPluginManager().registerEvents(new InterLis(), this);
        //getServer().getPluginManager().registerEvents(new InventLis(), this); 
        
        getCommand("pk").setExecutor(new PrkCmd());
        
        getServer().getConsoleSender().sendMessage("§2ParkHub is ready!");
    }
    

        
        
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
        stat.giveForce(p);//p.getInventory().setItem(4, mkItm(Material.MAP, "§9Статистика", "§bПКМ §7- Топ статистика", p.hasPermission("ostrov.builder") ? "§3Шифт + ПКМ §7- Создание карты" : ""));
        exit.giveForce(p);//p.getInventory().setItem(7, mkItm(Material.MAGMA_CREAM, "§4Выход"));

        final PD pd = data.get(p.getName());
        if (pd!=null) {
            pd.resetTrasse();
            lobbyScore(p.getName());
        }

        p.playerListName(Component.text("§7[§5ЛОББИ§7] "+p.getName())); //p.playerListName(Component.text("§7[§5ЛОББИ§7] " + nm + data.getPerm(nm, "pls")));

        if (PM.nameTagManager!=null) {
            PM.nameTagManager.setNametag(p, "§7[§5ЛОББИ§7] ", ""); //final Parkour ar = Parkour.getPlPark(other.getName());
        }
        
    }

        
    
    
    
    
    
    
    
    
    
    
    
    

    public static void joinParkur(final Player p, final int pkId) {

        final Trasse tr = Main.trasses.get(pkId);
        if (tr==null) {
            p.sendMessage("§cНет паркура "+pkId+"!");
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
        toStatr.giveForce(p);
        leave.giveForce(p);
        
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        for (PotionEffect ef : p.getActivePotionEffects()) {
            p.removePotionEffect(ef.getType());
        }
        
        p.setPlayerListName("§7[§3"+tr.displayName+"§7] "+p.getName());
        
        final CheckPoint cp = pd.current.getCp(go.checkPoint);
        p.teleport(cp.getLocation(tr.worldName));
    }


        

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
        

    public static void lobbyScore(final String name) {

        final Oplayer op = PM.getOplayer(name);
        final PD pd = data.get(name);

        op.score.getSideBar().setTitle("§7[§3Паркуры§7]");
        op.score.getSideBar().updateLine(13, "");
        op.score.getSideBar().updateLine(12, "§7Карта: §bЛобби");
        op.score.getSideBar().updateLine(11, "");
        op.score.getSideBar().updateLine(10, "§7Ваша статистика: ");
        op.score.getSideBar().updateLine(9, "");
        op.score.getSideBar().updateLine(8, "§7Чекп. пройдено: §b"+pd.totalCheckPoints);
        op.score.getSideBar().updateLine(7, "§7Время: "+ApiOstrov.secondToTime(pd.totalTime));
        op.score.getSideBar().updateLine(6, "§7Средние падения за");
        op.score.getSideBar().updateLine(5, "§7чекп.: §3"+pd.stageFall+" §7/ §3"+pd.totalCheckPoints+"§7 = §3"+(pd.totalCheckPoints>0 ? ((int)pd.totalFalls/pd.totalCheckPoints) : 0) );
        //op.score.getSideBar().updateLine(5, ChatColor.GRAY + "чекп.: " + ChatColor.DARK_AQUA + (exst ? String.valueOf(rs.getInt("TFLS")) + ChatColor.GRAY + " / " + ChatColor.DARK_AQUA + rs.getInt("TCPTS") + ChatColor.GRAY + " = " + ChatColor.DARK_AQUA + lmRslt(rs.getFloat("TFLS") / rs.getFloat("TCPTS"), 4) : 0));
        op.score.getSideBar().updateLine(4, "§7Прыжки: "+pd.stageJump);
        op.score.getSideBar().updateLine(3, "§7Ранг: §b" + getRank(pd.totalCheckPoints));
        op.score.getSideBar().updateLine(2, "§7До след. ранга: §b" + (getToNxtRnk(pd.totalCheckPoints)));
        op.score.getSideBar().updateLine(1, "");

    }

    public void gameScore(final PD pd) {
        final Oplayer op = PM.getOplayer(pd.name);

        final Progress go = pd.getProgress(pd.current.id);
        
        op.score.getSideBar().setTitle("§7[§3Паркуры§7]");
        op.score.getSideBar().updateLine(13, "");
        op.score.getSideBar().updateLine(12, "§7Карта: "+pd.current.displayName);
        op.score.getSideBar().updateLine(11, "");
        op.score.getSideBar().updateLine(10, "§7Проходят: "+pd.current.inProgress.size());
        op.score.getSideBar().updateLine(9, "");
        op.score.getSideBar().updateLine(8, "§7Чекпоинт: §b"+go.checkPoint+" §7из §b"+(pd.current.size()) );
        op.score.getSideBar().updateLine(7, "");
        op.score.getSideBar().updateLine(6, "§7Время: "+ApiOstrov.secondToTime(go.trasseTime));
        op.score.getSideBar().updateLine(5, "" );
        op.score.getSideBar().updateLine(4, "§7Падений: "+go.trasseFalls);
        op.score.getSideBar().updateLine(3, "");
        op.score.getSideBar().updateLine(2, "§7Прыжки: "+go.trasseJump);
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

    public static String getRank(final int ch) {
        switch (ch / 100) {
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
        final ItemStack is=new ItemBuilder(Material.COMPASS)
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
            .slot(7) //Chestplate
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .create();
        
        final ItemStack is4=new ItemBuilder(Material.REDSTONE)
            .setName("§3Харакири")
            .build();
        suicide = new MenuItemBuilder("suicide", is4)
            .slot(0) //Chestplate
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .create();
        
        final ItemStack is5=new ItemBuilder(Material.HONEYCOMB)
            .setName("§4В Начало Паркура")
            .build();
        toStatr = new MenuItemBuilder("toStatr", is5)
            .slot(4) //Chestplate
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .create();
        
        final ItemStack is6=new ItemBuilder(Material.SLIME_BALL)
            .setName("§cВыйти с Карты")
            .build();
        leave = new MenuItemBuilder("leave", is6)
            .slot(8) //Chestplate
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .create();
        
    }        	
	

    
    
    

    
    
    
    
    
    
    
}

    
    enum Level {
        Легко (Material.CHAINMAIL_HELMET),
        Нормально (Material.IRON_HELMET),
        Трудно (Material.DIAMOND_HELMET),
        Нереально (Material.NETHERITE_HELMET)
        ;
        
        
        public final Material mat;
        
        private Level (final Material mat) {
            this.mat = mat;
        }
        
        public static Level next (final Level curr) {
            if (curr==null) return Легко;
            switch (curr) {
                case Легко: return Нормально;
                case Нормально: return Трудно;
                case Трудно: return Нереально;
            }
            return null;
        }
        
    }
    

